package ua.hillel.jdbc.repo;

import ua.hillel.jdbc.model.Customer;
import ua.hillel.jdbc.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CustomerMysqlJdbcRepo implements CustomerRepo {
    private Connection connection;

    public CustomerMysqlJdbcRepo(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Customer find(Integer id) {
        try (PreparedStatement statement =  connection.prepareStatement("SELECT * FROM customer WHERE id = ?")) {
            statement.setInt(1, id);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            resultSet.next();

            Customer customer = new Customer();
            customer.setId(resultSet.getInt("id"));
            customer.setName(resultSet.getString("name"));
            customer.setEmail(resultSet.getString("email"));
            customer.setPassword(resultSet.getString("password"));

            return customer;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer findByEmail(String email) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM customer WHERE email = ?")) {
            statement.setString(1, email);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            resultSet.next();

            Customer customer = new Customer();
            customer.setId(resultSet.getInt("id"));
            customer.setName(resultSet.getString("name"));
            customer.setEmail(resultSet.getString("email"));
            customer.setPassword(resultSet.getString("password"));

            return customer;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer findWithOrders(Integer id) {
        String sql = "SELECT c.id, c.name, c.email, o.orderDate, o.totalAmount" +
                " FROM customer AS c" +
                " JOIN t_order AS o" +
                " ON c.id = o.customerId" +
                " WHERE c.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();

            Customer customer = null;


            while (resultSet.next()) {
                if (customer == null) {
                    customer = new Customer();
                    customer.setId(resultSet.getInt("c.id"));
                    customer.setName(resultSet.getString("c.name"));
                    customer.setEmail(resultSet.getString("c.email"));
                    customer.setOrders(new ArrayList<>());
                }

                Order order = new Order();
                String orderDateString = resultSet.getString("orderDate");
                order.setOrderDate(LocalDateTime.parse(orderDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                order.setTotalAmount(resultSet.getDouble("totalAmount"));
                order.setCustomer(customer);
                customer.getOrders().add(order);
            }

            return customer;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Customer customer) {
        String sql = "INSERT INTO customer " +
                "(name, email, password)" +
               " VALUES(?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getPassword());

            int i = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
