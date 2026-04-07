package servlet;

import javax.servlet.http.*;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import db.DBConnection;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {

        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        String loginId = req.getParameter("email");
        if (loginId == null || loginId.trim().isEmpty()) {
            loginId = req.getParameter("username"); // Fallback to username
        }
        
        String shopId = req.getParameter("shopId");
        String password = req.getParameter("password");

        // Validate input
        if (loginId == null || loginId.trim().isEmpty() ||
                shopId == null || shopId.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            out.println("{\"success\":false,\"message\":\"Login ID, Shop ID, and Password are all required\"}");
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            int shop = Integer.parseInt(shopId);

            con = DBConnection.getConnection();

            if (con == null) {
                out.println("{\"success\":false,\"message\":\"Database connection failed\"}");
                return;
            }

            // Check against BOTH Email and Username
            String sql = "SELECT u.UserID, u.Username, p.Email FROM User u " +
                         "JOIN Person p ON u.PersonID = p.PersonID " +
                         "WHERE (p.Email = ? OR u.Username = ?) AND u.ShopID = ? AND u.Password = ?";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, loginId);
            ps.setString(2, loginId);
            ps.setInt(3, shop);
            ps.setString(4, password);

            rs = ps.executeQuery();

            if (rs.next()) {
                HttpSession session = req.getSession(true);
                session.setMaxInactiveInterval(1800); // 30 minutes
                
                session.setAttribute("shopId", shop);
                session.setAttribute("username", rs.getString("Username"));
                session.setAttribute("email", rs.getString("Email"));

                System.out.println("Login Success: User " + rs.getString("Username") + " (Shop " + shop + ")");
                out.println("{\"success\":true,\"message\":\"Login successful\",\"shopId\":" + shop + "}");
            } else {
                System.out.println("Login Failed: User " + loginId + " (Shop " + shop + ")");
                out.println("{\"success\":false,\"message\":\"Invalid credentials or Shop ID\"}");
            }

        } catch (NumberFormatException e) {
            out.println("{\"success\":false,\"message\":\"Invalid Shop ID format\"}");
        } catch (Exception e) {
            System.out.println("LoginServlet Error: " + e.getMessage());
            e.printStackTrace();
            out.println("{\"success\":false,\"message\":\"Server error: " + e.getMessage().replace("\"", "'") + "\"}");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ignored) {
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (Exception ignored) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {

        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("shopId") != null) {
            int shopId = (int) session.getAttribute("shopId");
            out.println("{\"loggedIn\":true,\"shopId\":" + shopId + "}");
        } else {
            out.println("{\"loggedIn\":false}");
        }
    }
}