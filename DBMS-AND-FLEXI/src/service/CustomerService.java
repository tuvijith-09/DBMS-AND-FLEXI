package service;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Customer;

public class CustomerService implements CRUDOperations<Customer> {

    @Override
    public boolean add(Customer c) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            String personSql = "INSERT INTO Person (FirstName, LastName, Email, PhoneNo, City) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps1 = con.prepareStatement(personSql, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, c.getFirstName());
            ps1.setString(2, c.getLastName());
            ps1.setString(3, c.getEmail());
            ps1.setString(4, c.getPhoneNo());
            ps1.setString(5, c.getCity());
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            int personId = 0;
            if (rs.next()) personId = rs.getInt(1);

            String custSql = "INSERT INTO Customer (PersonID, ShopID, Status) VALUES (?, ?, ?)";
            PreparedStatement ps2 = con.prepareStatement(custSql);
            ps2.setInt(1, personId);
            ps2.setInt(2, c.getShopId());
            ps2.setString(3, c.getStatus());
            ps2.executeUpdate();

            con.commit();
            return true;
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            System.out.println("Add Customer Error: " + e.getMessage());
            return false;
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    @Override
    public boolean update(Customer c) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE Person p JOIN Customer c ON p.PersonID = c.PersonID " +
                         "SET p.FirstName=?, p.LastName=?, p.Email=?, p.PhoneNo=?, p.City=?, c.Status=? " +
                         "WHERE c.CustomerID=? AND c.ShopID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getLastName());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getPhoneNo());
            ps.setString(5, c.getCity());
            ps.setString(6, c.getStatus());
            ps.setInt(7, c.getCustomerId());
            ps.setInt(8, c.getShopId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update Customer Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void delete(int id) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM Customer WHERE CustomerID = ?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Customer deleted!" : "Customer not found!");
        } catch (SQLException e) {
            System.out.println("Delete Customer Error: " + e.getMessage());
        }
    }

    @Override
    public void viewAll() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT c.CustomerID, p.FirstName, p.LastName, c.Status FROM Customer c JOIN Person p ON c.PersonID = p.PersonID";
            ResultSet rs = con.createStatement().executeQuery(query);
            System.out.println("---- CUSTOMER LIST ----");
            while (rs.next()) {
                System.out.println(rs.getInt("CustomerID") + " | " +
                    rs.getString("FirstName") + " " + rs.getString("LastName") + " | " + rs.getString("Status"));
            }
        } catch (SQLException e) {
            System.out.println("ViewAll Error: " + e.getMessage());
        }
    }

    public ResultSet getAllCustomers(int shopId) throws SQLException {
        Connection con = DBConnection.getConnection();
        String query = "SELECT c.CustomerID, p.FirstName, p.LastName, s.ShopName, c.Status " +
                       "FROM Customer c JOIN Person p ON c.PersonID = p.PersonID " +
                       "JOIN Shop s ON c.ShopID = s.ShopID WHERE c.ShopID = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, shopId);
        return ps.executeQuery();
    }
}