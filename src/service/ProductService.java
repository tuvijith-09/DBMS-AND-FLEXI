package service;

import db.DBConnection;
import java.sql.*;
import model.Product;

public class ProductService implements CRUDOperations<Product> {

    @Override
    public boolean add(Product p) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Product (ShopID, CategoryID, Name, CostPrice, SellingPrice, Quantity) VALUES (?, ?, ?, ?, ?, ?)"
            );

            ps.setInt(1, p.getShopId()); // This will now come securely from the session!
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getName());
            ps.setDouble(4, p.getCostPrice());
            ps.setDouble(5, p.getSellingPrice());
            ps.setInt(6, p.getQuantity());

            ps.executeUpdate();
            return true; 

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false; 
        }
    }

    @Override
    public void delete(int id) {
        // Simple delete logic
    }

    @Override
    public void viewAll() {}

    // 🔥 SECURE UPGRADE: Filters products by the logged-in Warehouse ID
    public ResultSet getAllProducts(int shopId) throws Exception {
        Connection con = DBConnection.getConnection();
        String query = "SELECT * FROM Product WHERE ShopID = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, shopId);
        return ps.executeQuery();
    }
}