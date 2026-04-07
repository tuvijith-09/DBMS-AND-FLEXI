package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import model.Invoice;
import model.InvoiceItem;
import service.InvoiceService;
import util.UserSession;

public class InvoiceUI {

    public static void main(String[] args) {

        if (UserSession.loggedInShopId == 0) {
            JOptionPane.showMessageDialog(null, "Security Alert: Please login first!");
            return;
        }

        int currentShopId = UserSession.loggedInShopId;

        JFrame frame = new JFrame("Warehouse " + currentShopId + " - Billing & Checkout");
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField customerIdField = new JTextField();
        JTextField productIdField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();

        inputPanel.add(new JLabel("Customer ID:"));
        inputPanel.add(customerIdField);
        inputPanel.add(new JLabel("Product ID:"));
        inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Quantity to Sell:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Price Per Item:"));
        inputPanel.add(priceField);

        String[] cols = { "Product ID", "Quantity", "Price", "Subtotal" };
        DefaultTableModel cartModel = new DefaultTableModel(cols, 0);
        JTable cartTable = new JTable(cartModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);

        JPanel btnPanel = new JPanel();
        JButton addToCartBtn = new JButton("Add to Cart");
        JButton generateBillBtn = new JButton("Generate Bill");
        JLabel totalLabel = new JLabel("Total Amount: 0.0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        btnPanel.add(addToCartBtn);
        btnPanel.add(generateBillBtn);
        btnPanel.add(totalLabel);

        // 🔴 ADD TO CART
        addToCartBtn.addActionListener(e -> {
            try {

                if (productIdField.getText().isEmpty() ||
                        quantityField.getText().isEmpty() ||
                        priceField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "All fields are required!");
                    return;
                }

                int prodId = Integer.parseInt(productIdField.getText());
                int qty = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());

                // 🔴 VALIDATIONS
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(frame, "Quantity must be greater than 0!");
                    return;
                }

                if (price <= 0) {
                    JOptionPane.showMessageDialog(frame, "Price must be greater than 0!");
                    return;
                }

                double subtotal = qty * price;

                cartModel.addRow(new Object[] { prodId, qty, price, subtotal });

                // Update total
                double currentTotal = 0;
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    currentTotal += (double) cartModel.getValueAt(i, 3);
                }
                totalLabel.setText("Total Amount: " + currentTotal);

                // Clear fields
                productIdField.setText("");
                quantityField.setText("");
                priceField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter valid numbers!");
            }
        });

        // 🔴 GENERATE BILL
        generateBillBtn.addActionListener(e -> {
            try {

                if (customerIdField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Customer ID is required!");
                    return;
                }

                int customerId = Integer.parseInt(customerIdField.getText());

                if (cartModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(frame, "Cart is empty!");
                    return;
                }

                double finalTotal = 0;
                List<InvoiceItem> itemsList = new ArrayList<>();

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

                InvoiceService service = new InvoiceService();
                boolean success = service.createInvoiceWithItems(invoice, itemsList);

                if (success) {
                    JOptionPane.showMessageDialog(frame, "Bill Generated Successfully!");

                    cartModel.setRowCount(0);
                    customerIdField.setText("");
                    totalLabel.setText("Total Amount: 0.0");

                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Checkout Failed!\nCheck Customer ID, Product ID, or Stock.",
                            "Transaction Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter valid Customer ID!");
            }
        });

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}