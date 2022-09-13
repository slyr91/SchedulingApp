package calendar.entries;

import application.DatabaseConnectionManager;
import application.Main;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Country {
    private Integer countryId;
    private String country;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;

    public Country(Integer countryId, String country, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy) {
        this.countryId = countryId;
        this.country = country;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    public Country() {
        this.countryId = null;
        this.country = null;
        this.createDate = null;
        this.createdBy = null;
        this.lastUpdate = null;
        this.lastUpdateBy = null;
    }

    public static boolean alreadyExists(String country) {
        boolean exists = false;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT country FROM country WHERE country='" + country + "'");

            if(resultSet.next()) {
                exists = true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return exists;
    }

    public static Integer getCountryId(String country) {
        Integer id = null;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT countryId FROM country WHERE country='" + country + "'");

            if(resultSet.next()) {
                id = resultSet.getInt("countryId");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return id;
    }

    public static Integer getCountryId(Country country) {
        Integer countryId;

        if(country.getCountryId() == null) {
            countryId = getCountryId(country.getCountry());
        } else {
            countryId = country.countryId;
        }

        return countryId;
    }

    public static boolean createNewCountry(Country country) {
        boolean success = false;

        if(!Country.alreadyExists(country.getCountry())) {

            try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()) {

                int result = st.executeUpdate("INSERT INTO country(country,createDate,createdBy,lastUpdateBy) VALUES('" +
                        country.getCountry() + "','" + LocalDateTime.now().toString() + "','" + Main.getUser().getUsername() + "','" +
                        Main.getUser().getUsername() + "')");

                if(result > 0) {
                    int countryId = Country.getCountryId(country.getCountry());
                    country.setCountryId(countryId);
                    success = true;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return success;
    }

    public static boolean deleteCountry(Integer countryId) {
        boolean success = false;
        Country country = Country.getCountry(countryId);

        if(Country.alreadyExists(country.getCountry())) {

            try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()) {

                int result = st.executeUpdate("DELETE FROM country WHERE countryId=" + country.getCountryId());

                if(result > 0) {
                    success = true;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return success;
    }

    public static Country getCountry(String country) {
        Country countryEntry = null;
        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM country WHERE country='" + country + "'");

            if(resultSet.next()) {
                countryEntry = new Country(resultSet.getInt("countryId"), resultSet.getString("country"),
                        resultSet.getTimestamp("createDate").toLocalDateTime(), resultSet.getString("createdBy"),
                        resultSet.getTimestamp("lastUpdate").toLocalDateTime(), resultSet.getString("lastUpdateBy"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return countryEntry;

    }

    public static Country getCountry(Integer countryId) {
        Country countryEntry = null;
        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM country WHERE countryId=" + countryId);

            if(resultSet.next()) {
                countryEntry = new Country(resultSet.getInt("countryId"), resultSet.getString("country"),
                        resultSet.getTimestamp("createDate").toLocalDateTime(), resultSet.getString("createdBy"),
                        resultSet.getTimestamp("lastUpdate").toLocalDateTime(), resultSet.getString("lastUpdateBy"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return countryEntry;
    }


    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
