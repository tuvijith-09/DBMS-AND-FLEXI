package servlet;

import javax.servlet.http.*;
import java.io.PrintWriter;
import java.util.List;
import model.Person;
import model.Customer;
import service.CustomerService;

@SuppressWarnings("serial")
public class CustomerServlet extends HttpServlet {

    private CustomerService customerService = new CustomerService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("shopId") == null) {
            System.out.println("CustomerServlet.doGet: Unauthorized");
            out.println("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        int shopId = (int) session.getAttribute("shopId");
        List<Customer> customers = customerService.viewAll(shopId);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            json.append(String.format(
                "{\"customerId\":%d,\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"city\":\"%s\",\"status\":\"%s\"}",
                c.getCustomerId(),
                c.getFirstName().replace("\"", "\\\""),
                c.getLastName().replace("\"", "\\\""),
                c.getEmail().replace("\"", "\\\""),
                c.getPhoneNo().replace("\"", "\\\""),
                c.getCity().replace("\"", "\\\""),
                c.getStatus().replace("\"", "\\\"")
            ));
            if (i < customers.size() - 1) json.append(",");
        }
        json.append("]");
        out.println(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("shopId") == null) {
            out.println("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        try {
            int shopId = (int) session.getAttribute("shopId");
            String idStr = req.getParameter("id");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName") != null ? req.getParameter("lastName").trim() : "";
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String city = req.getParameter("city");
            String status = req.getParameter("status") != null ? req.getParameter("status").trim() : "Active";

            if (firstName == null || firstName.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"First name is required\"}");
                return;
            }

            boolean success;
            if (idStr != null && !idStr.isEmpty()) {
                // Update
                int customerId = Integer.parseInt(idStr);
                Customer c = new Customer(customerId, firstName, lastName, email, phone, city, 0, shopId, status);
                success = customerService.update(c);
            } else {
                // Add
                Customer c = new Customer(0, firstName, lastName, email, phone, city, 0, shopId, status);
                success = customerService.add(c);
            }

            if (success) {
                out.println("{\"success\":true,\"message\":\"Customer saved successfully\"}");
            } else {
                out.println("{\"success\":false,\"message\":\"Failed to save customer\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"success\":false,\"message\":\"Error: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        try {
            int customerId = Integer.parseInt(req.getParameter("id"));
            customerService.delete(customerId);
            out.println("{\"success\":true,\"message\":\"Customer deleted successfully\"}");
        } catch (Exception e) {
            out.println("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
