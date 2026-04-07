package servlet;

import javax.servlet.http.*;
import java.io.PrintWriter;
import java.util.List;
import model.Person;
import model.Supplier;
import service.SupplierService;

@SuppressWarnings("serial")
public class SupplierServlet extends HttpServlet {

    private SupplierService supplierService = new SupplierService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("shopId") == null) {
            String method = req.getMethod();
            System.out.println("SupplierServlet." + method + ": Unauthorized");
            out.println("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        int shopId = (int) session.getAttribute("shopId");
        List<Supplier> suppliers = supplierService.viewAll(shopId);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < suppliers.size(); i++) {
            Supplier s = suppliers.get(i);
            json.append(String.format(
                "{\"supplierId\":%d,\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"city\":\"%s\",\"companyName\":\"%s\"}",
                s.getSupplierId(),
                s.getFirstName().replace("\"", "\\\""),
                s.getLastName().replace("\"", "\\\""),
                s.getEmail().replace("\"", "\\\""),
                s.getPhoneNo().replace("\"", "\\\""),
                s.getCity().replace("\"", "\\\""),
                s.getCompanyName().replace("\"", "\\\"")
            ));
            if (i < suppliers.size() - 1) json.append(",");
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
            String method = req.getMethod();
            System.out.println("SupplierServlet." + method + ": Unauthorized");
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
            String companyName = req.getParameter("companyName");

            if (firstName == null || firstName.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"First name is required\"}");
                return;
            }
            if (companyName == null || companyName.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"Company name is required\"}");
                return;
            }

            boolean success;
            if (idStr != null && !idStr.isEmpty()) {
                // Update
                int supplierId = Integer.parseInt(idStr);
                Supplier s = new Supplier(supplierId, firstName, lastName, email, phone, city, 0, shopId, companyName);
                success = supplierService.update(s);
            } else {
                // Add
                Supplier s = new Supplier(0, firstName, lastName, email, phone, city, 0, shopId, companyName);
                success = supplierService.add(s);
            }

            if (success) {
                out.println("{\"success\":true,\"message\":\"Supplier saved successfully\"}");
            } else {
                out.println("{\"success\":false,\"message\":\"Failed to save supplier\"}");
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
            int supplierId = Integer.parseInt(req.getParameter("id"));
            supplierService.delete(supplierId);
            out.println("{\"success\":true,\"message\":\"Supplier deleted successfully\"}");
        } catch (Exception e) {
            out.println("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
