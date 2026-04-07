package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/multi_tenant_inventory",
                    "root",
                    "tu09");

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver Not Found: " + e.getMessage());
            System.out.println("Ensure mysql-connector-j JAR is in the classpath");
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e.getMessage());
            System.out.println("Please check:");
            System.out.println("1. MySQL server is running");
            System.out.println("2. Database 'multi_tenant_inventory' exists");
            System.out.println("3. Username/password are correct");
        }

        return con;
    }
}