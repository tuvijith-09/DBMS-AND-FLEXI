package model;

public class Payment {

    private int paymentId;
    private int invoiceId;
    private int shopId;
    private double amount;
    private String paymentMode; // Cash, Card, UPI

    public Payment(int paymentId, int invoiceId, int shopId, double amount, String paymentMode) {
        this.paymentId   = paymentId;
        this.invoiceId   = invoiceId;
        this.shopId      = shopId;
        this.amount      = amount;
        this.paymentMode = paymentMode;
    }

    public int    getPaymentId()   { return paymentId; }
    public int    getInvoiceId()   { return invoiceId; }
    public int    getShopId()      { return shopId; }
    public double getAmount()      { return amount; }
    public String getPaymentMode() { return paymentMode; }

    @Override
    public String toString() {
        return "Payment [ID=" + paymentId + ", Invoice=" + invoiceId +
               ", Amount=" + amount + ", Mode=" + paymentMode + "]";
    }
}