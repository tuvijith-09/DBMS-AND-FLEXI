package service;

import model.Supplier;
import db.DBConnection;
import java.sql.*;

public class SupplierService implements CRUDOperations<Supplier> {

    @Override
    public boolean add(Supplier s) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start transaction

            // 1. Insert into Person Table
            String personSql = "INSERT INTO Person (FirstName, LastName, Email, PhoneNo, City) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps1 = con.prepareStatement(personSql, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, s.getFirstName());
            ps1.setString(2, s.getLastName());
            ps1.setString(3, s.getEmail());
            ps1.setString(4, s.getPhoneNo());
            ps1.setString(5, s.getCity());
            ps1.executeUpdate();

            // Extract the auto-generated PersonID
            ResultSet rsKeys = ps1.getGeneratedKeys();
            int newPersonId = 0;
            if (rsKeys.next()) {
                newPersonId = rsKeys.getInt(1);
            }

            // 2. Insert into Supplier Table
            String supplierSql = "INSERT INTO Supplier (PersonID, ShopID, CompanyName) VALUES (?, ?, ?)";
            PreparedStatement ps2 = con.prepareStatement(supplierSql);
            ps2.setInt(1, newPersonId);
            ps2.setInt(2, s.getShopId()); // Securely injected from Session
            ps2.setString(3, s.getCompanyName());
            ps2.executeUpdate();

            con.commit(); // Save permanently
            return true;

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            System.out.println("Transaction Error: " + e.getMessage());
            return false; 
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    @Override
    public void delete(int id) {}

    @Override
    public void viewAll() {}

    // Securely load only this warehouse's suppliers
    public ResultSet getAllSuppliers(int shopId) throws Exception {
        Connection con = DBConnection.getConnection();
        
        String query = "SELECT s.SupplierID, p.FirstName, p.LastName, s.CompanyName, p.City " +
                       "FROM Supplier s " +
                       "JOIN Person p ON s.PersonID = p.PersonID " +
                       "WHERE s.ShopID = ?";
                       
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, shopId);
        
        return ps.executeQuery();
    }
}