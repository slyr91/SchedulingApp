package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import application.DatabaseConnectionManager;
import application.Main;
import exceptions.AccountInactiveException;
import exceptions.IncorrectUserOrPassException;
import exceptions.QuickDialog;
import identity.User;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import logging.Logger;

public class WelcomeController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField userNameField;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    void login(ActionEvent event) {
        if(userNameField.getText().equals("")) {
            throw new RuntimeException("Username Field can't be blank.");
        }
        if(passwordField.getText().equals("")) {
            throw new RuntimeException("Password Field can't be blank.");
        }

        String username = userNameField.getText();
        String password = passwordField.getText();

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet result = st.executeQuery("SELECT * FROM user WHERE userName='" + username + "'");

            if(result.next()) {
                if(result.getString("password").equals(password)) {
                    if(result.getInt("active") == 1) {

                        Main.setUser(new User(result.getInt("userId"), username));
                        Logger.getInstance().information(username + " has successfully logged into the application.");
                        openCalendarWindow();

                        Window loginWindow = loginButton.getScene().getWindow();
                        loginWindow.hide();

                    } else {
                        throw new AccountInactiveException(username);
                    }

                } else {
                    throw new IncorrectUserOrPassException(username);
                }

            } else {
                throw new IncorrectUserOrPassException(username);
            }

        } catch (SQLException e) {
            Logger.getInstance().error("Something went wrong with the connection to the MySQL server.");
            QuickDialog.errorDialog(resources, "loginSQLConnectionError", "loginErrorTitle", "loginErrorHeader");
            e.printStackTrace();
        } catch (IncorrectUserOrPassException e) {
            QuickDialog.errorDialog(resources, "loginIncorrectLogin", "loginErrorTitle", "loginErrorHeader");
            Logger.getInstance().warning("Incorrect username or password was entered. Username used was '" + e.getUsername() + "'");
        } catch (AccountInactiveException e) {
            QuickDialog.errorDialog(resources, "loginInactiveAccount", "loginErrorTitle", "loginErrorHeader");
            Logger.getInstance().warning("Attempted to log in with an inactive account '" + e.getUsername() + "'");
        }

    }

    private void openCalendarWindow() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../view/CalendarFrame.fxml"), resources);
            Stage calendarStage = new Stage();
            calendarStage.setTitle("Scheduling App");
            calendarStage.setScene(new Scene(root));
            calendarStage.setMaximized(true);
            calendarStage.show();
        } catch (IOException e) {
            Logger.getInstance().error("Failed to open the Calendar Windows.");
            QuickDialog.errorDialog(resources, "calendarErrorOpeningCalendar", "loginErrorTitle", "loginErrorHeader");
            throw new Error(e);
        }

    }



    @FXML
    void initialize() {
        assert userNameField != null : "fx:id=\"userNameField\" was not injected: check your FXML file 'Welcome.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'Welcome.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'Welcome.fxml'.";

        BooleanBinding userNameFieldFilled = Bindings.createBooleanBinding(() -> !userNameField.getText().equals(""), userNameField.textProperty());
        BooleanBinding passwordFieldFilled = Bindings.createBooleanBinding(() -> !passwordField.getText().equals(""), passwordField.textProperty());

        loginButton.disableProperty().bind(userNameFieldFilled.not().or(passwordFieldFilled.not()));
    }

}
