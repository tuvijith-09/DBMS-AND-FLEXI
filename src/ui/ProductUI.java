package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.Product;
import service.ProductService;

import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class ProductUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Product Management");

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 100, 25);

        JTextField nameField = new JTextField();
        nameField.setBounds(120, 20, 150, 25);

        JButton addBtn = new JButton("Add");
        addBtn.setBounds(300, 20, 80, 25);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(400, 20, 100, 25);

        String[] cols = {"ID", "Name", "Quantity", "Price"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 70, 480, 200);

        ProductService service = new ProductService();

        // Add product
        addBtn.addActionListener(e -> {
            String name = nameField.getText();

            Product p = new Product(0, 1, 1, name, 100, 150, 10);
            service.add(p);

            JOptionPane.showMessageDialog(frame, "Added!");
        });

        // Refresh table
        refreshBtn.addActionListener(e -> {
            try {
                model.setRowCount(0);
                ResultSet rs = service.getAllProducts();

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("ProductID"),
                            rs.getString("Name"),
                            rs.getInt("Quantity"),
                            rs.getDouble("SellingPrice")
                    });
                }

            } catch (Exception ex) {
                System.out.println("Table load error");
            }
        });

        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(addBtn);
        frame.add(refreshBtn);
        frame.add(sp);

        frame.setSize(550, 350);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}