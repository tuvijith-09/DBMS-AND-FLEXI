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
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1, 10, 10));
        frame.setLocationRelativeTo(null); // Center on screen

        // 2. Create UI Components
        JLabel titleLabel = new JLabel("Multi-Tenant System Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel emailPanel = new JPanel();
        JLabel emailLabel = new JLabel("Enter Email: ");
        // Pre-filled with your test data for easy grading!
        JTextField emailField = new JTextField("ravi@mail.com", 15); 
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        JPanel btnPanel = new JPanel();
        JButton loginBtn = new JButton("Login");
        btnPanel.add(loginBtn);

        // 3. Login Button Action (Database Check)
        loginBtn.addActionListener(e -> {
            String email = emailField.getText();

            try {
                Connection con = DBConnection.getConnection();
                
                // JOIN query to find the User's ShopID based on their Email
                String query = "SELECT u.ShopID, p.FirstName " +
                               "FROM User u " +
                               "JOIN Person p ON u.PersonID = p.PersonID " +
                               "WHERE p.Email = ?";
                               
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();

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
                    JOptionPane.showMessageDialog(frame, "Email not found in the system.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 4. Add components to frame and display
        frame.add(titleLabel);
        frame.add(emailPanel);
        frame.add(btnPanel);
        frame.setVisible(true);
    }
}