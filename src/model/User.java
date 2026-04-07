package model;

public class User extends Person {
    private int userId;
    private int shopId;
    private String username;
    private String password;
    private String role;

    // Constructor
    public User(int personId, String firstName, String lastName,
            String email, String phoneNo, String city,
            int userId, int shopId, String username, String password, String role) {
        super(personId, firstName, lastName, email, phoneNo, city);
        this.userId = userId;
        this.shopId = shopId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public int getShopId() {
        return shopId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}