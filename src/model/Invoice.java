package model;

public class Invoice {
    private int invoiceId;
    private int shopId;
    private int customerId;
    private double totalAmount;

    public Invoice(int invoiceId, int shopId, int customerId, double totalAmount) {
        this.invoiceId = invoiceId;
        this.shopId = shopId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
    }

    // Getters so the Service class can read the data
    public int getInvoiceId() { return invoiceId; }
    public int getShopId() { return shopId; }
    public int getCustomerId() { return customerId; }
    public double getTotalAmount() { return totalAmount; }
}