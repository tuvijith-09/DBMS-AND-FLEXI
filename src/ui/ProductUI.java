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

        if (UserSession.loggedInShopId == 0) {
            JOptionPane.showMessageDialog(null, "Security Alert: Please login first!");
            return;
        }

        int currentShopId = UserSession.loggedInShopId;

        JFrame frame = new JFrame("Warehouse " + currentShopId + " - Inventory Management");
        frame.setSize(850, 550);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField productIdField = new JTextField();
        productIdField.setEditable(false); // FIX

        JTextField catIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField cpField = new JTextField();
        JTextField spField = new JTextField();
        JTextField qtyField = new JTextField();

        inputPanel.add(new JLabel("Product ID (Auto / Update/Delete):"));
        inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Category ID:"));
        inputPanel.add(catIdField);
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Cost Price:"));
        inputPanel.add(cpField);
        inputPanel.add(new JLabel("Selling Price:"));
        inputPanel.add(spField);
        inputPanel.add(new JLabel("Stock Quantity:"));
        inputPanel.add(qtyField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));

        String[] cols = { "Product ID", "Cat ID", "Name", "Cost Price", "Selling Price", "Quantity" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

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

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Product");
        JButton updateBtn = new JButton("Update Product");
        JButton deleteBtn = new JButton("Delete Product");
        JButton refreshBtn = new JButton("Refresh Table");

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        ProductService service = new ProductService();

        refreshBtn.addActionListener(e -> {
            try {
                model.setRowCount(0);
                ResultSet rs = service.getAllProducts(currentShopId);
                while (rs.next()) {
                    model.addRow(new Object[] {
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

        // ADD
        addBtn.addActionListener(e -> {
            try {
                if (InputValidator.isNullOrEmpty(nameField.getText()) ||
                        InputValidator.isNullOrEmpty(catIdField.getText())) {
                    JOptionPane.showMessageDialog(frame, "Name and Category ID required.");
                    return;
                }

                int catId = Integer.parseInt(catIdField.getText());
                String name = nameField.getText().trim();
                double cp = Double.parseDouble(cpField.getText());
                double sp = Double.parseDouble(spField.getText());
                int qty = Integer.parseInt(qtyField.getText());

                // 🔴 Category validation
                if (!service.categoryExists(currentShopId, catId)) {
                    JOptionPane.showMessageDialog(frame, "Invalid Category! Category does not exist.");
                    return;
                }

                if (!validateValues(frame, cp, sp, qty))
                    return;

                Product p = new Product(0, currentShopId, catId, name, cp, sp, qty);

                if (service.add(p)) {
                    JOptionPane.showMessageDialog(frame, "Product Added Successfully!");
                    clearFields(productIdField, catIdField, nameField, cpField, spField, qtyField);
                    refreshBtn.doClick();
                } else {
                    JOptionPane.showMessageDialog(frame, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // UPDATE
        updateBtn.addActionListener(e -> {
            try {
                if (InputValidator.isNullOrEmpty(productIdField.getText())) {
                    JOptionPane.showMessageDialog(frame, "Select product first.");
                    return;
                }

                int productId = Integer.parseInt(productIdField.getText());
                int catId = Integer.parseInt(catIdField.getText());
                String name = nameField.getText().trim();
                double cp = Double.parseDouble(cpField.getText());
                double sp = Double.parseDouble(spField.getText());
                int qty = Integer.parseInt(qtyField.getText());

                if (!service.categoryExists(currentShopId, catId)) {
                    JOptionPane.showMessageDialog(frame, "Invalid Category!");
                    return;
                }

                if (!validateValues(frame, cp, sp, qty))
                    return;

                Product p = new Product(productId, currentShopId, catId, name, cp, sp, qty);

                if (service.update(p)) {
                    JOptionPane.showMessageDialog(frame, "Product Updated!");
                    clearFields(productIdField, catIdField, nameField, cpField, spField, qtyField);
                    refreshBtn.doClick();
                } else {
                    JOptionPane.showMessageDialog(frame, "Update failed.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input!");
            }
        });

        // DELETE
        deleteBtn.addActionListener(e -> {
            if (InputValidator.isNullOrEmpty(productIdField.getText())) {
                JOptionPane.showMessageDialog(frame, "Select product first.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame, "Delete this product?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int productId = Integer.parseInt(productIdField.getText());
                service.delete(productId);
                clearFields(productIdField, catIdField, nameField, cpField, spField, qtyField);
                refreshBtn.doClick();
            }
        });

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        refreshBtn.doClick();
    }

    // VALIDATION
    private static boolean validateValues(JFrame frame, double cp, double sp, int qty) {

        if (cp < 0) {
            JOptionPane.showMessageDialog(frame, "Cost Price cannot be negative!");
            return false;
        }

        if (sp < 0) {
            JOptionPane.showMessageDialog(frame, "Selling Price cannot be negative!");
            return false;
        }

        if (qty <= 0) {
            JOptionPane.showMessageDialog(frame, "Stock must be greater than 0!");
            return false;
        }

        if (sp < cp) {
            JOptionPane.showMessageDialog(frame, "Selling Price must be >= Cost Price!");
            return false;
        }

        return true;
    }

    private static void clearFields(JTextField... fields) {
        for (JTextField f : fields)
            f.setText("");
    }
}