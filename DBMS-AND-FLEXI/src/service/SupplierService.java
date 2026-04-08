package service;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Supplier;

public class SupplierService implements CRUDOperations<Supplier> {

    @Override
    public boolean add(Supplier s) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start transaction

            int personId = -1;

            // 1. Check if the human already exists in the Person table
            String checkPersonSql = "SELECT PersonID FROM Person WHERE Email = ?";
            try (PreparedStatement psCheck = con.prepareStatement(checkPersonSql)) {
                psCheck.setString(1, s.getEmail());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        personId = rs.getInt("PersonID");
                    }
                }
            }

            // 2. If they don't exist, create a new Person
            if (personId == -1) {
                String personSql = "INSERT INTO Person (FirstName, LastName, Email, PhoneNo, City) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps1 = con.prepareStatement(personSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps1.setString(1, s.getFirstName());
                    ps1.setString(2, s.getLastName());
                    ps1.setString(3, s.getEmail());
                    ps1.setString(4, s.getPhoneNo());
                    ps1.setString(5, s.getCity());
                    ps1.executeUpdate();

                    try (ResultSet rsKeys = ps1.getGeneratedKeys()) {
                        if (rsKeys.next()) personId = rsKeys.getInt(1);
                    }
                }
            }

            // 3. Prevent duplicate supplier records inside the SAME warehouse
            String checkSupplierSql = "SELECT 1 FROM Supplier WHERE PersonID = ? AND ShopID = ?";
            try (PreparedStatement psCheckSup = con.prepareStatement(checkSupplierSql)) {
                psCheckSup.setInt(1, personId);
                psCheckSup.setInt(2, s.getShopId());
                try (ResultSet rs = psCheckSup.executeQuery()) {
                    if (rs.next()) {
                        con.rollback();
                        System.out.println("Supplier already exists in this Warehouse.");
                        return false; 
                    }
                }
            }

            // 4. Link the human to this specific Warehouse as a Supplier
            String supSql = "INSERT INTO Supplier (PersonID, ShopID, CompanyName) VALUES (?, ?, ?)";
            try (PreparedStatement ps2 = con.prepareStatement(supSql)) {
                ps2.setInt(1, personId);
                ps2.setInt(2, s.getShopId());
                ps2.setString(3, s.getCompanyName());
                ps2.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            System.out.println("Add Supplier Error: " + e.getMessage());
            return false;
        } finally {
            try { 
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                } 
            } catch (SQLException ex) {}
        }
    }

    @Override
    public boolean update(Supplier s) {
        String sql = "UPDATE Person p JOIN Supplier sp ON p.PersonID = sp.PersonID " +
                     "SET p.FirstName=?, p.LastName=?, p.Email=?, p.PhoneNo=?, p.City=?, sp.CompanyName=? " +
                     "WHERE sp.SupplierID=? AND sp.ShopID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhoneNo());
            ps.setString(5, s.getCity());
            ps.setString(6, s.getCompanyName());
            ps.setInt(7, s.getSupplierId());
            ps.setInt(8, s.getShopId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update Supplier Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void delete(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Supplier WHERE SupplierID = ?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Supplier deleted!" : "Supplier not found!");
        } catch (SQLException e) {
            System.out.println("Delete Supplier Error: " + e.getMessage());
        }
    }

    @Override
    public void viewAll() {
        String query = "SELECT s.SupplierID, p.FirstName, p.LastName, s.CompanyName FROM Supplier s JOIN Person p ON s.PersonID = p.PersonID";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("---- SUPPLIER LIST ----");
            while (rs.next()) {
                System.out.println(rs.getInt("SupplierID") + " | " +
                    rs.getString("FirstName") + " " + rs.getString("LastName") + " | " + rs.getString("CompanyName"));
            }
        } catch (SQLException e) {
            System.out.println("ViewAll Error: " + e.getMessage());
        }
    }

    // SECURE DATA FETCHING
    public List<Object[]> getSupplierTableData(int shopId) {
        List<Object[]> rowData = new ArrayList<>();
        String query = "SELECT s.SupplierID, p.FirstName, p.LastName, s.CompanyName, p.City " +
                       "FROM Supplier s JOIN Person p ON s.PersonID = p.PersonID WHERE s.ShopID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rowData.add(new Object[]{
                        rs.getInt("SupplierID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("CompanyName"),
                        rs.getString("City")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Fetch Suppliers Error: " + e.getMessage());
        }
        return rowData;
    }
}