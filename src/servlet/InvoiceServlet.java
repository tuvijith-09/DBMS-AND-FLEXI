package servlet;

import javax.servlet.http.*;
import java.io.PrintWriter;
import service.InvoiceService;

@SuppressWarnings("serial")
public class InvoiceServlet extends HttpServlet {

    private InvoiceService invoiceService = new InvoiceService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("shopId") == null) {
            out.println("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        int shopId = (int) session.getAttribute("shopId");
        java.util.List<model.Invoice> invoices = invoiceService.getAllInvoicesList(shopId);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < invoices.size(); i++) {
            model.Invoice inv = invoices.get(i);
            json.append(String.format(
                "{\"invoiceId\":%d,\"customerId\":%d,\"totalAmount\":%.2f}",
                inv.getInvoiceId(), inv.getCustomerId(), inv.getTotalAmount()
            ));
            if (i < invoices.size() - 1) json.append(",");
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
            System.out.println("InvoiceServlet." + method + ": Unauthorized");
            out.println("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        try {
            int shopId = (int) session.getAttribute("shopId");
            int customerId = safeParseInt(req.getParameter("customerId"), 0);
            double totalAmount = safeParseDouble(req.getParameter("totalAmount"), 0.0);

            if (customerId <= 0) {
                out.println("{\"success\":false,\"message\":\"Valid Customer ID is required\"}");
                return;
            }

            model.Invoice invoice = new model.Invoice(0, shopId, customerId, totalAmount);
            boolean success = invoiceService.add(invoice);

            if (success) {
                out.println("{\"success\":true,\"message\":\"Invoice created successfully\"}");
            } else {
                out.println("{\"success\":false,\"message\":\"Failed to create invoice\"}");
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
}
