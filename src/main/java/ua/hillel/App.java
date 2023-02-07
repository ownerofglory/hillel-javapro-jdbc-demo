package ua.hillel;

import ua.hillel.jdbc.model.Customer;
import ua.hillel.jdbc.repo.CustomerMysqlJdbcRepo;
import ua.hillel.jdbc.repo.CustomerRepo;

import java.sql.*;

public class App {
    public static void main(String[] args) {
        try {

            // load driver into JVM
            Class.forName("com.mysql.cj.jdbc.Driver");


            String url = "jdbc:mysql://localhost:3306/webshop";
            String username = System.getenv("MYSQL_USER");
            String password = System.getenv("MYSQL_PASSWORD");

            // create connection
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                CustomerRepo repo = new CustomerMysqlJdbcRepo(connection);

                Customer customer = new Customer();
                customer.setName("Ivan Petrenko");
                customer.setEmail("Ivan.Petrenko@test.com");
                customer.setPassword("Ishsifhvc8dhrp9");

                Customer withOrders = repo.findWithOrders(7);

                repo.create(customer);

                Customer byEmail = repo.findByEmail("Ivan.Petrenko@test.com");

                System.out.println();

                //                Statement statement = connection.createStatement();
//
//                Customer customerByEmail = getCustomerByEmail(connection, "jd@test.com\";-- select * from customer");
//
//                statement.execute("SELECT * FROM customer");
//                ResultSet resultSet = statement.getResultSet();
//
//                List<Customer> customerList = new ArrayList<>();
//
//                while (resultSet.next()) {
//                    Customer customer = new Customer();
//                    customer.setId(resultSet.getInt("id"));
//                    customer.setName(resultSet.getString("name"));
//                    customer.setEmail(resultSet.getString("email"));
//                    customer.setPassword(resultSet.getString("password"));
//
//                    customerList.add(customer);
//                }

//                System.out.println(customerList);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }




        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Customer getCustomerByEmail(Connection connection, String email) {
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
}
