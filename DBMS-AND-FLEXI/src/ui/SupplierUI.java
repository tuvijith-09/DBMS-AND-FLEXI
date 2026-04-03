package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import model.Supplier;
import service.SupplierService;
import util.UserSession;

public class SupplierUI {

    public static void main(String[] args) {
        if (UserSession.loggedInShopId == 0) {
            JOptionPane.showMessageDialog(null, "Security Alert: Please login first!");
            return;
        }

        JFrame frame = new JFrame("Warehouse " + UserSession.loggedInShopId + " - Supplier Management");
        frame.setSize(850, 500);
        frame.setLayout(new BorderLayout(10, 10)); 
        frame.setLocationRelativeTo(null); 

        // Top Panel (Inputs)
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField fNameField = new JTextField();
        JTextField lNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField companyField = new JTextField();

        inputPanel.add(new JLabel("First Name:"));    inputPanel.add(fNameField);
        inputPanel.add(new JLabel("Last Name:"));     inputPanel.add(lNameField);
        inputPanel.add(new JLabel("Email:"));         inputPanel.add(emailField);
        inputPanel.add(new JLabel("Phone No:"));      inputPanel.add(phoneField);
        inputPanel.add(new JLabel("City:"));          inputPanel.add(cityField);
        inputPanel.add(new JLabel("Company Name:"));  inputPanel.add(companyField);

        // Center Panel (Table)
        String[] cols = {"Supplier ID", "First Name", "Last Name", "Company", "City"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Bottom Panel (Buttons)
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Supplier");
        JButton refreshBtn = new JButton("Refresh Table");
        btnPanel.add(addBtn);
        btnPanel.add(refreshBtn);

        SupplierService service = new SupplierService();

        // Refresh Action
        refreshBtn.addActionListener(e -> {
            try {
                model.setRowCount(0); 
                ResultSet rs = service.getAllSuppliers(UserSession.loggedInShopId); 
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("SupplierID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("CompanyName"),
                            rs.getString("City")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Table load error: " + ex.getMessage());
            }
        });

        // Add Action
        addBtn.addActionListener(e -> {
            String fName = fNameField.getText();
            String lName = lNameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String city = cityField.getText();
            String company = companyField.getText();
            
            int currentShopId = UserSession.loggedInShopId;

            Supplier s = new Supplier(0, fName, lName, email, phone, city, 0, currentShopId, company);
            
            if (service.add(s)) {
                JOptionPane.showMessageDialog(frame, "Supplier Added Successfully!");
                fNameField.setText(""); lNameField.setText(""); emailField.setText(""); 
                phoneField.setText(""); cityField.setText(""); companyField.setText("");
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