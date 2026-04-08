package ui;

import javax.swing.*;
import java.awt.*;
import service.RegistrationService;
import util.InputValidator;

public class RegisterUI {

    public static void main(String[] args) {
        JFrame frame = new JFrame("SaaS Inventory - Register New Warehouse");
        frame.setSize(450, 450); // Slightly taller to fit everything comfortably
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        frame.setLayout(new GridLayout(8, 2, 10, 10));
        frame.setLocationRelativeTo(null);

        // Input Fields
        JTextField shopNameField = new JTextField();
        JTextField shopCityField = new JTextField();
        JTextField fNameField = new JTextField();
        JTextField lNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passField = new JPasswordField();

        // Add to Frame
        frame.add(new JLabel("  Warehouse/Shop Name:")); frame.add(shopNameField);
        frame.add(new JLabel("  Warehouse City:"));      frame.add(shopCityField);
        frame.add(new JLabel("  Manager First Name:"));  frame.add(fNameField);
        frame.add(new JLabel("  Manager Last Name:"));   frame.add(lNameField);
        frame.add(new JLabel("  Manager Email:"));       frame.add(emailField);
        frame.add(new JLabel("  Manager Phone No:"));    frame.add(phoneField);
        frame.add(new JLabel("  Set Password:"));        frame.add(passField);

        // Buttons
        JButton registerBtn = new JButton("Register & Create");
        JButton cancelBtn = new JButton("Cancel");
        frame.add(registerBtn);
        frame.add(cancelBtn);

        // Cancel Action
        cancelBtn.addActionListener(e -> {
            frame.dispose();
            LoginUI.main(new String[0]); // Go back to login
        });

        // Register Action with Strict Validation
        registerBtn.addActionListener(e -> {
            String shopName = shopNameField.getText().trim();
            String shopCity = shopCityField.getText().trim();
            String fName = fNameField.getText().trim();
            String lName = lNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String pass = new String(passField.getPassword());

            // 1. Check for empty fields
            if (InputValidator.isNullOrEmpty(shopName) || InputValidator.isNullOrEmpty(shopCity) ||
                InputValidator.isNullOrEmpty(fName) || InputValidator.isNullOrEmpty(lName) ||
                InputValidator.isNullOrEmpty(email) || InputValidator.isNullOrEmpty(phone) ||
                InputValidator.isNullOrEmpty(pass)) {
                JOptionPane.showMessageDialog(frame, "All fields are required. Please fill them out.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Validate name formats (Letters only, min 2 chars)
            if (!InputValidator.isValidName(fName) || !InputValidator.isValidName(lName)) {
                JOptionPane.showMessageDialog(frame, "Manager names must contain at least 2 characters and only letters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Validate city format
            if (!InputValidator.isValidName(shopCity)) {
                JOptionPane.showMessageDialog(frame, "City name must contain only letters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 4. Validate email format
            if (!InputValidator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 5. Validate phone format (10 digits)
            if (!InputValidator.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(frame, "Phone number must be exactly 10 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 6. Validate password strength
            if (pass.length() < 6) {
                JOptionPane.showMessageDialog(frame, "Password must be at least 6 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // All validations passed, proceed to database
            RegistrationService service = new RegistrationService();
            boolean success = service.registerNewWarehouse(shopName, shopCity, fName, lName, email, phone, pass);

            if (success) {
                JOptionPane.showMessageDialog(frame, "Warehouse created successfully! You can now log in.");
                frame.dispose();
                LoginUI.main(new String[0]); // Send them back to login
            } else {
                JOptionPane.showMessageDialog(frame, "Registration failed. Email might already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}