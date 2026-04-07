package service;

import db.DBConnection;
import java.sql.*;
import java.util.List;
import model.Invoice;
import model.InvoiceItem;

public class InvoiceService {

    public boolean createInvoiceWithItems(Invoice invoice, List<InvoiceItem> items) {
        Connection con = null;

        try {
            con = DBConnection.getConnection();

            if (con == null) {
                System.out.println("DB connection failed");
                return false;
            }

            // 🔴 START TRANSACTION
            con.setAutoCommit(false);

            double totalAmount = 0;

            // 🔴 VALIDATION LOOP
            for (InvoiceItem item : items) {

                int productId = item.getProductId();
                int qty = item.getQuantity();

                // 1️⃣ Quantity validation
                if (qty <= 0) {
                    throw new SQLException("Quantity must be greater than 0 for Product ID: " + productId);
                }

                // 2️⃣ Check product + stock
                String checkSql = "SELECT Quantity, SellingPrice FROM Product WHERE ProductID=? AND ShopID=?";
                PreparedStatement psCheck = con.prepareStatement(checkSql);
                psCheck.setInt(1, productId);
                psCheck.setInt(2, invoice.getShopId());

                ResultSet rs = psCheck.executeQuery();

                // Product not found
                if (!rs.next()) {
                    throw new SQLException("Product does not exist in this shop. Product ID: " + productId);
                }

                int stock = rs.getInt("Quantity");
                double price = rs.getDouble("SellingPrice");

                // 3️⃣ Stock validation
                if (qty > stock) {
                    throw new SQLException("Not enough stock for Product ID: " + productId);
                }

                // Calculate subtotal
                double subtotal = qty * price;
                item.setSubtotal(subtotal);
                totalAmount += subtotal;

                rs.close();
                psCheck.close();
            }

            // 🔴 INSERT INVOICE
            String invoiceSql = "INSERT INTO Invoice (ShopID, CustomerID, TotalAmount) VALUES (?, ?, ?)";
            PreparedStatement psInvoice = con.prepareStatement(invoiceSql, Statement.RETURN_GENERATED_KEYS);
            psInvoice.setInt(1, invoice.getShopId());
            psInvoice.setInt(2, invoice.getCustomerId());
            psInvoice.setDouble(3, totalAmount);
            psInvoice.executeUpdate();

            ResultSet rsKeys = psInvoice.getGeneratedKeys();
            int newInvoiceId = 0;

            if (rsKeys.next()) {
                newInvoiceId = rsKeys.getInt(1);
            } else {
                throw new SQLException("Failed to get generated Invoice ID.");
            }

            // 🔴 PREPARE STATEMENTS
            String itemSql = "INSERT INTO InvoiceItem (InvoiceID, ProductID, Quantity, Subtotal) VALUES (?, ?, ?, ?)";
            PreparedStatement psItem = con.prepareStatement(itemSql);

            String updateStockSql = "UPDATE Product SET Quantity = Quantity - ? WHERE ProductID = ? AND ShopID = ?";
            PreparedStatement psUpdateStock = con.prepareStatement(updateStockSql);

            // 🔴 PROCESS ITEMS
            for (InvoiceItem item : items) {

                // Insert InvoiceItem
                psItem.setInt(1, newInvoiceId);
                psItem.setInt(2, item.getProductId());
                psItem.setInt(3, item.getQuantity());
                psItem.setDouble(4, item.getSubtotal());
                psItem.executeUpdate();

                // Update stock
                psUpdateStock.setInt(1, item.getQuantity());
                psUpdateStock.setInt(2, item.getProductId());
                psUpdateStock.setInt(3, invoice.getShopId());

                int rowsAffected = psUpdateStock.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Stock update failed for Product ID: " + item.getProductId());
                }
            }

            // 🔴 COMMIT
            con.commit();
            return true;

        } catch (Exception e) {

            // 🔴 ROLLBACK
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback Error: " + ex.getMessage());
            }

            System.out.println("Billing Transaction Error: " + e.getMessage());
            return false;

        } finally {

            try {
                if (con != null)
                    con.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("AutoCommit Reset Error: " + ex.getMessage());
            }
        }
    }

    // ---------------- OTHER METHODS ----------------

    public java.util.List<Invoice> getAllInvoicesList(int shopId) {
        java.util.List<Invoice> list = new java.util.ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            if (con == null)
                return list;

            String query = "SELECT InvoiceID, ShopID, CustomerID, TotalAmount FROM Invoice WHERE ShopID = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, shopId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Invoice(
                        rs.getInt("InvoiceID"),
                        rs.getInt("ShopID"),
                        rs.getInt("CustomerID"),
                        rs.getDouble("TotalAmount")));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("GetAllInvoices Error: " + e.getMessage());
        }
        return list;
    }

    public boolean add(Invoice invoice) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null)
                return false;

            String sql = "INSERT INTO Invoice (ShopID, CustomerID, TotalAmount) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, invoice.getShopId());
            ps.setInt(2, invoice.getCustomerId());
            ps.setDouble(3, invoice.getTotalAmount());

            boolean success = ps.executeUpdate() > 0;
            ps.close();

            return success;

        } catch (SQLException e) {
            System.out.println("Add Invoice Error: " + e.getMessage());
            return false;
        }
    }
}