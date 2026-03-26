package service;

import model.Customer;
import db.DBConnection;
import java.sql.*;

// Implements your CRUD interface!
public class CustomerService implements CRUDOperations<Customer> {

    public void add(Customer c) {
        // Adding a customer requires inserting into Person first, then Customer.
        // We will keep it simple for now and focus on viewing data for the demo!
        System.out.println("Add customer logic goes here.");
    }

    public void delete(int id) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM Customer WHERE CustomerID=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Delete error: " + e.getMessage());
        }
    }

    public void viewAll() {
        // Console view logic
    }

    // 🔥 Used for the JTable in CustomerUI
    public ResultSet getAllCustomers() throws Exception {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        
        // The JOIN query straight from your excellent SQL script
        String query = "SELECT c.CustomerID, p.FirstName, p.LastName, s.ShopName, c.Status " +
                       "FROM Customer c " +
                       "JOIN Person p ON c.PersonID = p.PersonID " +
                       "JOIN Shop s ON c.ShopID = s.ShopID";
                       
        return st.executeQuery(query);
    }
}