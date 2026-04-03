package util;

public class UserSession {
    // These variables are static, meaning they are shared across the entire application
    public static int loggedInShopId = 0;
    public static String loggedInUserName = "";

    // Method to save user details upon successful login
    public static void startSession(int shopId, String userName) {
        loggedInShopId = shopId;
        loggedInUserName = userName;
    }

    // Method to clear details when they exit
    public static void clearSession() {
        loggedInShopId = 0;
        loggedInUserName = "";
    }
}