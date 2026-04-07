package servlet;

import javax.servlet.http.*;
import java.io.PrintWriter;
import java.util.List;
import model.Payment;
import service.PaymentService;

@SuppressWarnings("serial")
public class PaymentServlet extends HttpServlet {

    private PaymentService paymentService = new PaymentService();

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
        java.util.List<Payment> payments = paymentService.viewAll(shopId);

        out.println("[");
        for (int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);
            out.println("  {\"paymentId\":" + p.getPaymentId() + ",\"invoiceId\":" + p.getInvoiceId() +
                    ",\"amount\":" + p.getAmount() + ",\"mode\":\"" + p.getPaymentMode() + "\"}");
            if (i < payments.size() - 1)
                out.println(",");
        }
        out.println("]");
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
            int invoiceId = safeParseInt(req.getParameter("invoiceId"), 0);
            double amount = safeParseDouble(req.getParameter("amount"), 0.0);
            String paymentMode = req.getParameter("paymentMode");

            if (invoiceId <= 0) {
                out.println("{\"success\":false,\"message\":\"Valid Invoice ID is required\"}");
                return;
            }

            Payment p = new Payment(0, invoiceId, shopId, amount, paymentMode);
            boolean added = paymentService.add(p);

            if (added) {
                out.println("{\"success\":true,\"message\":\"Payment added successfully\"}");
            } else {
                out.println("{\"success\":false,\"message\":\"Failed to add payment\"}");
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
            int paymentId = Integer.parseInt(req.getParameter("id"));
            boolean deleted = paymentService.deletePayment(paymentId);

            if (deleted) {
                out.println("{\"success\":true,\"message\":\"Payment deleted successfully\"}");
            } else {
                out.println("{\"success\":false,\"message\":\"Failed to delete payment\"}");
            }
        } catch (Exception e) {
            out.println("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
