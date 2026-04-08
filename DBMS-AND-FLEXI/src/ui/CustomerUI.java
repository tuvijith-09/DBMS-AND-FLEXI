package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import service.CustomerService;
import util.InputValidator;
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

        // Top Panel (Inputs)
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField fNameField = new JTextField();
        JTextField lNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField cityField = new JTextField();
        
        // SECURITY FIX: Replaced editable JTextField with a strict Dropdown Menu (JComboBox)
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});

        inputPanel.add(new JLabel("First Name:")); inputPanel.add(fNameField);
        inputPanel.add(new JLabel("Last Name:"));  inputPanel.add(lNameField);
        inputPanel.add(new JLabel("Email:"));      inputPanel.add(emailField);
        inputPanel.add(new JLabel("Phone No:"));   inputPanel.add(phoneField);
        inputPanel.add(new JLabel("City:"));       inputPanel.add(cityField);
        inputPanel.add(new JLabel("Status:"));     inputPanel.add(statusCombo); // Added Dropdown

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

        // Refresh Action (SECURED: No Memory Leaks)
        refreshBtn.addActionListener(e -> {
            try {
                model.setRowCount(0); 
                List<Object[]> rows = service.getCustomerTableData(UserSession.loggedInShopId); 
                
                for (Object[] row : rows) {
                    model.addRow(row);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Table load error: " + ex.getMessage());
            }
        });

        // Add Action (Fully Validated)
        addBtn.addActionListener(e -> {
            String fName = fNameField.getText().trim();
            String lName = lNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String city = cityField.getText().trim();
            
            // SECURITY FIX: Read directly from the dropdown menu
            String status = statusCombo.getSelectedItem().toString();
            
            // 1. Check for empty fields (We don't need to check status anymore since the dropdown forces a valid choice)
            if (InputValidator.isNullOrEmpty(fName) || InputValidator.isNullOrEmpty(lName) ||
                InputValidator.isNullOrEmpty(email) || InputValidator.isNullOrEmpty(phone) ||
                InputValidator.isNullOrEmpty(city)) {
                JOptionPane.showMessageDialog(frame, "All text fields are required. Please fill them out.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Validate formats
            if (!InputValidator.isValidName(fName) || !InputValidator.isValidName(lName)) {
                JOptionPane.showMessageDialog(frame, "Names must contain at least 2 characters and only letters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!InputValidator.isValidName(city)) {
                JOptionPane.showMessageDialog(frame, "City name must contain only letters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!InputValidator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!InputValidator.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(frame, "Phone number must be exactly 10 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int currentShopId = UserSession.loggedInShopId;

            Customer c = new Customer(0, fName, lName, email, phone, city, 0, currentShopId, status);
            
            if (service.add(c)) {
                JOptionPane.showMessageDialog(frame, "Customer Added Successfully to Warehouse " + currentShopId + "!");
                fNameField.setText(""); lNameField.setText(""); emailField.setText(""); 
                phoneField.setText(""); cityField.setText(""); 
                
                // Reset dropdown to default "Active"
                statusCombo.setSelectedIndex(0); 
                
                refreshBtn.doClick(); 
            } else {
                JOptionPane.showMessageDialog(frame, "Database Error! Email might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
        refreshBtn.doClick(); 
    }
}