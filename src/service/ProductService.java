package service;

import model.Product;
import db.DBConnection;

import java.sql.*;

public class ProductService implements CRUDOperations<Product> {

    public void add(Product p) {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Product (ShopID, CategoryID, Name, CostPrice, SellingPrice, Quantity) VALUES (?, ?, ?, ?, ?, ?)"
            );

            ps.setInt(1, p.getShopId());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getName());
            ps.setDouble(4, p.getCostPrice());
            ps.setDouble(5, p.getSellingPrice());
            ps.setInt(6, p.getQuantity());

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void delete(int id) {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM Product WHERE ProductID=?"
            );

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Delete error");
        }
    }

    public void viewAll() {
        try {
            Connection con = DBConnection.getConnection();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Product");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("ProductID") + " | " +
                        rs.getString("Name") + " | " +
                        rs.getInt("Quantity")
                );
            }

        } catch (Exception e) {
            System.out.println("Fetch error");
        }
    }

    // 🔥 Used for JTable
    public ResultSet getAllProducts() throws Exception {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        return st.executeQuery("SELECT * FROM Product");
    }
}