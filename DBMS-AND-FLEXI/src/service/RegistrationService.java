package service;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegistrationService {

    public boolean registerNewWarehouse(String shopName, String shopCity, 
                                        String fName, String lName, 
                                        String email, String phone, String password) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start the ACID transaction

            int newShopId = 0;
            int newPersonId = 0;

            // 1. Create the new Shop
            String shopSql = "INSERT INTO Shop (ShopName, City) VALUES (?, ?)";
            try (PreparedStatement psShop = con.prepareStatement(shopSql, Statement.RETURN_GENERATED_KEYS)) {
                psShop.setString(1, shopName);
                psShop.setString(2, shopCity);
                psShop.executeUpdate();
                try (ResultSet rs = psShop.getGeneratedKeys()) {
                    if (rs.next()) newShopId = rs.getInt(1);
                }
            }

            // 2. Check if the Person's email already exists (We don't want duplicate managers)
            String checkPersonSql = "SELECT PersonID FROM Person WHERE Email = ?";
            try (PreparedStatement psCheck = con.prepareStatement(checkPersonSql)) {
                psCheck.setString(1, email);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        con.rollback(); // Abort!
                        System.out.println("Email already registered in the system.");
                        return false; 
                    }
                }
            }

            // 3. Create the new Person
            String personSql = "INSERT INTO Person (FirstName, LastName, Email, PhoneNo, City) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psPerson = con.prepareStatement(personSql, Statement.RETURN_GENERATED_KEYS)) {
                psPerson.setString(1, fName);
                psPerson.setString(2, lName);
                psPerson.setString(3, email);
                psPerson.setString(4, phone);
                psPerson.setString(5, shopCity); // Assuming manager lives in the same city as the shop for simplicity
                psPerson.executeUpdate();
                try (ResultSet rs = psPerson.getGeneratedKeys()) {
                    if (rs.next()) newPersonId = rs.getInt(1);
                }
            }

            // 4. Create the User login credentials linking the Person and Shop
            String userSql = "INSERT INTO User (PersonID, ShopID, Password) VALUES (?, ?, ?)";
            try (PreparedStatement psUser = con.prepareStatement(userSql)) {
                psUser.setInt(1, newPersonId);
                psUser.setInt(2, newShopId);
                psUser.setString(3, password);
                psUser.executeUpdate();
            }

            // Everything succeeded! Commit the transaction.
            con.commit();
            return true;

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            System.out.println("Registration Error: " + e.getMessage());
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
}