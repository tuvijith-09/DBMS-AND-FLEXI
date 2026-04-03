package ui;

import java.awt.*;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Product;
import service.ProductService;
import util.InputValidator;
import util.UserSession;

public class ProductUI {

    public static void main(String[] args) {

        // Security Check
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
        JTextField catIdField     = new JTextField();
        JTextField nameField      = new JTextField();
        JTextField cpField        = new JTextField();
        JTextField spField        = new JTextField();
        JTextField qtyField       = new JTextField();

        inputPanel.add(new JLabel("Product ID (for Update/Delete):")); inputPanel.add(productIdField);
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

        // ---- Refresh Action ----
        refreshBtn.addActionListener(e -> {
            try {
                model.setRowCount(0);
                ResultSet rs = service.getAllProducts(currentShopId);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("ProductID"),
                        rs.getInt("CategoryID"),
                        rs.getString("Name"),
                        rs.getDouble("CostPrice"),
                        rs.getDouble("SellingPrice"),
                        rs.getInt("Quantity")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Table load error: " + ex.getMessage());
            }
        });

        // ---- Add Action ----
        addBtn.addActionListener(e -> {
            try {
                if (InputValidator.isNullOrEmpty(nameField.getText()) ||
                    InputValidator.isNullOrEmpty(catIdField.getText())) {
                    JOptionPane.showMessageDialog(frame, "Name and Category ID are required.");
                    return;
                }
                int catId = Integer.parseInt(catIdField.getText());
                String name = nameField.getText().trim();
                double cp  = Double.parseDouble(cpField.getText());
                double sp  = Double.parseDouble(spField.getText());
                int qty    = Integer.parseInt(qtyField.getText());

                Product p = new Product(0, currentShopId, catId, name, cp, sp, qty);
                if (service.add(p)) {
                    JOptionPane.showMessageDialog(frame, "Product Added to Warehouse " + currentShopId + "!");
                    clearFields(productIdField, catIdField, nameField, cpField, spField, qtyField);
                    refreshBtn.doClick();
                } else {
                    JOptionPane.showMessageDialog(frame, "Database Error! Check Category ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input: Enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---- Update Action ----
        updateBtn.addActionListener(e -> {
            try {
                if (InputValidator.isNullOrEmpty(productIdField.getText())) {
                    JOptionPane.showMessageDialog(frame, "Select a product from the table first (Product ID required).");
                    return;
                }
                int productId = Integer.parseInt(productIdField.getText());
                int catId     = Integer.parseInt(catIdField.getText());
                String name   = nameField.getText().trim();
                double cp     = Double.parseDouble(cpField.getText());
                double sp     = Double.parseDouble(spField.getText());
                int qty       = Integer.parseInt(qtyField.getText());

                Product p = new Product(productId, currentShopId, catId, name, cp, sp, qty);
                if (service.update(p)) {
                    JOptionPane.showMessageDialog(frame, "Product Updated Successfully!");
                    clearFields(productIdField, catIdField, nameField, cpField, spField, qtyField);
                    refreshBtn.doClick();
                } else {
                    JOptionPane.showMessageDialog(frame, "Update failed. Product may not belong to this shop.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input: Enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---- Delete Action ----
        deleteBtn.addActionListener(e -> {
            if (InputValidator.isNullOrEmpty(productIdField.getText())) {
                JOptionPane.showMessageDialog(frame, "Select a product from the table first.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int productId = Integer.parseInt(productIdField.getText());
                service.delete(productId);
                clearFields(productIdField, catIdField, nameField, cpField, spField, qtyField);
                refreshBtn.doClick();
                JOptionPane.showMessageDialog(frame, "Product deleted.");
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