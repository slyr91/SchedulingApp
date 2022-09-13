package calendar.entries;

import application.DatabaseConnectionManager;
import application.Main;
import exceptions.CountEntriesException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class City {
    private Integer cityId;
    private String city;
    private Integer countryId;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;

    public City(Integer cityId, String city, Integer countryId, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy) {
        this.cityId = cityId;
        this.city = city;
        this.countryId = countryId;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    public City() {
        this.cityId = null;
        this.city = null;
        this.countryId = null;
        this.createDate = null;
        this.createdBy = null;
        this.lastUpdate = null;
        this.lastUpdateBy = null;
    }

    public static boolean alreadyExists(String city, Integer countryId) {
        boolean exists = false;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT city, countryId FROM city WHERE city='" + city + "' AND countryId=" + countryId);

            if(resultSet.next()) {
                exists = true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return exists;
    }

    public static Integer getCityId(String city, Integer countryId) {
        Integer id = null;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT cityId FROM city WHERE city='" + city + "' AND countryId=" + countryId);

            if(resultSet.next()) {
                id = resultSet.getInt("cityId");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return id;
    }

    public static int getCityId(City city) {
        Integer cityId;

        if(city.getCityId() == null) {
            cityId = getCityId(city.getCity(), city.getCountryId());
        } else {
            cityId = city.cityId;
        }

        return cityId;
    }

    public static boolean createNewCity(City city) {
        boolean success = false;

        if(!City.alreadyExists(city.getCity(), city.getCountryId())) {

            try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()) {

                int result = st.executeUpdate("INSERT INTO city(city,countryId,createDate,createdBy,lastUpdateBy) VALUES('" +
                        city.getCity() + "'," + city.getCountryId() + ",'" + LocalDateTime.now().toString() + "','" + Main.getUser().getUsername() + "','" +
                        Main.getUser().getUsername() + "')");

                if(result > 0) {
                    int cityId = City.getCityId(city);
                    city.setCityId(cityId);
                    success = true;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return success;

    }

    public static City getCity(String city, Integer countryId) {
        City cityEntry = null;
        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM city WHERE city='" + city + "' AND countryId=" + countryId);

            if(resultSet.next()) {
                cityEntry = new City(resultSet.getInt("cityId"), resultSet.getString("city"),
                        resultSet.getInt("countryId"), resultSet.getTimestamp("createDate").toLocalDateTime(),
                        resultSet.getString("createdBy"), resultSet.getTimestamp("lastUpdate").toLocalDateTime(),
                        resultSet.getString("lastUpdateBy"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return cityEntry;

    }

    public static City getCity(Integer cityId) {
        City cityEntry = null;
        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM city WHERE cityId=" + cityId);

            if(resultSet.next()) {
                cityEntry = new City(resultSet.getInt("cityId"), resultSet.getString("city"),
                        resultSet.getInt("countryId"), resultSet.getTimestamp("createDate").toLocalDateTime(),
                        resultSet.getString("createdBy"), resultSet.getTimestamp("lastUpdate").toLocalDateTime(),
                        resultSet.getString("lastUpdateBy"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return cityEntry;
    }

    public static int countCountryId(Integer countryId) throws CountEntriesException {
        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT count(countryId) FROM city WHERE countryId=" + countryId);

            if(resultSet.next()) {
                return resultSet.getInt("count(countryId)");
            } else {
                return 0;
            }
        } catch (SQLException throwables) {
            throw new CountEntriesException();
        }
    }

    public static boolean deleteCity(Integer cityId) {
        boolean success = false;
        City city = City.getCity(cityId);

        if(City.alreadyExists(city.getCity(), city.getCountryId())) {

            try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()) {

                int result = st.executeUpdate("DELETE FROM city WHERE cityId=" + city.getCityId());

                if(result > 0) {
                    success = true;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return success;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
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
