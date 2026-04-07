package servlet;

import javax.servlet.http.*;
import java.io.PrintWriter;
import java.util.List;
import model.Product;
import service.ProductService;

@SuppressWarnings("serial")
public class ProductServlet extends HttpServlet {

    private ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("shopId") == null) {
            System.out.println("ProductServlet.doGet: Unauthorized (Session: " + (session != null) + ")");
            out.println("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        int shopId = (int) session.getAttribute("shopId");
        List<Product> products = productService.viewAll(shopId);
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            json.append(String.format(
                "{\"productId\":%d,\"shopId\":%d,\"categoryId\":%d,\"name\":\"%s\",\"costPrice\":%.2f,\"sellingPrice\":%.2f,\"quantity\":%d}",
                p.getProductId(), p.getShopId(), p.getCategoryId(), 
                p.getName().replace("\"", "\\\""), p.getCostPrice(), p.getSellingPrice(), p.getQuantity()
            ));
            if (i < products.size() - 1) json.append(",");
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
            System.out.println("ProductServlet.doPost: Unauthorized");
            out.println("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        try {
            int shopId = (int) session.getAttribute("shopId");
            String idStr = req.getParameter("id");
            int categoryId = safeParseInt(req.getParameter("categoryId"), 1);
            String name = req.getParameter("name");
            double costPrice = safeParseDouble(req.getParameter("costPrice"), 0.0);
            double sellingPrice = safeParseDouble(req.getParameter("sellingPrice"), 0.0);
            int quantity = safeParseInt(req.getParameter("quantity"), 0);

            if (name == null || name.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"Product name is required\"}");
                return;
            }

            boolean success;
            if (idStr != null && !idStr.isEmpty()) {
                // Update
                int productId = Integer.parseInt(idStr);
                Product p = new Product(productId, shopId, categoryId, name, costPrice, sellingPrice, quantity);
                success = productService.update(p);
            } else {
                // Add
                Product p = new Product(0, shopId, categoryId, name, costPrice, sellingPrice, quantity);
                success = productService.add(p, "WebUI");
            }

            if (success) {
                out.println("{\"success\":true,\"message\":\"Product saved successfully\"}");
            } else {
                out.println("{\"success\":false,\"message\":\"Failed to save product\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"success\":false,\"message\":\"Error: " + e.getMessage() + "\"}");
        }
    }

    private int safeParseInt(String str, int defaultValue) {
        try {
            if (str == null || str.trim().isEmpty()) return defaultValue;
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double safeParseDouble(String str, double defaultValue) {
        try {
            if (str == null || str.trim().isEmpty()) return defaultValue;
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        try {
            int productId = Integer.parseInt(req.getParameter("id"));
            productService.delete(productId);
            out.println("{\"success\":true,\"message\":\"Product deleted successfully\"}");
        } catch (Exception e) {
            out.println("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
