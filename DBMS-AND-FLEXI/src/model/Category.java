package model;

public class Category {

    private int categoryId;
    private int shopId;
    private String name;

    public Category(int categoryId, int shopId, String name) {
        this.categoryId = categoryId;
        this.shopId = shopId;
        this.name = name;
    }

    public int getCategoryId() { return categoryId; }
    public int getShopId()     { return shopId; }
    public String getName()    { return name; }

    @Override
    public String toString() {
        return "Category [ID=" + categoryId + ", ShopID=" + shopId + ", Name=" + name + "]";
    }
}