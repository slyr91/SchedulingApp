package application;

import identity.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jdk.nashorn.internal.objects.annotations.Getter;
import logging.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;


public class Main extends Application {

    private static User userObj;

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
             Statement st = conn.createStatement()) {

            ResultSet result = st.executeQuery("SELECT * FROM user");
            if(!result.next()) {
                //Initialize the database
                String[] initializationStrings = DatabaseConnectionManager.getInitializationString();
                for(int i = 0; i < initializationStrings.length; i++) {
                    st.executeUpdate(initializationStrings[i]);
                }
                st.executeUpdate("INSERT INTO user VALUES(NULL,'test','test',1,'" + LocalDateTime.now().toString() + "','Daryl Arouchian',NULL,'Daryl Arouchian')");
            }

            result = st.executeQuery("SELECT * FROM user");
            System.out.println("--- USER TABLE ENTRIES ---");
            if(result.next()) {
                System.out.println("userId\tuserName\tpassword\tactive\tcreateDate\tcreatedBy\tlastUpdate\tlastUpdateBy");
                do {
                    System.out.println(result.getInt("userId") + "\t" + result.getString("userName") +
                            "\t" + result.getString("password") + "\t" + result.getInt("active") +
                            "\t" + result.getTimestamp("createDate") + "\t" + result.getString("createdBy") +
                            "\t" + result.getTimestamp("lastUpdate") + "\t" + result.getString("lastUpdateBy"));
                } while(result.next());
            }
            System.out.println("----------------------------------------------------------------------------------------");

            result = st.executeQuery("SELECT * FROM appointment");
            System.out.println("--- APPOINTMENT TABLE ENTRIES ---");
            if(result.next()) {
                System.out.println("appointmentId\tcustomerId\tuserId\ttitle\tdescription\tlocation\tcontact\ttype\turl\t" +
                        "start\tend\tcreateDate\tcreatedBy\tlastUpdate\tlastUpdateBy");
                do {

                    String description = result.getString("description");

                    System.out.println(result.getInt("appointmentId") + "\t" + result.getInt("customerId") +
                            "\t" + result.getInt("userId") + "\t" + result.getString("title") +
                            "\t" + ((description.length() > 10) ? description.substring(0,10) : description) + "\t" + result.getString("location") +
                            "\t" + result.getString("contact") + "\t" + result.getString("type") +
                            "\t" + result.getString("url") + "\t" + result.getTimestamp("start") +
                            "\t" + result.getTimestamp("end") + "\t" + result.getTimestamp("createDate") +
                            "\t" + result.getString("createdBy") + "\t" + result.getTimestamp("lastUpdate") +
                            "\t" + result.getString("lastUpdateBy"));
                } while(result.next());
            }
            System.out.println("----------------------------------------------------------------------------------------");

            result = st.executeQuery("SELECT * FROM customer");
            System.out.println("--- CUSTOMER TABLE ENTRIES ---");
            if(result.next()) {
                System.out.println("customerId\tcustomerName\taddressId\tactive\tcreateDate\tcreatedBy\tlastUpdate\tlastUpdateBy");
                do {
                    System.out.println(result.getInt("customerId") + "\t" + result.getString("customerName") +
                            "\t" + result.getInt("addressId") + "\t" + result.getInt("active") +
                            "\t" + result.getTimestamp("createDate") + "\t" + result.getString("createdBy") +
                            "\t" + result.getTimestamp("lastUpdate") + "\t" + result.getString("lastUpdateBy"));
                } while(result.next());
            }
            System.out.println("----------------------------------------------------------------------------------------");

            result = st.executeQuery("SELECT * FROM address");
            System.out.println("--- ADDRESS TABLE ENTRIES ---");
            if(result.next()) {
                System.out.println("addressId\taddress\taddress2\tcityId\tpostalCode\tphone\tcreateDate\tcreatedBy\tlastUpdate\tlastUpdateBy");
                do {
                    System.out.println(result.getInt("addressId") + "\t" + result.getString("address") +
                            "\t" + result.getString("address2") + "\t" + result.getInt("cityId") +
                            "\t" + result.getString("postalCode") + "\t" + result.getString("phone") +
                            "\t" + result.getTimestamp("createDate") + "\t" + result.getString("createdBy") +
                            "\t" + result.getTimestamp("lastUpdate") + "\t" + result.getString("lastUpdateBy"));
                } while(result.next());
            }
            System.out.println("----------------------------------------------------------------------------------------");

            result = st.executeQuery("SELECT * FROM city");
            System.out.println("--- CITY TABLE ENTRIES ---");
            if(result.next()) {
                System.out.println("cityId\tcity\tcountryId\tcreateDate\tcreatedBy\tlastUpdate\tlastUpdateBy");
                do {
                    System.out.println(result.getInt("cityId") + "\t" + result.getString("city") +
                            "\t" + result.getInt("countryId") + "\t" + result.getTimestamp("createDate") +
                            "\t" + result.getString("createdBy") + "\t" + result.getTimestamp("lastUpdate") +
                            "\t" + result.getString("lastUpdateBy"));
                } while(result.next());
            }
            System.out.println("----------------------------------------------------------------------------------------");

            result = st.executeQuery("SELECT * FROM country");
            System.out.println("--- COUNTRY TABLE ENTRIES ---");
            if(result.next()) {
                System.out.println("countryId\tcountry\tcreateDate\tcreatedBy\tlastUpdate\tlastUpdateBy");
                do {
                    System.out.println(result.getInt("countryId") + "\t" + result.getString("country") +
                            "\t" + result.getTimestamp("createDate") + "\t" + result.getString("createdBy") +
                            "\t" + result.getTimestamp("lastUpdate") + "\t" + result.getString("lastUpdateBy"));
                } while(result.next());
            }
            System.out.println("----------------------------------------------------------------------------------------");

        } catch (SQLException throwables) {
            Logger.getInstance().error("Failed to connect to MySQL server at start of program or failed to initialize data.");
            throwables.printStackTrace();
        }

        launch(args);
    }

    public static User getUser() {
        return userObj;
    }

    public static void setUser(User user) {
        userObj = user;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Locale locale = new Locale("en");

        if(Locale.getDefault().getLanguage().equals("es")) {
            locale = Locale.getDefault();
        }

        ResourceBundle resourceBundle = ResourceBundle.getBundle("internationalization.Lang", locale);

        Parent root = FXMLLoader.load(getClass().getResource("../view/Welcome.fxml"), resourceBundle);
        primaryStage.setTitle("Scheduling App");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
