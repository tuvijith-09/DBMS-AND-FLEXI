package service;

import db.DBConnection;
import java.sql.*;
import java.util.List;
import model.Invoice;
import model.InvoiceItem;

public class InvoiceService {

    // We use a custom method here because an Invoice needs a list of items (a cart) to process together.
    public boolean createInvoiceWithItems(Invoice invoice, List<InvoiceItem> items) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            
            // 1. Start the Transaction (Do not save permanently until everything is successful!)
            con.setAutoCommit(false);

            // 2. Insert the main Invoice record
            String invoiceSql = "INSERT INTO Invoice (ShopID, CustomerID, TotalAmount) VALUES (?, ?, ?)";
            PreparedStatement psInvoice = con.prepareStatement(invoiceSql, Statement.RETURN_GENERATED_KEYS);
            psInvoice.setInt(1, invoice.getShopId());
            psInvoice.setInt(2, invoice.getCustomerId());
            psInvoice.setDouble(3, invoice.getTotalAmount());
            psInvoice.executeUpdate();

            // Extract the newly generated InvoiceID
            ResultSet rsKeys = psInvoice.getGeneratedKeys();
            int newInvoiceId = 0;
            if (rsKeys.next()) {
                newInvoiceId = rsKeys.getInt(1);
            } else {
                throw new SQLException("Failed to get generated Invoice ID.");
            }

            // 3. Prepare queries for the Cart Items and Product Stock updates
            String itemSql = "INSERT INTO InvoiceItem (InvoiceID, ProductID, Quantity, Subtotal) VALUES (?, ?, ?, ?)";
            PreparedStatement psItem = con.prepareStatement(itemSql);

            // The update query dynamically subtracts the sold quantity from current stock
            String updateStockSql = "UPDATE Product SET Quantity = Quantity - ? WHERE ProductID = ? AND ShopID = ?";
            PreparedStatement psUpdateStock = con.prepareStatement(updateStockSql);

            // 4. Loop through every item in the shopping cart
            for (InvoiceItem item : items) {
                // A. Insert into InvoiceItem table
                psItem.setInt(1, newInvoiceId);
                psItem.setInt(2, item.getProductId());
                psItem.setInt(3, item.getQuantity());
                psItem.setDouble(4, item.getSubtotal());
                psItem.executeUpdate();

                // B. Reduce stock in Product table (Using ShopID to ensure strict multi-tenant isolation!)
                psUpdateStock.setInt(1, item.getQuantity());
                psUpdateStock.setInt(2, item.getProductId());
                psUpdateStock.setInt(3, invoice.getShopId()); 
                
                int rowsAffected = psUpdateStock.executeUpdate();
                
                // If 0 rows were updated, it means the product doesn't exist for this specific shop!
                if (rowsAffected == 0) {
                    throw new SQLException("Stock update failed for Product ID: " + item.getProductId() + ". Invalid Product or Shop.");
                }
            }

            // 5. If everything succeeded, commit the transaction and save permanently!
            con.commit();
            return true;

        } catch (Exception e) {
            // 6. If ANYTHING fails, undo everything to protect database integrity!
            try { 
                if (con != null) con.rollback(); 
            } catch (SQLException ex) {
                System.out.println("Rollback Error: " + ex.getMessage());
            }
            System.out.println("Billing Transaction Error: " + e.getMessage());
            return false;
            
        } finally {
            // Always return connection to normal auto-commit state
            try { 
                if (con != null) con.setAutoCommit(true); 
            } catch (SQLException ex) {
                System.out.println("AutoCommit Reset Error: " + ex.getMessage());
            }
        }
    }
}