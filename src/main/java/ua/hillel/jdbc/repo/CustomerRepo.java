package ua.hillel.jdbc.repo;

import ua.hillel.jdbc.model.Customer;

public interface CustomerRepo {
    Customer find(Integer id);
    Customer findByEmail(String email);
    Customer findWithOrders(Integer id);
    void create(Customer customer);
}
