package service;

import model.Payment;

public class PaymentService implements CRUDOperations<Payment> {

    @Override
    public boolean add(Payment p) {
        // Payment table not yet in DB schema - placeholder for future use
        System.out.println("PaymentService.add() - Payment recorded: " + p.toString());
        return true;
    }

    @Override
    public boolean update(Payment p) {
        System.out.println("PaymentService.update() - Payment records are immutable.");
        return false;
    }

    @Override
    public void delete(int id) {
        System.out.println("PaymentService.delete() - Payment ID: " + id);
    }

    @Override
    public void viewAll() {
        System.out.println("PaymentService.viewAll() - No Payment table in DB yet.");
    }
}