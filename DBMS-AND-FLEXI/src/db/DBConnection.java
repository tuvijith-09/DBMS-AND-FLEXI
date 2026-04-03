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
                "Tuvijith@93"
            );

        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
        }

        return con;
    }
}