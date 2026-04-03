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
        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO Product (ShopID, CategoryID, Name, CostPrice, SellingPrice, Quantity) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, p.getShopId());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getName());
            ps.setDouble(4, p.getCostPrice());
            ps.setDouble(5, p.getSellingPrice());
            ps.setInt(6, p.getQuantity());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Add Product Error: " + e.getMessage());
            return false;
        }
    }

    // Overloaded add - demonstrates method overloading (polymorphism)
    public boolean add(Product p, String addedBy) {
        boolean result = add(p);
        if (result) System.out.println("Product added by: " + addedBy);
        return result;
    }

    @Override
    public boolean update(Product p) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE Product SET CategoryID=?, Name=?, CostPrice=?, SellingPrice=?, Quantity=? WHERE ProductID=? AND ShopID=?";
            PreparedStatement ps = con.prepareStatement(sql);
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
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM Product WHERE ProductID = ?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Product deleted!" : "Product not found!");
        } catch (SQLException e) {
            System.out.println("Delete Product Error: " + e.getMessage());
        }
    }

    @Override
    public void viewAll() {
        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Product");
            System.out.println("---- PRODUCT LIST ----");
            while (rs.next()) {
                System.out.println(rs.getInt("ProductID") + " | " + rs.getString("Name") +
                    " | CP: " + rs.getDouble("CostPrice") + " | SP: " + rs.getDouble("SellingPrice") +
                    " | Qty: " + rs.getInt("Quantity"));
            }
        } catch (SQLException e) {
            System.out.println("ViewAll Error: " + e.getMessage());
        }
    }

    public ResultSet getAllProducts(int shopId) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Product WHERE ShopID = ?");
        ps.setInt(1, shopId);
        return ps.executeQuery();
    }
}