package model;

// Model class for Invoice
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

    public int getInvoiceId() {
        return invoiceId;
    }

    public int getShopId() {
        return shopId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

}
