package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import db.DBConnection;
import util.UserSession;

public class LoginUI {

    public static void main(String[] args) {
        // 1. Setup the Login Window
        JFrame frame = new JFrame("SaaS Inventory - Login");
        frame.setSize(400, 300); // Increased height slightly to fit the new password field
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 1, 10, 10)); // Increased rows to 5
        frame.setLocationRelativeTo(null); // Center on screen

        // 2. Create UI Components
        JLabel titleLabel = new JLabel("Multi-Tenant System Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Email Panel
        JPanel emailPanel = new JPanel();
        JLabel emailLabel = new JLabel("Enter Email: ");
        // Pre-filled with Ravi's data for easy testing
        JTextField emailField = new JTextField("ravi@mail.com", 15); 
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        // NEW: Password Panel
        JPanel passPanel = new JPanel();
        JLabel passLabel = new JLabel("Password:   "); // Extra spaces for visual alignment
        // Pre-filled with Ravi's new password for easy testing
        JPasswordField passField = new JPasswordField("ravi_password", 15); 
        passPanel.add(passLabel);
        passPanel.add(passField);

        // Button Panel
        JPanel btnPanel = new JPanel();
        JButton loginBtn = new JButton("Login");
        btnPanel.add(loginBtn);

        // 3. Login Button Action (Database Check)
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            // Securely extract the password from the JPasswordField
            String password = new String(passField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both email and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // JOIN query to find the User based on Email AND Password
            String query = "SELECT u.ShopID, p.FirstName " +
                           "FROM User u " +
                           "JOIN Person p ON u.PersonID = p.PersonID " +
                           "WHERE p.Email = ? AND u.Password = ?";

            // SECURED: Try-with-resources to prevent connection leaks
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {
                 
                ps.setString(1, email);
                ps.setString(2, password);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Success! Grab the data from the database
                        int shopId = rs.getInt("ShopID");
                        String name = rs.getString("FirstName");

                        // Save it to our global session
                        UserSession.startSession(shopId, name);

                        JOptionPane.showMessageDialog(frame, "Welcome " + name + "! You are managing Shop ID: " + shopId);

                        // Close the login window
                        frame.dispose(); 

                        // Open the Main Dashboard
                        MainDashboard.main(new String[0]); 

                    } else {
                        // Triggers if email doesn't exist OR password doesn't match
                        JOptionPane.showMessageDialog(frame, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 4. Add components to frame and display
        frame.add(titleLabel);
        frame.add(emailPanel);
        frame.add(passPanel); // Added the new password panel
        frame.add(btnPanel);
        frame.setVisible(true);
    }
}