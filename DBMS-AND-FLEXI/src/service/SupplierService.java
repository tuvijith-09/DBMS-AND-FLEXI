package service;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Supplier;

public class SupplierService implements CRUDOperations<Supplier> {

    @Override
    public boolean add(Supplier s) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            String personSql = "INSERT INTO Person (FirstName, LastName, Email, PhoneNo, City) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps1 = con.prepareStatement(personSql, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, s.getFirstName());
            ps1.setString(2, s.getLastName());
            ps1.setString(3, s.getEmail());
            ps1.setString(4, s.getPhoneNo());
            ps1.setString(5, s.getCity());
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            int personId = 0;
            if (rs.next()) personId = rs.getInt(1);

            String supSql = "INSERT INTO Supplier (PersonID, ShopID, CompanyName) VALUES (?, ?, ?)";
            PreparedStatement ps2 = con.prepareStatement(supSql);
            ps2.setInt(1, personId);
            ps2.setInt(2, s.getShopId());
            ps2.setString(3, s.getCompanyName());
            ps2.executeUpdate();

            con.commit();
            return true;
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            System.out.println("Add Supplier Error: " + e.getMessage());
            return false;
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    @Override
    public boolean update(Supplier s) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE Person p JOIN Supplier sp ON p.PersonID = sp.PersonID " +
                         "SET p.FirstName=?, p.LastName=?, p.Email=?, p.PhoneNo=?, p.City=?, sp.CompanyName=? " +
                         "WHERE sp.SupplierID=? AND sp.ShopID=?";
            PreparedStatement ps = con.prepareStatement(sql);
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
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM Supplier WHERE SupplierID = ?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Supplier deleted!" : "Supplier not found!");
        } catch (SQLException e) {
            System.out.println("Delete Supplier Error: " + e.getMessage());
        }
    }

    @Override
    public void viewAll() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT s.SupplierID, p.FirstName, p.LastName, s.CompanyName FROM Supplier s JOIN Person p ON s.PersonID = p.PersonID";
            ResultSet rs = con.createStatement().executeQuery(query);
            System.out.println("---- SUPPLIER LIST ----");
            while (rs.next()) {
                System.out.println(rs.getInt("SupplierID") + " | " +
                    rs.getString("FirstName") + " " + rs.getString("LastName") + " | " + rs.getString("CompanyName"));
            }
        } catch (SQLException e) {
            System.out.println("ViewAll Error: " + e.getMessage());
        }
    }

    public ResultSet getAllSuppliers(int shopId) throws SQLException {
        Connection con = DBConnection.getConnection();
        String query = "SELECT s.SupplierID, p.FirstName, p.LastName, s.CompanyName, p.City " +
                       "FROM Supplier s JOIN Person p ON s.PersonID = p.PersonID WHERE s.ShopID = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, shopId);
        return ps.executeQuery();
    }
}