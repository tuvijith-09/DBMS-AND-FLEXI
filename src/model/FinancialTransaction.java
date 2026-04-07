package model;

public class FinancialTransaction {

    private int transactionId;
    private int shopId;
    private String type;   // SALE, EXPENSE, REFUND
    private double amount;
    private String description;

    public FinancialTransaction(int transactionId, int shopId, String type,
                                double amount, String description) {
        this.transactionId = transactionId;
        this.shopId        = shopId;
        this.type          = type;
        this.amount        = amount;
        this.description   = description;
    }

    public int    getTransactionId() { return transactionId; }
    public int    getShopId()        { return shopId; }
    public String getType()          { return type; }
    public double getAmount()        { return amount; }
    public String getDescription()   { return description; }

    @Override
    public String toString() {
        return "FinancialTransaction [ID=" + transactionId + ", Type=" + type +
               ", Amount=" + amount + "]";
    }
}