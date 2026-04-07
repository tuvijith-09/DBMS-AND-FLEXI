package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Payment;
import service.PaymentService;
import util.InputValidator;
import util.UserSession;

public class PaymentUI {

    public static void main(String[] args) {

        // Security Check
        if (UserSession.loggedInShopId == 0) {
            JOptionPane.showMessageDialog(null, "Security Alert: Please login first!");
            return;
        }

        int currentShopId = UserSession.loggedInShopId;

        JFrame frame = new JFrame("Shop " + currentShopId + " - Payment Management");
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // ---- Top Panel: Input Fields ----
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField paymentIdField = new JTextField(); // For view/update
        JTextField invoiceIdField = new JTextField();
        JTextField amountField = new JTextField();
        JComboBox<String> modeCombo = new JComboBox<>(
                new String[] { "Cash", "Card", "UPI", "Check", "Online Transfer" });

        inputPanel.add(new JLabel("Payment ID (for search):"));
        inputPanel.add(paymentIdField);
        inputPanel.add(new JLabel("Invoice ID:"));
        inputPanel.add(invoiceIdField);
        inputPanel.add(new JLabel("Amount (Rs):"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Payment Mode:"));
        inputPanel.add(modeCombo);
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));

        // ---- Center Panel: Table ----
        String[] cols = { "Payment ID", "Invoice ID", "Amount", "Mode", "Date", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Click on a row to fill fields for editing
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                paymentIdField.setText(model.getValueAt(row, 0).toString());
                invoiceIdField.setText(model.getValueAt(row, 1).toString());
                amountField.setText(model.getValueAt(row, 2).toString());
                modeCombo.setSelectedItem(model.getValueAt(row, 3).toString());
            }
        });

        // ---- Bottom Panel: Buttons ----
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Process Payment");
        JButton viewBtn = new JButton("View Payment Details");
        JButton deleteBtn = new JButton("Cancel Payment");
        JButton refreshBtn = new JButton("Refresh Table");
        btnPanel.add(addBtn);
        btnPanel.add(viewBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        PaymentService service = new PaymentService();

        // ---- Refresh Action ----
        refreshBtn.addActionListener(e -> {
            try {
                model.setRowCount(0);
                List<Payment> payments = service.viewAll(currentShopId);
                for (Payment payment : payments) {
                    model.addRow(new Object[] {
                            payment.getPaymentId(),
                            payment.getInvoiceId(),
                            payment.getAmount(),
                            payment.getPaymentMode(),
                            null,
                            "Completed"
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Table load error: " + ex.getMessage());
            }
        });

        // ---- Add Payment Action ----
        addBtn.addActionListener(e -> {
            try {
                if (InputValidator.isNullOrEmpty(invoiceIdField.getText()) ||
                        InputValidator.isNullOrEmpty(amountField.getText())) {
                    JOptionPane.showMessageDialog(frame, "Invoice ID and Amount are required.");
                    return;
                }
                int invoiceId = Integer.parseInt(invoiceIdField.getText());
                String amountText = amountField.getText();
                if (!InputValidator.isPositiveDouble(amountText)) {
                    JOptionPane.showMessageDialog(frame, "Amount must be a positive number.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double amount = Double.parseDouble(amountText);
                String mode = (String) modeCombo.getSelectedItem();

                if (!service.invoiceExists(invoiceId, currentShopId)) {
                    JOptionPane.showMessageDialog(frame, "Invoice ID not found for this shop.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Payment p = new Payment(0, invoiceId, currentShopId, amount, mode);
                if (service.add(p)) {
                    JOptionPane.showMessageDialog(frame,
                            "Payment processed successfully for Shop " + currentShopId + "!");
                    clearFields(paymentIdField, invoiceIdField, amountField);
                    modeCombo.setSelectedIndex(0);
                    refreshBtn.doClick();
                } else {
                    JOptionPane.showMessageDialog(frame, "Database Error! Please check application logs.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input: Enter valid numbers.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---- View Payment Details Action ----
        viewBtn.addActionListener(e -> {
            try {
                if (InputValidator.isNullOrEmpty(paymentIdField.getText())) {
                    JOptionPane.showMessageDialog(frame, "Select a payment from the table first.");
                    return;
                }
                int paymentId = Integer.parseInt(paymentIdField.getText());
                Payment payment = service.getPaymentById(paymentId);

                if (payment != null) {
                    String details = "Payment ID: " + payment.getPaymentId() + "\n" +
                            "Invoice ID: " + payment.getInvoiceId() + "\n" +
                            "Amount: Rs " + payment.getAmount() + "\n" +
                            "Mode: " + payment.getPaymentMode();
                    JOptionPane.showMessageDialog(frame, details, "Payment Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Payment not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Payment ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // ---- Delete Payment Action ----
        deleteBtn.addActionListener(e -> {
            if (InputValidator.isNullOrEmpty(paymentIdField.getText())) {
                JOptionPane.showMessageDialog(frame, "Select a payment from the table first.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to cancel this payment?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int paymentId = Integer.parseInt(paymentIdField.getText());
                    boolean deleted = service.deletePayment(paymentId);
                    if (deleted) {
                        clearFields(paymentIdField, invoiceIdField, amountField);
                        modeCombo.setSelectedIndex(0);
                        refreshBtn.doClick();
                        JOptionPane.showMessageDialog(frame, "Payment cancelled.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Could not cancel payment. Check the payment ID.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid Payment ID.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        refreshBtn.doClick();
    }

    private static void clearFields(JTextField... fields) {
        for (JTextField f : fields)
            f.setText("");
    }
}
