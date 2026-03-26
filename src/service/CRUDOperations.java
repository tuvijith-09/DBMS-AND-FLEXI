package service;

public interface CRUDOperations<T> {
    void add(T obj);
    void delete(int id);
    void viewAll();
}