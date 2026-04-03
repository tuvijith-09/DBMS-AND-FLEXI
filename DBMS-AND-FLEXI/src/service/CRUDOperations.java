package service;

public interface CRUDOperations<T> {

    boolean add(T obj);

    boolean update(T obj);

    void delete(int id);

    void viewAll();
}