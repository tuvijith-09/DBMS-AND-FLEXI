package service;

public interface CRUDOperations<T> {
    // Returns true if successful, false if there is a database error
    boolean add(T obj); 
    void delete(int id);
    void viewAll();
}