package util;

public class InputValidator {

    // Checks if a string is null or blank
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Checks if a string is a valid positive integer
    public static boolean isPositiveInteger(String value) {
        try {
            return Integer.parseInt(value.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks if a string is a valid non-negative double (price/quantity)
    public static boolean isNonNegativeDouble(String value) {
        try {
            return Double.parseDouble(value.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks if a string is a valid positive double
    public static boolean isPositiveDouble(String value) {
        try {
            return Double.parseDouble(value.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Validates a basic email format
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email))
            return false;
        return email.contains("@") && email.contains(".");
    }

    // Validates phone number (must be 10 digits)
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone))
            return false;
        return phone.trim().matches("\\d{10}");
    }

    // Validates that selling price is not less than cost price
    public static boolean isSellingPriceValid(double costPrice, double sellingPrice) {
        return sellingPrice >= costPrice;
    }

    // Validates minimum name length
    public static boolean isValidName(String name) {
        return !isNullOrEmpty(name) && name.trim().length() >= 2;
    }
}