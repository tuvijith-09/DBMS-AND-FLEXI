package service;

import java.util.List;

public interface CRUDOperations<T> {

    boolean add(T obj);

    boolean update(T obj);

    boolean delete(int id);

    List<T> viewAll(int shopId);
}