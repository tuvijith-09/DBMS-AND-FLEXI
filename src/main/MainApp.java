package main;

import model.Product;
import service.CustomerService;
import service.ProductService;
import service.SupplierService;

public class MainApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("  Multi-Tenant Inventory System - Test  ");
        System.out.println("========================================\n");

        // ---- PRODUCT OPERATIONS ----
        ProductService productService = new ProductService();

        System.out.println("--- Testing ProductService ---");
        productService.viewAll(1);

        // Test overloaded add (polymorphism - method overloading)
        Product newProduct = new Product(0, 1, 1, "Test Keyboard", 500.0, 750.0, 15);
        productService.add(newProduct, "MainApp Test");

        System.out.println("\nAfter adding Test Keyboard:");
        productService.viewAll(1);

        System.out.println("\n--- Testing Product Validation (CLI Check) ---");
        System.out.println("Attempting to add a product with a missing name...");
        Product invalidProduct1 = new Product(0, 1, 1, "", 500.0, 750.0, 15);
        productService.add(invalidProduct1);

        System.out.println("\nAttempting to add a product with negative prices...");
        Product invalidProduct2 = new Product(0, 1, 1, "Broken Mouse", -100.0, -150.0, 15);
        productService.add(invalidProduct2);

        System.out.println("\nAttempting to add a product with negative quantity...");
        Product invalidProduct3 = new Product(0, 1, 1, "Broken Screen", 100.0, 150.0, -15);
        productService.add(invalidProduct3);

        // ---- CUSTOMER OPERATIONS ----
        CustomerService customerService = new CustomerService();

        System.out.println("\n--- Testing CustomerService ---");
        customerService.viewAll(1);

        // ---- SUPPLIER OPERATIONS ----
        SupplierService supplierService = new SupplierService();

        System.out.println("\n--- Testing SupplierService ---");
        supplierService.viewAll(1);

        System.out.println("\n========================================");
        System.out.println("  All Backend Tests Completed!");
        System.out.println("========================================");
    }
}