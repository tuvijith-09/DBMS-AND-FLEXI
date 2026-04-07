package model;

public class Expense {

    private int expenseId;
    private int shopId;
    private String description;
    private double amount;
    private String category; // Rent, Salary, Utilities, etc.

    public Expense(int expenseId, int shopId, String description, double amount, String category) {
        this.expenseId   = expenseId;
        this.shopId      = shopId;
        this.description = description;
        this.amount      = amount;
        this.category    = category;
    }

    public int    getExpenseId()   { return expenseId; }
    public int    getShopId()      { return shopId; }
    public String getDescription() { return description; }
    public double getAmount()      { return amount; }
    public String getCategory()    { return category; }

    @Override
    public String toString() {
        return "Expense [ID=" + expenseId + ", ShopID=" + shopId +
               ", Desc=" + description + ", Amount=" + amount + "]";
    }
}