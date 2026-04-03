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
        productService.viewAll();

        // Test overloaded add (polymorphism - method overloading)
        Product newProduct = new Product(0, 1, 1, "Test Keyboard", 500.0, 750.0, 15);
        productService.add(newProduct, "MainApp Test");

        System.out.println("\nAfter adding Test Keyboard:");
        productService.viewAll();

        // ---- CUSTOMER OPERATIONS ----
        CustomerService customerService = new CustomerService();

        System.out.println("\n--- Testing CustomerService ---");
        customerService.viewAll();

        // ---- SUPPLIER OPERATIONS ----
        SupplierService supplierService = new SupplierService();

        System.out.println("\n--- Testing SupplierService ---");
        supplierService.viewAll();

        System.out.println("\n========================================");
        System.out.println("  All Backend Tests Completed!");
        System.out.println("========================================");
    }
}