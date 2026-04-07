<%@ page import="java.sql.*, db.DBConnection" %>
<html>
<head><title>IMS DB Diagnosis</title></head>
<body>
    <h1>Database Connection Test</h1>
    <%
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            if (con != null) {
                out.println("<h2 style='color:green'>SUCCESS: Database Connected!</h2>");
                DatabaseMetaData meta = con.getMetaData();
                out.println("<p>Product Name: " + meta.getDatabaseProductName() + "</p>");
                out.println("<p>Product Version: " + meta.getDatabaseProductVersion() + "</p>");
                
                // Check for User table
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM User");
                if (rs.next()) {
                    out.println("<p>User Table exists and has " + rs.getInt(1) + " rows.</p>");
                }
                
                // Session Check
                HttpSession sess = request.getSession(false);
                if (sess != null && sess.getAttribute("shopId") != null) {
                    out.println("<p>Session Active: YES (Shop ID: " + sess.getAttribute("shopId") + ")</p>");
                } else {
                    out.println("<p style='color:orange'>Session Active: NO (or missing shopId)</p>");
                }
                
            } else {
                out.println("<h2 style='color:red'>FAILURE: Database Connection is NULL</h2>");
            }
        } catch (Exception e) {
            out.println("<h2 style='color:red'>ERROR: " + e.getMessage() + "</h2>");
            e.printStackTrace(new java.io.PrintWriter(out));
        } finally {
            if (con != null) con.close();
        }
    %>
    <hr>
    <a href="index.html">Back to Login</a>
</body>
</html>
