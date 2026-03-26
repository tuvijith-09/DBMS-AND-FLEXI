package model;

// 'extends Person' means Customer already has all Person variables!
public class Customer extends Person {
    private int customerId;
    private int shopId;
    private String status;

    // The Constructor
    public Customer(int personId, String firstName, String lastName,
                    String email, String phoneNo, String city,
                    int customerId, int shopId, String status) {

        // 1. super() sends the shared data to the Person class
        super(personId, firstName, lastName, email, phoneNo, city);
        
        // 2. These are the variables unique to Customer
        this.customerId = customerId;
        this.shopId = shopId;
        this.status = status;
    }

    // Getters for Customer specific data
    public int getCustomerId() { return customerId; }
    public String getStatus() { return status; }
}