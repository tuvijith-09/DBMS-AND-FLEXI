package model;

public class Customer extends Person {
    private int customerId;
    private int shopId;
    private String status;

    // Constructor to build the object
    public Customer(int personId, String firstName, String lastName,
                    String email, String phoneNo, String city,
                    int customerId, int shopId, String status) {
        
        // super() passes the human details to the Person class
        super(personId, firstName, lastName, email, phoneNo, city);
        
        this.customerId = customerId;
        this.shopId = shopId;
        this.status = status;
    }

    // --- GETTERS (This fixes the ShopID compile error!) ---
    public int getCustomerId() { return customerId; }
    public int getShopId() { return shopId; }
    public String getStatus() { return status; }
}