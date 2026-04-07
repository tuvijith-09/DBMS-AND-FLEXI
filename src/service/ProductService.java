package service;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Product;

public class ProductService implements CRUDOperations<Product> {

    @Override
    public boolean add(Product p) {
        // ---- validation checking ----
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            System.out.println("Validation Error: Product name cannot be empty.");
            return false;
        }
        if (p.getCostPrice() < 0 || p.getSellingPrice() < 0) {
            System.out.println("Validation Error: Price cannot be negative.");
            return false;
        }
        if (p.getQuantity() < 0) {
            System.out.println("Validation Error: Quantity cannot be negative.");
            return false;
        }

        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                System.out.println("Add Product Error: Database connection failed");
                return false;
            }
            String sql = "INSERT INTO Product (ShopID, CategoryID, Name, CostPrice, SellingPrice, Quantity) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, p.getShopId());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getName());
            ps.setDouble(4, p.getCostPrice());
            ps.setDouble(5, p.getSellingPrice());
            ps.setInt(6, p.getQuantity());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Add Product SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Overloaded add - demonstrates method overloading (polymorphism)
    public boolean add(Product p, String addedBy) {
        boolean result = add(p);
        if (result)
            System.out.println("Product added by: " + addedBy);
        return result;
    }

    @Override
    public boolean update(Product p) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                System.out.println("Update Product Error: Database connection failed");
                return false;
            }
            String sql = "UPDATE Product SET CategoryID=?, Name=?, CostPrice=?, SellingPrice=?, Quantity=? WHERE ProductID=? AND ShopID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, p.getCategoryId());
            ps.setString(2, p.getName());
            ps.setDouble(3, p.getCostPrice());
            ps.setDouble(4, p.getSellingPrice());
            ps.setInt(5, p.getQuantity());
            ps.setInt(6, p.getProductId());
            ps.setInt(7, p.getShopId());
            boolean result = ps.executeUpdate() > 0;
            ps.close();
            return result;
        } catch (SQLException e) {
            System.out.println("Update Product SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                System.out.println("Delete Product Error: Database connection failed");
                return false;
            }
            PreparedStatement ps = con.prepareStatement("DELETE FROM Product WHERE ProductID = ?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            ps.close();
            System.out.println(rows > 0 ? "Product deleted!" : "Product not found!");
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Delete Product SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public java.util.List<Product> viewAll(int shopId) {
        java.util.List<Product> list = new java.util.ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                System.out.println("ViewAll Error: Database connection failed");
                return list;
            }
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Product WHERE ShopID = ?");
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            System.out.println("---- PRODUCT LIST (Shop " + shopId + ") ----");
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("ProductID"),
                        rs.getInt("ShopID"),
                        rs.getInt("CategoryID"),
                        rs.getString("Name"),
                        rs.getDouble("CostPrice"),
                        rs.getDouble("SellingPrice"),
                        rs.getInt("Quantity"));
                list.add(p);
                System.out.println(p.getProductId() + " | " + p.getName() + " | Qty: " + p.getQuantity());
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("ViewAll Error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public ResultSet getAllProducts(int shopId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Database connection failed");
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Product WHERE ShopID = ?");
        ps.setInt(1, shopId);
        return ps.executeQuery();
    }

    public boolean categoryExists(int currentShopId, int catId) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            PreparedStatement ps = con.prepareStatement(
                "SELECT 1 FROM Category WHERE ShopID = ? AND CategoryID = ?"
            );
            ps.setInt(1, currentShopId);
            ps.setInt(2, catId);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close();
            ps.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}