package model;

public class Product {

    private int productId;
    private int shopId;
    private int categoryId;
    private String name;
    private double costPrice;
    private double sellingPrice;
    private int quantity;

    public Product(int productId, int shopId, int categoryId,
                   String name, double costPrice,
                   double sellingPrice, int quantity) {
        this.productId     = productId;
        this.shopId        = shopId;
        this.categoryId    = categoryId;
        this.name          = name;
        this.costPrice     = costPrice;
        this.sellingPrice  = sellingPrice;
        this.quantity      = quantity;
    }

    // getProductId was MISSING in original - needed for update()
    public int    getProductId()    { return productId; }
    public int    getShopId()       { return shopId; }
    public int    getCategoryId()   { return categoryId; }
    public String getName()         { return name; }
    public double getCostPrice()    { return costPrice; }
    public double getSellingPrice() { return sellingPrice; }
    public int    getQuantity()     { return quantity; }
}