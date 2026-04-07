package servlet;

import javax.servlet.http.*;
import java.io.PrintWriter;
import model.User;
import service.UserService;

@SuppressWarnings("serial")
public class SignupServlet extends HttpServlet {

    private UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        try {
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String city = req.getParameter("city");
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String shopIdStr = req.getParameter("shopId");

            // FIX: Validate each field individually with clear messages
            if (firstName == null || firstName.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"First name is required\"}");
                return;
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"Last name is required\"}");
                return;
            }
            if (email == null || email.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"Email is required\"}");
                return;
            }
            if (phone == null || phone.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"Phone is required\"}");
                return;
            }
            if (city == null || city.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"City is required\"}");
                return;
            }
            if (username == null || username.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"Username is required\"}");
                return;
            }
            if (password == null || password.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"Password is required\"}");
                return;
            }
            if (shopIdStr == null || shopIdStr.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"Shop ID is required\"}");
                return;
            }

            int shopId;
            try {
                shopId = Integer.parseInt(shopIdStr.trim());
            } catch (NumberFormatException e) {
                out.println("{\"success\":false,\"message\":\"Invalid shop ID - must be a number\"}");
                return;
            }

            // FIX: Check username exists only if DB is reachable; catch exception
            try {
                if (userService.usernameExists(username)) {
                    out.println("{\"success\":false,\"message\":\"Username already exists\"}");
                    return;
                }
            } catch (Exception e) {
                // FIX: If DB is down, report it clearly instead of proceeding
                out.println("{\"success\":false,\"message\":\"Database connection failed: "
                        + e.getMessage().replace("\"", "'") + "\"}");
                return;
            }

            User user = new User(0, firstName, lastName, email, phone, city, 0, shopId, username, password, "user");
            boolean success = userService.signup(user);

            if (success) {
                out.println("{\"success\":true,\"message\":\"Signup successful\"}");
            } else {
                String error = userService.getLastError();
                if (error == null || error.isEmpty()) error = "Signup failed";
                out.println("{\"success\":false,\"message\":\"" + error.replace("\"", "'") + "\"}");
            }
        } catch (Exception e) {
            System.out.println("SignupServlet Error: " + e.getMessage());
            e.printStackTrace();
            out.println("{\"success\":false,\"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}