package service;

import model.Payment;
import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentService implements CRUDOperations<Payment> {

    @Override
    public boolean add(Payment p) {
        if (p == null) {
            System.out.println("PaymentService.add() Error: Payment object is null");
            return false;
        }

        if (p.getAmount() <= 0) {
            System.out.println("PaymentService.add() Error: Amount must be positive");
            return false;
        }

        if (!invoiceExists(p.getInvoiceId(), p.getShopId())) {
            System.out.println("PaymentService.add() Error: Invoice not found or does not belong to this shop");
            return false;
        }

        String query = "INSERT INTO Payment (InvoiceID, ShopID, Amount, PaymentMode, Status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("PaymentService.add() Error: Database connection failed");
                return false;
            }

            ensurePaymentTableExists(con);

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, p.getInvoiceId());
                ps.setInt(2, p.getShopId());
                ps.setDouble(3, p.getAmount());
                ps.setString(4, p.getPaymentMode());
                ps.setString(5, "Completed");

                int result = ps.executeUpdate();
                return result > 0;
            }
        } catch (Exception e) {
            System.out.println("PaymentService.add() Error: " + e.getMessage());
            return false;
        }
    }

    public boolean invoiceExists(int invoiceId, int shopId) {
        String query = "SELECT 1 FROM Invoice WHERE InvoiceID = ? AND ShopID = ?";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("PaymentService.invoiceExists() Error: Database connection failed");
                return false;
            }

            ensurePaymentTableExists(con);

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, invoiceId);
                ps.setInt(2, shopId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (Exception e) {
            System.out.println("PaymentService.invoiceExists() Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Payment p) {
        System.out.println("PaymentService.update() - Payment records are immutable once recorded.");
        return false;
    }

    @Override
    public boolean delete(int id) {
        boolean deleted = deletePayment(id);
        if (!deleted) {
            System.out.println("PaymentService.delete() Error: Could not delete payment ID " + id);
        }
        return deleted;
    }

    public boolean deletePayment(int id) {
        String query = "DELETE FROM Payment WHERE PaymentID = ?";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("PaymentService.deletePayment() Error: Database connection failed");
                return false;
            }

            ensurePaymentTableExists(con);

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("PaymentService.deletePayment() - Payment ID: " + id + " cancelled.");
                }
                return rows > 0;
            }
        } catch (Exception e) {
            System.out.println("PaymentService.deletePayment() Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Payment> viewAll(int shopId) {
        String query = "SELECT * FROM Payment WHERE ShopID = ? ORDER BY PaymentDate DESC";
        List<Payment> payments = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("PaymentService.viewAll() Error: Database connection failed");
                return payments;
            }

            ensurePaymentTableExists(con);

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, shopId);
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("\n========== Payments (Shop " + shopId + ") ==========");
                    while (rs.next()) {
                        Payment p = new Payment(
                                rs.getInt("PaymentID"),
                                rs.getInt("InvoiceID"),
                                rs.getInt("ShopID"),
                                rs.getDouble("Amount"),
                                rs.getString("PaymentMode"));
                        payments.add(p);
                        System.out.println("Payment ID: " + p.getPaymentId() + ", Amount: " + p.getAmount());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("PaymentService.viewAll() Error: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    public Payment getPaymentById(int paymentId) {
        String query = "SELECT * FROM Payment WHERE PaymentID = ?";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("PaymentService.getPaymentById() Error: Database connection failed");
                return null;
            }

            ensurePaymentTableExists(con);

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, paymentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Payment(
                                rs.getInt("PaymentID"),
                                rs.getInt("InvoiceID"),
                                rs.getInt("ShopID"),
                                rs.getDouble("Amount"),
                                rs.getString("PaymentMode"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("PaymentService.getPaymentById() Error: " + e.getMessage());
        }
        return null;
    }

    public List<Payment> getPaymentsByInvoice(int invoiceId) {
        String query = "SELECT * FROM Payment WHERE InvoiceID = ?";
        List<Payment> payments = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("PaymentService.getPaymentsByInvoice() Error: Database connection failed");
                return payments;
            }

            ensurePaymentTableExists(con);

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, invoiceId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        payments.add(new Payment(
                                rs.getInt("PaymentID"),
                                rs.getInt("InvoiceID"),
                                rs.getInt("ShopID"),
                                rs.getDouble("Amount"),
                                rs.getString("PaymentMode")));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("PaymentService.getPaymentsByInvoice() Error: " + e.getMessage());
        }
        return payments;
    }

    public double getTotalPaymentsByShop(int shopId) {
        String query = "SELECT SUM(Amount) as TotalAmount FROM Payment WHERE ShopID = ? AND Status = 'Completed'";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("PaymentService.getTotalPaymentsByShop() Error: Database connection failed");
                return 0;
            }

            ensurePaymentTableExists(con);

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, shopId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("TotalAmount");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("PaymentService.getTotalPaymentsByShop() Error: " + e.getMessage());
        }
        return 0;
    }

    private void ensurePaymentTableExists(Connection con) {
        String ddl = "CREATE TABLE IF NOT EXISTS Payment (" +
                "PaymentID INT AUTO_INCREMENT PRIMARY KEY, " +
                "InvoiceID INT NOT NULL, " +
                "ShopID INT NOT NULL, " +
                "Amount DECIMAL(10,2) NOT NULL, " +
                "PaymentMode VARCHAR(50) NOT NULL, " +
                "PaymentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "Status VARCHAR(50) DEFAULT 'Completed')";
        try (Statement st = con.createStatement()) {
            st.execute(ddl);
        } catch (Exception e) {
            System.out.println("PaymentService.ensurePaymentTableExists() Error: " + e.getMessage());
        }
    }
}