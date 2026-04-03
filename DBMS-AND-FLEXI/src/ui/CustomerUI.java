package ui;

import java.awt.*;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import service.CustomerService;
import util.UserSession;

public class CustomerUI {

    public static void main(String[] args) {
        // Security Check: Ensure they are logged in
        if (UserSession.loggedInShopId == 0) {
            JOptionPane.showMessageDialog(null, "Security Alert: Please login first!");
            return;
        }

        JFrame frame = new JFrame("Warehouse " + UserSession.loggedInShopId + " - Customer Management");
        frame.setSize(850, 500);
        frame.setLayout(new BorderLayout(10, 10)); 
        frame.setLocationRelativeTo(null); 

        // Top Panel (Inputs) - Notice there is no Shop ID field anymore!
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField fNameField = new JTextField();
        JTextField lNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField statusField = new JTextField("Active");

        inputPanel.add(new JLabel("First Name:")); inputPanel.add(fNameField);
        inputPanel.add(new JLabel("Last Name:"));  inputPanel.add(lNameField);
        inputPanel.add(new JLabel("Email:"));      inputPanel.add(emailField);
        inputPanel.add(new JLabel("Phone No:"));   inputPanel.add(phoneField);
        inputPanel.add(new JLabel("City:"));       inputPanel.add(cityField);
        inputPanel.add(new JLabel("Status:"));     inputPanel.add(statusField);

        // Center Panel (Table)
        String[] cols = {"Customer ID", "First Name", "Last Name", "Warehouse Name", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Bottom Panel (Buttons)
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Customer");
        JButton refreshBtn = new JButton("Refresh Table");
        btnPanel.add(addBtn);
        btnPanel.add(refreshBtn);

        CustomerService service = new CustomerService();

        // Refresh Action (Securely loads only this warehouse's customers)
        refreshBtn.addActionListener(e -> {
            try {
                model.setRowCount(0); 
                // Pass the secure Session ID to the database query
                ResultSet rs = service.getAllCustomers(UserSession.loggedInShopId); 
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
                JOptionPane.showMessageDialog(frame, "Table load error: " + ex.getMessage());
            }
        });

        // Add Action (Securely injects the Session ID)
        addBtn.addActionListener(e -> {
            String fName = fNameField.getText();
            String lName = lNameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String city = cityField.getText();
            String status = statusField.getText();
            
            // Auto-inject the logged-in user's Shop ID!
            int currentShopId = UserSession.loggedInShopId;

            Customer c = new Customer(0, fName, lName, email, phone, city, 0, currentShopId, status);
            
            if (service.add(c)) {
                JOptionPane.showMessageDialog(frame, "Customer Added Successfully to Warehouse " + currentShopId + "!");
                fNameField.setText(""); lNameField.setText(""); emailField.setText(""); 
                phoneField.setText(""); cityField.setText(""); 
                refreshBtn.doClick(); 
            } else {
                JOptionPane.showMessageDialog(frame, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
        refreshBtn.doClick(); 
    }
}