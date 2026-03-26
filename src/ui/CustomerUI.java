package ui;

import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import service.CustomerService;

public class CustomerUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Customer Management");

        // UI Components for a clean top bar
        JButton refreshBtn = new JButton("Load Customers");
        refreshBtn.setBounds(20, 20, 150, 25);

        // Define Table Columns based on our JOIN query
        String[] cols = {"Customer ID", "First Name", "Last Name", "Shop", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 70, 500, 200);

        CustomerService service = new CustomerService();

        // Action when "Load Customers" is clicked
        refreshBtn.addActionListener(e -> {
            try {
                // Clear existing rows first
                model.setRowCount(0); 
                // Fetch joined data
                ResultSet rs = service.getAllCustomers();

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("CustomerID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("ShopName"),
                            rs.getString("Status")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error loading table: " + ex.getMessage());
            }
        });

        // Add components to frame
        frame.add(refreshBtn);
        frame.add(sp);

        frame.setSize(560, 350);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}