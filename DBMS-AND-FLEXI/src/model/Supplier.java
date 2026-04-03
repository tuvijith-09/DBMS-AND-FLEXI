package model;

public class Supplier extends Person {
    private int supplierId;
    private int shopId;
    private String companyName;

    // Constructor to build the Supplier object
    public Supplier(int personId, String firstName, String lastName,
                    String email, String phoneNo, String city,
                    int supplierId, int shopId, String companyName) {
        
        // Pass human details to the parent Person class
        super(personId, firstName, lastName, email, phoneNo, city);
        
        this.supplierId = supplierId;
        this.shopId = shopId;
        this.companyName = companyName;
    }

    // Getters so the Service layer can read the data
    public int getSupplierId() { return supplierId; }
    public int getShopId() { return shopId; }
    public String getCompanyName() { return companyName; }
}