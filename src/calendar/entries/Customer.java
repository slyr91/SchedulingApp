package calendar.entries;

import application.DatabaseConnectionManager;
import application.Main;
import exceptions.CountEntriesException;
import exceptions.CustomerUpdateException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Customer {
    private Integer customerId;
    private String customerName;
    private Integer addressId;
    private Integer active;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;

    public Customer(Integer customerId, String customerName, Integer addressId, Integer active, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.addressId = addressId;
        this.active = active;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    public Customer() {
        this.customerId = null;
        this.customerName = null;
        this.addressId = null;
        this.active = 1;
        this.createDate = null;
        this.createdBy = null;
        this.lastUpdate = null;
        this.lastUpdateBy = null;
    }

    public static boolean alreadyExists(String customerName, Integer addressId) {
        boolean exists = false;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("Select * FROM customer WHERE customerName='" + customerName +
                    "' AND addressId=" + addressId);

            if (resultSet.next()) {
                exists = true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return exists;
    }

    public static Customer getCustomer(Integer customerId) {
        Customer customerEntry = null;
        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM customer WHERE customerId=" + customerId);

            if(resultSet.next()) {
                customerEntry = new Customer(resultSet.getInt("customerId"), resultSet.getString("customerName"),
                        resultSet.getInt("addressId"), resultSet.getInt("active"), resultSet.getTimestamp("createDate").toLocalDateTime(),
                        resultSet.getString("createdBy"), resultSet.getTimestamp("lastUpdate").toLocalDateTime(),
                        resultSet.getString("lastUpdateBy"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return customerEntry;
    }

    public static Integer getCustomerId(String customerName, int addressId) {
        Integer id = null;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("Select customerId FROM customer WHERE customerName='" + customerName +
                    "' AND addressId=" + addressId);

            if(resultSet.next()) {
                id = resultSet.getInt("customerId");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return id;
    }

    public static int getCustomerId(Customer customer) {
        Integer customerId;

        if(customer.getCustomerId() == null) {
            customerId = getCustomerId(customer.getCustomerName(), customer.getAddressId());
        } else {
            customerId = customer.getCustomerId();
        }

        return customerId;
    }

    public static boolean createNewCustomer(Customer customer) {
        boolean success = false;

        if(!Customer.alreadyExists(customer.getCustomerName(), customer.getAddressId())) {

            try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()) {

                int result = st.executeUpdate("INSERT INTO customer(customerName,addressId,active,createDate,createdBy,lastUpdateBy) VALUES('" +
                        customer.getCustomerName() + "'," + customer.getAddressId() + "," + 1 +
                        ",'" + LocalDateTime.now().toString() + "','" + Main.getUser().getUsername() + "','" + Main.getUser().getUsername() + "')");

                if(result > 0) {
                    int customerId = Customer.getCustomerId(customer);
                    customer.setCustomerId(customerId);
                    success = true;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return success;
    }

    public static boolean updateCustomer(Customer oldCustomer, Customer newCustomer) throws CustomerUpdateException {
        boolean success = false;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            int result = st.executeUpdate("UPDATE customer SET customerName='" + newCustomer.getCustomerName() + "', addressId=" +
                    newCustomer.getAddressId() + ", lastUpdateBy='" + Main.getUser().getUsername() + "' WHERE customerId=" + oldCustomer.getCustomerId());

            if(result > 0) {
                success = true;
            }

        } catch (SQLException throwables) {
            throw new CustomerUpdateException();
        }

        return success;
    }

    public static Customer getCustomer(String customerName, Integer addressId) {
        Customer customerEntry = null;
        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM customer WHERE customerName='" + customerName +
                    "' AND addressId=" + addressId);

            if(resultSet.next()) {
                customerEntry = new Customer(resultSet.getInt("customerId"), resultSet.getString("customerName"),
                        resultSet.getInt("addressId"), resultSet.getInt("active"),
                        resultSet.getTimestamp("createDate").toLocalDateTime(),
                        resultSet.getString("createdBy"), resultSet.getTimestamp("lastUpdate").toLocalDateTime(),
                        resultSet.getString("lastUpdateBy"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return customerEntry;
    }

    public static int countAddressId(Integer addressId) throws CountEntriesException {

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT count(addressId) FROM customer WHERE addressId=" + addressId);

            if(resultSet.next()) {
                return resultSet.getInt("count(addressId)");
            } else {
                return 0;
            }
        } catch (SQLException throwables) {
            throw new CountEntriesException();
        }

    }

    public static boolean deleteCustomer(Integer customerId) {
        boolean success = false;
        Customer customer = Customer.getCustomer(customerId);

        if(Customer.alreadyExists(customer.getCustomerName(), customer.getAddressId())) {

            try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()) {

                int result = st.executeUpdate("DELETE FROM customer WHERE customerId=" + customer.getCustomerId());

                if(result > 0) {
                    success = true;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return success;
    }


    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
