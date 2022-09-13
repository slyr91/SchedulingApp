package controllers;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.DatabaseConnectionManager;
import application.Main;
import calendar.Appointment;
import calendar.SimpleAppointmentType;
import calendar.entries.Customer;
import calendar.entries.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class ReportsViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button appByMonthButton;

    @FXML
    private TextArea reportArea;

    @FXML
    private Button averageAppButton;

    @FXML
    private ComboBox<String> yearChoiceBox;

    @FXML
    private Button scheduleButton;

    @FXML
    void generateAppByMonthReport(ActionEvent event) {
        if(yearChoiceBox.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        ArrayList<SimpleAppointmentType> appointmentTypes = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM appointment WHERE start BETWEEN '" + LocalDate.of(Integer.parseInt(yearChoiceBox.getSelectionModel().getSelectedItem()), Month.JANUARY, 1) +
                    "' AND '" + LocalDate.of(Integer.parseInt(yearChoiceBox.getSelectionModel().getSelectedItem()), Month.DECEMBER, 31) + "'");

            while(resultSet.next()) {
                SimpleAppointmentType appointmentType = new SimpleAppointmentType();
                appointmentType.setType(resultSet.getString("type"));
                appointmentType.setMonth(resultSet.getTimestamp("start").toLocalDateTime().getMonth());
                appointmentTypes.add(appointmentType);
            }

            reportArea.clear();
            reportArea.appendText("\t\t\tJan\tFeb\tMar\tApr\tMay\tJun\tJul\tAug\tSep\tOct\tNov\tDec");
            reportArea.appendText("\n");
            reportArea.appendText("------------------------------------------------------------------------------------------------------");
            reportArea.appendText("\n");
            reportArea.appendText("Routine:\t");
            for (Month month: Month.values()) {
                reportArea.appendText("\t");
                long count = appointmentTypes.stream().filter(s -> s.getMonth().equals(month)).filter(s -> s.getType().equals("Routine")).count(); //By using lambdas in these streams I was able to quickly specify which month and which type I wanted to count without creating complex loops.
                reportArea.appendText(String.valueOf(count));
            }
            reportArea.appendText("\n");
            reportArea.appendText("Non-Routine:");
            for (Month month: Month.values()) {
                reportArea.appendText("\t");
                long count = appointmentTypes.stream().filter(s -> s.getMonth().equals(month)).filter(s -> s.getType().equals("Non-Routine")).count();
                reportArea.appendText(String.valueOf(count));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    void generateScheduleReport(ActionEvent event) {
        reportArea.clear();

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ArrayList<User> users = new ArrayList<>();

            ResultSet resultSet = st.executeQuery("SELECT * FROM user");

            while(resultSet.next()) {
                User user = new User(resultSet.getInt("userId"), resultSet.getString("userName"), resultSet.getString("password"),
                        resultSet.getInt("active"), resultSet.getTimestamp("createDate").toLocalDateTime(), resultSet.getString("createdBy"),
                        resultSet.getTimestamp("lastUpdate").toLocalDateTime(), resultSet.getString("lastUpdateBy"));
                users.add(user);
            }

            if(!users.isEmpty()) {
                for (User user: users) {
                    ArrayList<Appointment> appointments = new ArrayList<>();

                    resultSet = st.executeQuery("SELECT * FROM appointment WHERE userId=" + user.getUserId() + " AND start >= '" + LocalDate.now() + "'");

                    while(resultSet.next()) {
                        Appointment appointment = new Appointment(resultSet.getInt("appointmentId"), resultSet.getInt("customerId"),
                                resultSet.getInt("userId"), resultSet.getString("title"), resultSet.getString("description"),
                                resultSet.getString("location"), resultSet.getString("contact"), resultSet.getString("type"),
                                resultSet.getTimestamp("start").toLocalDateTime(), resultSet.getTimestamp("end").toLocalDateTime(),
                                resultSet.getTimestamp("createDate").toLocalDateTime().toLocalDate(), resultSet.getString("createdBy"),
                                resultSet.getTimestamp("lastUpdate").toLocalDateTime(), resultSet.getString("lastUpdateBy"));

                        appointments.add(appointment);
                    }

                    reportArea.appendText("\t\t\t\tUser: " + user.getUserName() + "\n");
                    reportArea.appendText("_________________________________________________________________________________\n");

                    for(LocalDate currentDate = LocalDate.now(), lastDate = currentDate.plusMonths(1).plusDays(1);
                        currentDate.isBefore(lastDate); currentDate = currentDate.plusDays(1)) {

                        LocalDate finalCurrentDate = currentDate;
                        appointments.stream().filter(a -> a.getStart().toLocalDate().isEqual(finalCurrentDate)).forEach(a -> {
                            reportArea.appendText(finalCurrentDate.toString() + " -\t" + "From " + a.getStart().toLocalTime() + " to " +
                                    a.getEnd().toLocalTime() + " with " + Customer.getCustomer(a.getCustomerID()).getCustomerName() +
                                    " titled: " + ((a.getTitle().length() < 10) ? a.getTitle() : a.getTitle().substring(0, 20) + "...") + "\n");
                        });

                    }
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    void generateAverageAppsReport(ActionEvent event) {
        reportArea.clear();

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM appointment WHERE userId=" + Main.getUser().getUserID() +
                    " AND start BETWEEN '" + LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1) + "' AND '" +
                    LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 31) + "'");

            ArrayList<Appointment> appointments = new ArrayList<>();

            while(resultSet.next()) {
                Appointment appointment = new Appointment(resultSet.getInt("appointmentId"), resultSet.getInt("customerId"),
                        resultSet.getInt("userId"), resultSet.getString("title"), resultSet.getString("description"),
                        resultSet.getString("location"), resultSet.getString("contact"), resultSet.getString("type"),
                        resultSet.getTimestamp("start").toLocalDateTime(), resultSet.getTimestamp("end").toLocalDateTime(),
                        resultSet.getTimestamp("createDate").toLocalDateTime().toLocalDate(), resultSet.getString("createdBy"),
                        resultSet.getTimestamp("lastUpdate").toLocalDateTime(), resultSet.getString("lastUpdateBy"));

                appointments.add(appointment);
            }

            reportArea.appendText("Average appointments per month: ");
            int totalAppointments = 0;
            int numberOfZeroMonths = 0;
            for (Month month: Month.values()) {
                long count = appointments.stream().filter(a -> a.getStart().toLocalDate().getMonth().equals(month)).count();

                if(count == 0) {
                    numberOfZeroMonths++;
                } else {
                    totalAppointments += count;
                }
            }

            int averageMonthlyAppointments = totalAppointments / (12 - numberOfZeroMonths);

            reportArea.appendText(averageMonthlyAppointments + "\n\n");

            reportArea.appendText("______________________________________________________________________________________\n");
            reportArea.appendText("\t\t\t\tAverage appointments per day\n");
            reportArea.appendText("Jan\tFeb\tMar\tApr\tMay\tJun\tJul\tAug\tSep\tOct\tNov\tDec\n");

            for (Month month: Month.values()) {
                int totalAppointmentsPerDay = 0;
                int numberOfZeroDays = 0;

                for(LocalDate currentDate = LocalDate.of(LocalDate.now().getYear(), month, 1), lastDate = currentDate.plusMonths(1);
                    currentDate.isBefore(lastDate); currentDate = currentDate.plusDays(1)) {

                    LocalDate finalCurrentDate = currentDate;
                    long count = appointments.stream().filter(a -> a.getStart().toLocalDate().isEqual(finalCurrentDate)).count();

                    if(count == 0) {
                        numberOfZeroDays++;
                    } else {
                        totalAppointmentsPerDay += count;
                    }
                }

                int daysCount = month.length(LocalDate.now().isLeapYear()) - numberOfZeroDays;
                if(daysCount == 0) {
                    reportArea.appendText(0 + "\t");
                } else {
                    int averageDailyAppointments = totalAppointmentsPerDay / (month.length(LocalDate.now().isLeapYear()) - numberOfZeroDays);

                    reportArea.appendText(averageDailyAppointments + "\t");
                }

            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        assert appByMonthButton != null : "fx:id=\"appByMonthButton\" was not injected: check your FXML file 'ReportsView.fxml'.";
        assert reportArea != null : "fx:id=\"reportArea\" was not injected: check your FXML file 'ReportsView.fxml'.";
        assert averageAppButton != null : "fx:id=\"averageAppButton\" was not injected: check your FXML file 'ReportsView.fxml'.";
        assert yearChoiceBox != null : "fx:id=\"yearChoiceBox\" was not injected: check your FXML file 'ReportsView.fxml'.";
        assert scheduleButton != null : "fx:id=\"scheduleButton\" was not injected: check your FXML file 'ReportsView.fxml'.";

        for(int i = 0, year = 2000; i <= LocalDate.now().getYear() - year; i++) {
            yearChoiceBox.getItems().add(Integer.toString(year + i));
        }
    }
}
