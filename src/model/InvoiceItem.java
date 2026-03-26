package model;

public class InvoiceItem {
    private int invoiceItemId;
    private int invoiceId;
    private int productId;
    private int quantity;
    private double subtotal;

    public InvoiceItem(int invoiceItemId, int invoiceId, int productId, int quantity, double subtotal) {
        this.invoiceItemId = invoiceItemId;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    // Getters
    public int getInvoiceItemId() { return invoiceItemId; }
    public int getInvoiceId() { return invoiceId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return subtotal; }
}