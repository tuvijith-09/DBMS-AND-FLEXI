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

        if (UserSession.loggedInShopId == 0) {
            JOptionPane.showMessageDialog(null, "Security Alert: Please login first!");
            return;
        }

        JFrame frame = new JFrame("Warehouse " + UserSession.loggedInShopId + " - Customer Management");
        frame.setSize(850, 500);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField fNameField = new JTextField();
        JTextField lNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField statusField = new JTextField("Active");

        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(fNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lNameField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Phone No:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("City:"));
        inputPanel.add(cityField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusField);

        String[] cols = { "Customer ID", "First Name", "Last Name", "Warehouse Name", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Customer");
        JButton refreshBtn = new JButton("Refresh Table");

        btnPanel.add(addBtn);
        btnPanel.add(refreshBtn);

        CustomerService service = new CustomerService();

        // ---- REFRESH ----
        refreshBtn.addActionListener(e -> {
            try {
                model.setRowCount(0);
                ResultSet rs = service.getAllCustomers(UserSession.loggedInShopId);
                while (rs.next()) {
                    model.addRow(new Object[] {
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

        // ---- ADD CUSTOMER ----
        addBtn.addActionListener(e -> {

            String fName = fNameField.getText().trim();
            String lName = lNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String city = cityField.getText().trim();
            String status = statusField.getText().trim();

            int currentShopId = UserSession.loggedInShopId;

            // 🔴 VALIDATIONS

            if (fName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "First Name is required!");
                return;
            }

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Email is required!");
                return;
            }

            // Simple Email format check
            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(frame, "Invalid Email format!");
                return;
            }

            // 🔴 EMAIL UNIQUENESS CHECK
            if (service.emailExists(email, currentShopId)) {
                JOptionPane.showMessageDialog(frame, "Email already exists in this warehouse!");
                return;
            }

            Customer c = new Customer(0, fName, lName, email, phone, city, 0, currentShopId, status);

            if (service.add(c)) {
                JOptionPane.showMessageDialog(frame, "Customer Added Successfully to Warehouse " + currentShopId + "!");

                // Clear fields
                fNameField.setText("");
                lNameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                cityField.setText("");

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