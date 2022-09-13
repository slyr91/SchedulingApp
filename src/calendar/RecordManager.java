package calendar;

import application.DatabaseConnectionManager;
import calendar.entries.Customer;
import calendar.entries.Record;
import logging.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordManager {

    public static List<Record> fetchRecords() {
        List<Record> records = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(),DatabaseConnectionManager.getUsername() ,DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet results = st.executeQuery("SELECT * FROM customer");

            if(results.next()) {
                do {
                    Customer customer = new Customer();
                    customer.setCustomerId(results.getInt("customerId"));
                    customer.setCustomerName(results.getString("customerName"));
                    customer.setAddressId(results.getInt("addressId"));
                    customer.setActive(results.getInt("active"));
                    customer.setCreateDate(results.getTimestamp("createDate").toLocalDateTime());
                    customer.setCreatedBy(results.getString("createdBy"));
                    customer.setLastUpdate(results.getTimestamp("lastUpdate").toLocalDateTime());
                    customer.setLastUpdateBy(results.getString("lastUpdateBy"));

                    Record record = new Record(customer);

                    records.add(record);

                } while (results.next());
            }

        } catch (SQLException throwables) {
            Logger.getInstance().error("Unable to fetch contact data from the database.");
        }

        return records;
    }

}
