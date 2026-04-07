package service;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.User;

public class UserService {

    // FIX: Store last error message so servlet can report it clearly
    private String lastError = "";

    public String getLastError() {
        return lastError;
    }

    public boolean signup(User user) {
        Connection con = null;
        lastError = "";
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                lastError = "Database connection failed";
                System.out.println("Signup Error: " + lastError);
                return false;
            }
            con.setAutoCommit(false);

            // Insert into Person table
            String personSql = "INSERT INTO Person (FirstName, LastName, Email, PhoneNo, City) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps1 = con.prepareStatement(personSql, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, user.getFirstName());
            ps1.setString(2, user.getLastName());
            ps1.setString(3, user.getEmail());
            ps1.setString(4, user.getPhoneNo());
            ps1.setString(5, user.getCity());
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            int personId = 0;
            if (rs.next()) {
                personId = rs.getInt(1);
            }

            if (personId == 0) {
                lastError = "Failed to create Person record";
                con.rollback();
                return false;
            }

            // Insert into User table
            String userSql = "INSERT INTO User (PersonID, ShopID, Username, Password, Role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps2 = con.prepareStatement(userSql);
            ps2.setInt(1, personId);
            ps2.setInt(2, user.getShopId());
            ps2.setString(3, user.getUsername());
            ps2.setString(4, user.getPassword());
            ps2.setString(5, user.getRole());
            ps2.executeUpdate();

            con.commit();
            return true;

        } catch (SQLException e) {
            lastError = e.getMessage();
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            // FIX: Print full SQL error for debugging
            System.out.println("Signup SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean usernameExists(String username) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                throw new RuntimeException("Database connection failed");
            }

            String sql = "SELECT COUNT(*) FROM User WHERE Username = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Username check error: " + e.getMessage());
            // FIX: Rethrow so SignupServlet can catch and report DB error properly
            throw new RuntimeException("DB error checking username: " + e.getMessage(), e);
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}