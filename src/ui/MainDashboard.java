package ui;

import java.awt.*;
import javax.swing.*;

import model.Invoice;
import util.UserSession;

public class MainDashboard {

    public static void main(String[] args) {
        if (UserSession.loggedInUserName == null || UserSession.loggedInUserName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Security Alert: You must login first!", "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
            LoginUI.main(new String[0]);
            return;
        }

        JFrame frame = new JFrame("Warehouse Inventory Management - Dashboard");
        frame.setSize(500, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(7, 1, 10, 10)); // Changed to 7 rows for Payment
        frame.setLocationRelativeTo(null);

        String welcomeText = "Welcome, " + UserSession.loggedInUserName + " (Warehouse ID: "
                + UserSession.loggedInShopId + ")";
        JLabel welcomeLabel = new JLabel(welcomeText, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(welcomeLabel);

        JButton productBtn = new JButton("Manage Warehouse Inventory");
        JButton supplierBtn = new JButton("Manage Suppliers"); // NEW
        JButton customerBtn = new JButton("Manage Customers");
        JButton billingBtn = new JButton("Invoice & Billing (Checkout)");
        JButton paymentBtn = new JButton("Manage Payments"); // NEW
        JButton exitBtn = new JButton("Logout & Exit");

        productBtn.addActionListener(e -> ProductUI.main(new String[0]));
        supplierBtn.addActionListener(e -> SupplierUI.main(new String[0])); // NEW ACTION
        customerBtn.addActionListener(e -> CustomerUI.main(new String[0]));
        billingBtn.addActionListener(e -> InvoiceUI.main(new String[0]));
        paymentBtn.addActionListener(e -> PaymentUI.main(new String[0])); // NEW ACTION
        exitBtn.addActionListener(e -> {
            UserSession.clearSession();
            System.exit(0);
        });

        frame.add(productBtn);
        frame.add(supplierBtn); // ADDED TO SCREEN
        frame.add(customerBtn);
        frame.add(billingBtn);
        frame.add(paymentBtn); // ADDED TO SCREEN
        frame.add(exitBtn);

        frame.setVisible(true);
    }
}