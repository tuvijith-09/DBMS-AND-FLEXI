package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import model.Invoice;
import model.InvoiceItem;
import service.InvoiceService;
import util.UserSession;

public class InvoiceUI {

    public static void main(String[] args) {
        // Security Check: Ensure a warehouse manager is logged in
        if (UserSession.loggedInShopId == 0) {
            JOptionPane.showMessageDialog(null, "Security Alert: Please login first!");
            return;
        }

        int currentShopId = UserSession.loggedInShopId;

        // Setup the Main Frame
        JFrame frame = new JFrame("Warehouse " + currentShopId + " - Billing & Checkout");
        frame.setSize(850, 550);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // --- Top Panel: Customer & Product Entry ---
        // Changed to 3 rows since we removed the vulnerable manual price field
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField customerIdField = new JTextField();
        JTextField productIdField = new JTextField();
        JTextField quantityField = new JTextField();

        inputPanel.add(new JLabel("Customer ID:"));
        inputPanel.add(customerIdField);
        inputPanel.add(new JLabel("Product ID:"));
        inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Quantity to Sell:"));
        inputPanel.add(quantityField);

        // --- Center Panel: The Shopping Cart (Table) ---
        String[] cols = {"Product ID", "Quantity", "Unit Price", "Subtotal"};
        DefaultTableModel cartModel = new DefaultTableModel(cols, 0);
        JTable cartTable = new JTable(cartModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);

        // --- Bottom Panel: Buttons and Total ---
        JPanel btnPanel = new JPanel();
        JButton addToCartBtn = new JButton("Add to Cart");
        JButton removeBtn = new JButton("Remove Selected Item");
        JButton generateBillBtn = new JButton("Generate Bill");
        JLabel totalLabel = new JLabel("Total Amount: 0.0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        btnPanel.add(addToCartBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(generateBillBtn);
        btnPanel.add(totalLabel);

        // --- Actions ---

        // 1. Add to Cart Action (Now fully validated against the Database)
        addToCartBtn.addActionListener(e -> {
            try {
                int customerId = Integer.parseInt(customerIdField.getText().trim());
                int prodId = Integer.parseInt(productIdField.getText().trim());
                int qty = Integer.parseInt(quantityField.getText().trim());

                if (qty <= 0) {
                    JOptionPane.showMessageDialog(frame, "Quantity must be greater than 0.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate Customer First
                if (!validateCustomerExists(customerId, currentShopId)) {
                    JOptionPane.showMessageDialog(frame, "Customer ID does not exist in this Warehouse.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate Product, Check Stock, and Fetch True Price
                double actualPrice = fetchPriceAndCheckStock(prodId, currentShopId, qty);
                
                if (actualPrice == -1.0) {
                    JOptionPane.showMessageDialog(frame, "Product ID does not exist in this Warehouse.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (actualPrice == -2.0) {
                    JOptionPane.showMessageDialog(frame, "Insufficient stock for Product ID: " + prodId, "Inventory Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // If validations pass, calculate subtotal and add row to the visual cart
                double subtotal = qty * actualPrice;
                cartModel.addRow(new Object[]{prodId, qty, actualPrice, subtotal});

                // Update the visual total
                updateTotalAmount(cartModel, totalLabel);

                // Lock Customer ID field so it can't be tampered with mid-transaction
                customerIdField.setEditable(false);

                // Clear product fields for the next item
                productIdField.setText("");
                quantityField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numeric values for IDs and Quantity.");
            }
        });

        // 2. Remove Selected Item Action (Fixes the "Trap" vulnerability)
        removeBtn.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                cartModel.removeRow(selectedRow);
                updateTotalAmount(cartModel, totalLabel);
                
                // If the cart is empty again, allow them to change the Customer ID
                if (cartModel.getRowCount() == 0) {
                    customerIdField.setEditable(true);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an item from the cart to remove.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 3. Generate Bill Action
        generateBillBtn.addActionListener(e -> {
            if (cartModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(frame, "Cart is empty!");
                return;
            }

            try {
                int customerId = Integer.parseInt(customerIdField.getText().trim());
                double finalTotal = 0;
                List<InvoiceItem> itemsList = new ArrayList<>();

                // Build the list of items from the validated cart
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    int pId = (int) cartModel.getValueAt(i, 0);
                    int qty = (int) cartModel.getValueAt(i, 1);
                    double sub = (double) cartModel.getValueAt(i, 3);
                    
                    finalTotal += sub;
                    
                    InvoiceItem item = new InvoiceItem(0, 0, pId, qty, sub);
                    itemsList.add(item);
                }

                // Create the main Invoice object
                Invoice invoice = new Invoice(0, currentShopId, customerId, finalTotal);

                // Send to the service layer for the database transaction
                InvoiceService service = new InvoiceService();
                boolean success = service.createInvoiceWithItems(invoice, itemsList);

                if (success) {
                    JOptionPane.showMessageDialog(frame, "Bill Generated Successfully! Stock has been updated.");
                    // Reset the UI for the next checkout
                    cartModel.setRowCount(0);
                    customerIdField.setText("");
                    customerIdField.setEditable(true); // Unlock field
                    totalLabel.setText("Total Amount: 0.0");
                } else {
                    JOptionPane.showMessageDialog(frame, "Checkout Failed! A database error occurred during the transaction.", "Transaction Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Customer ID state.");
            }
        });

        // Add everything to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // --- Helper Methods for Database Validation ---

    private static void updateTotalAmount(DefaultTableModel cartModel, JLabel totalLabel) {
        double currentTotal = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            currentTotal += (double) cartModel.getValueAt(i, 3);
        }
        totalLabel.setText("Total Amount: " + currentTotal);
    }

    private static boolean validateCustomerExists(int customerId, int shopId) {
        String query = "SELECT 1 FROM Customer WHERE CustomerID = ? AND ShopID = ?";
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, customerId);
            ps.setInt(2, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // True if customer exists in this shop
            }
        } catch (Exception e) {
            System.out.println("Customer Validation Error: " + e.getMessage());
            return false;
        }
    }

    private static double fetchPriceAndCheckStock(int productId, int shopId, int requiredQty) {
        String query = "SELECT SellingPrice, Quantity FROM Product WHERE ProductID = ? AND ShopID = ?";
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, productId);
            ps.setInt(2, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int availableStock = rs.getInt("Quantity");
                    double price = rs.getDouble("SellingPrice");
                    
                    if (availableStock >= requiredQty) {
                        return price; // Valid
                    } else {
                        return -2.0; // Insufficient stock error code
                    }
                } else {
                    return -1.0; // Product not found error code
                }
            }
        } catch (Exception e) {
            System.out.println("Product Validation Error: " + e.getMessage());
            return -1.0;
        }
    }
}