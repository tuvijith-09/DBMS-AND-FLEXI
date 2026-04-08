package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Product;
import service.ProductService;
import util.InputValidator;
import util.UserSession;

public class ProductUI {

    public static void main(String[] args) {

        // Security Check: Ensure user is logged in
        if (UserSession.loggedInShopId == 0) {
            JOptionPane.showMessageDialog(null, "Security Alert: Please login first!");
            return;
        }

        int currentShopId = UserSession.loggedInShopId;

        JFrame frame = new JFrame("Warehouse " + currentShopId + " - Inventory Management");
        frame.setSize(850, 550);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // ---- Top Panel: Input Fields ----
        JPanel inputPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField productIdField = new JTextField(); // For update/delete
        productIdField.setEditable(false); // Make ID read-only so users don't break updates
        JTextField catIdField     = new JTextField();
        JTextField nameField      = new JTextField();
        JTextField cpField        = new JTextField();
        JTextField spField        = new JTextField();
        JTextField qtyField       = new JTextField();

        inputPanel.add(new JLabel("Product ID (Auto/Read-Only):")); inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Category ID (1 or 2):"));           inputPanel.add(catIdField);
        inputPanel.add(new JLabel("Product Name:"));                    inputPanel.add(nameField);
        inputPanel.add(new JLabel("Cost Price (CP):"));                 inputPanel.add(cpField);
        inputPanel.add(new JLabel("Selling Price (SP):"));              inputPanel.add(spField);
        inputPanel.add(new JLabel("Stock Quantity:"));                  inputPanel.add(qtyField);
        inputPanel.add(new JLabel(""));                                 inputPanel.add(new JLabel(""));

        // ---- Center Panel: Table ----
        String[] cols = {"Product ID", "Cat ID", "Name", "Cost Price", "Selling Price", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Click on a row to fill fields for editing
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                productIdField.setText(model.getValueAt(row, 0).toString());
                catIdField.setText(model.getValueAt(row, 1).toString());
                nameField.setText(model.getValueAt(row, 2).toString());
                cpField.setText(model.getValueAt(row, 3).toString());
                spField.setText(model.getValueAt(row, 4).toString());
                qtyField.setText(model.getValueAt(row, 5).toString());
            }
        });

        // ---- Bottom Panel: Buttons ----
        JPanel btnPanel = new JPanel();
        JButton addBtn     = new JButton("Add Product");
        JButton updateBtn  = new JButton("Update Product");
        JButton deleteBtn  = new JButton("Delete Product");
        JButton refreshBtn = new JButton("Refresh Table");
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        ProductService service = new ProductService();

        // ---- Refresh Action (SECURED: No Memory Leaks) ----
        refreshBtn.addActionListener(e -> {
            try {
                model.setRowCount(0); // Clear the table first
                List<Object[]> rows = service.getProductTableData(currentShopId);
                
                for (Object[] row : rows) {
                    model.addRow(row);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Table load error: " + ex.getMessage());
            }
        });

        // ---- Add Action (Fully Validated) ----
        addBtn.addActionListener(e -> {
            String catIdStr = catIdField.getText();
            String name = nameField.getText();
            String cpStr = cpField.getText();
            String spStr = spField.getText();
            String qtyStr = qtyField.getText();

            // 1. Check for Empty Fields
            if (InputValidator.isNullOrEmpty(catIdStr) || InputValidator.isNullOrEmpty(name) ||
                InputValidator.isNullOrEmpty(cpStr) || InputValidator.isNullOrEmpty(spStr) ||
                InputValidator.isNullOrEmpty(qtyStr)) {
                
                JOptionPane.showMessageDialog(frame, "All fields are required. Please fill them out.", 
                                              "Validation Error", JOptionPane.WARNING_MESSAGE);
                return; 
            }

            // 2. Validate Formats
            if (!InputValidator.isPositiveInteger(catIdStr)) {
                JOptionPane.showMessageDialog(frame, "Category ID must be a positive integer.");
                return;
            }
            if (!InputValidator.isNonNegativeDouble(cpStr) || !InputValidator.isNonNegativeDouble(spStr)) {
                JOptionPane.showMessageDialog(frame, "Prices must be valid non-negative numbers.");
                return;
            }
            if (!InputValidator.isPositiveInteger(qtyStr) && !qtyStr.equals("0")) {
                JOptionPane.showMessageDialog(frame, "Quantity must be 0 or a positive integer.");
                return;
            }

            // 3. Safe Parsing
            int catId = Integer.parseInt(catIdStr);
            double cp = Double.parseDouble(cpStr);
            double sp = Double.parseDouble(spStr);
            int qty = Integer.parseInt(qtyStr);

            // 4. Business Logic Check
            if (!InputValidator.isSellingPriceValid(cp, sp)) {
                JOptionPane.showMessageDialog(frame, "Selling Price (" + sp + ") cannot be lower than Cost Price (" + cp + ").", 
                                              "Business Logic Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 5. Database Insert
            Product p = new Product(0, currentShopId, catId, name, cp, sp, qty);
            if (service.add(p)) {
                JOptionPane.showMessageDialog(frame, "Product Added to Warehouse " + currentShopId + "!");
                clearFields(productIdField, catIdField, nameField, cpField, spField, qtyField);
                refreshBtn.doClick();
            } else {
                JOptionPane.showMessageDialog(frame, "Database Error! Check Category ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---- Update Action (Fully Validated) ----
        updateBtn.addActionListener(e -> {
            String idStr = productIdField.getText();
            String catIdStr = catIdField.getText();
            String name = nameField.getText();
            String cpStr = cpField.getText();
            String spStr = spField.getText();
            String qtyStr = qtyField.getText();

            // Check if a product is actually selected
            if (InputValidator.isNullOrEmpty(idStr)) {
                JOptionPane.showMessageDialog(frame, "Please select a product from the table first.");
                return;
            }

            // 1. Check for Empty Fields
            if (InputValidator.isNullOrEmpty(catIdStr) || InputValidator.isNullOrEmpty(name) ||
                InputValidator.isNullOrEmpty(cpStr) || InputValidator.isNullOrEmpty(spStr) ||
                InputValidator.isNullOrEmpty(qtyStr)) {
                JOptionPane.showMessageDialog(frame, "All fields are required. Please fill them out.");
                return; 
            }

            // 2. Validate Formats
            if (!InputValidator.isPositiveInteger(catIdStr) || 
                (!InputValidator.isPositiveInteger(qtyStr) && !qtyStr.equals("0")) ||
                !InputValidator.isNonNegativeDouble(cpStr) || 
                !InputValidator.isNonNegativeDouble(spStr)) {
                JOptionPane.showMessageDialog(frame, "Please ensure numeric fields contain valid positive numbers.");
                return;
            }

            // 3. Safe Parsing
            int productId = Integer.parseInt(idStr);
            int catId = Integer.parseInt(catIdStr);
            double cp = Double.parseDouble(cpStr);
            double sp = Double.parseDouble(spStr);
            int qty = Integer.parseInt(qtyStr);

            // 4. Business Logic Check
            if (!InputValidator.isSellingPriceValid(cp, sp)) {
                JOptionPane.showMessageDialog(frame, "Selling Price cannot be lower than Cost Price.", 
                                              "Business Logic Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 5. Database Update
            Product p = new Product(productId, currentShopId, catId, name, cp, sp, qty);
            if (service.update(p)) {
                JOptionPane.showMessageDialog(frame, "Product Updated Successfully!");
                clearFields(productIdField, catIdField, nameField, cpField, spField, qtyField);
                refreshBtn.doClick();
            } else {
                JOptionPane.showMessageDialog(frame, "Update failed. Product may not belong to this shop.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---- Delete Action (Security Fixed) ----
        deleteBtn.addActionListener(e -> {
            if (InputValidator.isNullOrEmpty(productIdField.getText())) {
                JOptionPane.showMessageDialog(frame, "Select a product from the table first.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                int productId = Integer.parseInt(productIdField.getText());
                
                // SECURITY FIX: Passing both ProductID and the logged-in ShopID
                service.delete(productId, currentShopId); 
                
                clearFields(productIdField, catIdField, nameField, cpField, spField, qtyField);
                refreshBtn.doClick();
                JOptionPane.showMessageDialog(frame, "Delete action processed.");
            }
        });

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        refreshBtn.doClick();
    }

    private static void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }
}