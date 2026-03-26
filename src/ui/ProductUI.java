package ui;

import java.awt.*;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Product;
import service.ProductService;
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
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout(10, 10)); 
        frame.setLocationRelativeTo(null); 

        // Top Panel (Inputs) - Notice Shop ID is gone!
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField catIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField cpField = new JTextField();
        JTextField spField = new JTextField();
        JTextField qtyField = new JTextField();

        inputPanel.add(new JLabel("Category ID (1 or 2):")); inputPanel.add(catIdField);
        inputPanel.add(new JLabel("Product Name:"));         inputPanel.add(nameField);
        inputPanel.add(new JLabel("Cost Price (CP):"));      inputPanel.add(cpField);
        inputPanel.add(new JLabel("Selling Price (SP):"));   inputPanel.add(spField);
        inputPanel.add(new JLabel("Stock Quantity:"));       inputPanel.add(qtyField);
        inputPanel.add(new JLabel(""));                      inputPanel.add(new JLabel("")); // Empty placeholders to keep grid neat

        // Center Panel (Table)
        String[] cols = {"Product ID", "Cat ID", "Name", "Cost Price", "Selling Price", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Bottom Panel (Buttons)
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Product");
        JButton refreshBtn = new JButton("Refresh Table");
        btnPanel.add(addBtn);
        btnPanel.add(refreshBtn);

        ProductService service = new ProductService();

        // Refresh Button Action (Securely loads only this warehouse's products)
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

        // Add Button Action (Securely injects the Session ID)
        addBtn.addActionListener(e -> {
            try {
                int catId = Integer.parseInt(catIdField.getText());
                String name = nameField.getText();
                double cp = Double.parseDouble(cpField.getText());
                double sp = Double.parseDouble(spField.getText());
                int qty = Integer.parseInt(qtyField.getText());

                // Auto-inject the Shop ID from the session!
                Product p = new Product(0, currentShopId, catId, name, cp, sp, qty);
                
                if (service.add(p)) {
                    JOptionPane.showMessageDialog(frame, "Product Successfully Added to Warehouse " + currentShopId + "!");
                    nameField.setText(""); cpField.setText(""); spField.setText(""); qtyField.setText("");
                    refreshBtn.doClick(); 
                } else {
                    JOptionPane.showMessageDialog(frame, "Database Error! Make sure Category ID exists.", "Insert Failed", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Validation Error: Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
        refreshBtn.doClick(); 
    }
}