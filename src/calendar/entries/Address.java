package calendar.entries;

import application.DatabaseConnectionManager;
import application.Main;
import exceptions.CountEntriesException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Address {
    private Integer addressId;
    private String address;
    private String address2;
    private Integer cityId;
    private String postalCode;
    private String phone;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;

    public Address(Integer addressId, String addressLine, String addressLine2, Integer cityId, String postalCode, String phone, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy) {
        this.addressId = addressId;
        this.address = addressLine;
        this.address2 = addressLine2;
        this.cityId = cityId;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate =lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    public Address() {
        this.addressId = null;
        this.address = null;
        this.address2 = null;
        this.cityId = null;
        this.postalCode = null;
        this.phone = null;
        this.createDate = null;
        this.createdBy = null;
        this.lastUpdate =null;
        this.lastUpdateBy = null;
    }

    public static boolean alreadyExists(String addressLine, String addressLine2, Integer cityId, String postalCode, String phoneNumber) {
        boolean exists = false;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM address WHERE address='" + addressLine +
                    "' AND address2='" + addressLine2 + "' AND cityId=" + cityId +
                    " AND postalCode='" + postalCode + "' AND phone='" + phoneNumber + "'");

            if(resultSet.next()) {
                exists = true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return exists;
    }

    public static Integer getAddressId(String addressLine, String addressLine2, Integer cityId, String postalCode, String phoneNumber) {
        Integer id = null;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT addressId FROM address WHERE address='" + addressLine +
                    "' AND address2='" + addressLine2 + "' AND cityId=" + cityId + " AND postalCode='" + postalCode +
                    "' AND phone='" + phoneNumber + "'");

            if(resultSet.next()) {
                id = resultSet.getInt("addressId");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return id;
    }

    public static int getAddressId(Address address) {
        Integer addressId;

        if(address.getAddressId() == null) {
            addressId = getAddressId(address.getAddress(), address.getAddress2(), address.getCityId(), address.getPostalCode(),
                    address.getPhone());
        } else {
            addressId = address.getAddressId();
        }

        return addressId;
    }

    public static boolean createNewAddress(Address address) {
        boolean success = false;

        if(!Address.alreadyExists(address.getAddress(), address.getAddress2(), address.getCityId(), address.getPostalCode(),
                address.getPhone())) {

            try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()) {

                int result = st.executeUpdate("INSERT INTO address(address,address2,cityId,postalCode,phone,createDate,createdBy,lastUpdateBy) VALUES('" +
                        address.getAddress() + "','" + address.getAddress2() + "'," + address.getCityId() + ",'" + address.getPostalCode() + "','" + address.getPhone() +
                        "','" + LocalDateTime.now().toString() + "','" + Main.getUser().getUsername() + "','" + Main.getUser().getUsername() + "')");

                if(result > 0) {
                    int addressId = Address.getAddressId(address);
                    address.setAddressId(addressId);
                    success = true;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return success;
    }

    public static Address getAddress(String addressLine, String addressLine2, Integer cityId, String postalCode, String phoneNumber) {
        Address addressEntry = null;
        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM address WHERE address='" + addressLine +
                    "' AND address2='" + addressLine2 + "' AND cityId=" + cityId + " AND postalCode='" + postalCode +
                    "' AND phone='" + phoneNumber + "'");

            if(resultSet.next()) {
                addressEntry = new Address(resultSet.getInt("addressId"), resultSet.getString("address"),
                        resultSet.getString("address2"), resultSet.getInt("cityId"),
                        resultSet.getString("postalCode"), resultSet.getString("phone"), resultSet.getTimestamp("createDate").toLocalDateTime(),
                        resultSet.getString("createdBy"), resultSet.getTimestamp("lastUpdate").toLocalDateTime(),
                        resultSet.getString("lastUpdateBy"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return addressEntry;
    }

    public static Address getAddress(Integer addressId) {
        Address addressEntry = null;
        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM address WHERE addressId=" + addressId);

            if(resultSet.next()) {
                addressEntry = new Address(resultSet.getInt("addressId"), resultSet.getString("address"),
                        resultSet.getString("address2"), resultSet.getInt("cityId"),
                        resultSet.getString("postalCode"), resultSet.getString("phone"), resultSet.getTimestamp("createDate").toLocalDateTime(),
                        resultSet.getString("createdBy"), resultSet.getTimestamp("lastUpdate").toLocalDateTime(),
                        resultSet.getString("lastUpdateBy"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return addressEntry;
    }

    public static int countCityId(Integer cityId) throws CountEntriesException {

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT count(cityId) FROM address WHERE cityId=" + cityId);

            if(resultSet.next()) {
                return resultSet.getInt("count(cityId)");
            } else {
                return 0;
            }
        } catch (SQLException throwables) {
            throw new CountEntriesException();
        }

    }

    public static boolean deleteAddress(Integer addressId) {

        boolean success = false;
        Address address = Address.getAddress(addressId);

        if(Address.alreadyExists(address.getAddress(), address.getAddress2(), address.getCityId(), address.getPostalCode(),
            address.getPhone())) {

            try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()) {

                int result = st.executeUpdate("DELETE FROM address WHERE addressId=" + address.getAddressId());

                if(result > 0) {
                    success = true;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return success;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
}
