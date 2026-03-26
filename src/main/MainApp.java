package main;

import service.ProductService;

public class MainApp {
    public static void main(String[] args) {
        ProductService service = new ProductService();
        service.viewAll();
    }
}