package service;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Product;

public class ProductService implements CRUDOperations<Product> {

    @Override
    public boolean add(Product p) {
        String sql = "INSERT INTO Product (ShopID, CategoryID, Name, CostPrice, SellingPrice, Quantity) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getShopId());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getName());
            ps.setDouble(4, p.getCostPrice());
            ps.setDouble(5, p.getSellingPrice());
            ps.setInt(6, p.getQuantity());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add Product Error: " + e.getMessage());
            return false;
        }
    }

    public boolean add(Product p, String addedBy) {
        boolean result = add(p);
        if (result) System.out.println("Product added by: " + addedBy);
        return result;
    }

    @Override
    public boolean update(Product p) {
        String sql = "UPDATE Product SET CategoryID=?, Name=?, CostPrice=?, SellingPrice=?, Quantity=? WHERE ProductID=? AND ShopID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getCategoryId());
            ps.setString(2, p.getName());
            ps.setDouble(3, p.getCostPrice());
            ps.setDouble(4, p.getSellingPrice());
            ps.setInt(5, p.getQuantity());
            ps.setInt(6, p.getProductId());
            ps.setInt(7, p.getShopId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update Product Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void delete(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Product WHERE ProductID = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Legacy Delete Error: " + e.getMessage());
        }
    }

    public void delete(int productId, int shopId) {
        String sql = "DELETE FROM Product WHERE ProductID = ? AND ShopID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, shopId);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Product deleted securely!" : "Unauthorized delete attempt!");
        } catch (SQLException e) {
            System.out.println("Secure Delete Error: " + e.getMessage());
        }
    }

    @Override
    public void viewAll() {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Product")) {
            System.out.println("---- PRODUCT LIST ----");
            while (rs.next()) {
                System.out.println(rs.getInt("ProductID") + " | " + rs.getString("Name"));
            }
        } catch (SQLException e) {
            System.out.println("ViewAll Error: " + e.getMessage());
        }
    }

    // --- SECURE DATA FETCHING: Returns a detached List, safely closing the DB Connection ---
    public List<Object[]> getProductTableData(int shopId) {
        List<Object[]> rowData = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE ShopID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rowData.add(new Object[]{
                        rs.getInt("ProductID"),
                        rs.getInt("CategoryID"),
                        rs.getString("Name"),
                        rs.getDouble("CostPrice"),
                        rs.getDouble("SellingPrice"),
                        rs.getInt("Quantity")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Fetch Products Error: " + e.getMessage());
        }
        return rowData;
    }
}