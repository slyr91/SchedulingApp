package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import calendar.AppointmentManager;
import calendar.CallBackController;
import calendar.wrappers.Month;
import calendar.wrappers.Week;
import exceptions.QuickDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logging.Logger;

public class CalendarFrameController implements CallBackController {

    private boolean isWeekView = false;
    private LocalDate todaysDate;
    private LocalDate currentDate;
    private Consumer<CallBackController> returnAction;
    private CallBackController controller;

    private AppointmentManager appointmentManager;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button nextButton;

    @FXML
    private Button weekViewButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button addressBookButton;

    @FXML
    private Button newEventButton;

    @FXML
    private GridPane menuGrid;

    @FXML
    private TitledPane calendarArea;

    @FXML
    private Button reportsButton;

    @FXML
    private Button monthViewButton;

    @FXML
    private Button todayButton;

    @FXML
    void createNewEvent(ActionEvent event) {

        try{
            Stage appointmentStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/AppointmentEditor.fxml"), resources);
            Parent root = loader.load();
            AppointmentEditorController controller = loader.getController();
            controller.onReturnAction(this, c -> { //Lambda here allows me to change the behavior of the program as needed without needing to creating one off methods.
                appointmentManager.invalidateCache();
                if(isWeekView) {
                    weekViewButton.fire();
                } else {
                    monthViewButton.fire();
                }
            });
            appointmentStage.setTitle("Create New Event");
            appointmentStage.setScene(new Scene(root));
            appointmentStage.initOwner(newEventButton.getScene().getWindow());
            appointmentStage.initModality(Modality.WINDOW_MODAL);
            appointmentStage.show();
        } catch (IOException e) {
            QuickDialog.errorDialog(resources, "calendarFailedNewEvent", "calendarErrorTitle", "calendarErrorHeader");
            Logger.getInstance().error("Failed to load the new event window.");
        }

    }

    @FXML
    void openAddressBook(ActionEvent event) {
        try {
            Stage addressBookStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../view/AddressBook.fxml"), resources);
            addressBookStage.setTitle("Address book");
            addressBookStage.setScene(new Scene(root));
            addressBookStage.initOwner(addressBookButton.getScene().getWindow());
            addressBookStage.initModality(Modality.WINDOW_MODAL);
            addressBookStage.show();
        } catch (IOException e) {
            QuickDialog.errorDialog(resources, "calendarFailedAddressBook", "calendarErrorTitle", "calendarErrorHeader");
            Logger.getInstance().error("Failed to load the address book window.");
        }

    }

    @FXML
    void setViewToWeek(ActionEvent event) {
        if(!isWeekView) {
            isWeekView = true;
        }

        Week week = appointmentManager.getWeekOf(currentDate);

        calendarArea.setText(resources.getString("calendarWeekOf") + " " + week.getWeekOf());
        calendarArea.setContent(week.getWeekNode());

    }

    @FXML
    void setViewToMonth(ActionEvent event) {
        if(isWeekView) {
            isWeekView = false;
        }

        Month month = appointmentManager.getMonthOf(currentDate);

        calendarArea.setText(month.getMonthOfDate().format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())) + " " + month.getYear());
        calendarArea.setContent(month.getMonthNode());

    }

    @FXML
    void navBack(ActionEvent event) {
        if(isWeekView) {
            currentDate = currentDate.minusWeeks(1);
            weekViewButton.fire();
        } else {
            currentDate = currentDate.minusMonths(1);
            monthViewButton.fire();
        }
    }

    @FXML
    void navNext(ActionEvent event) {
        if(isWeekView) {
            currentDate = currentDate.plusWeeks(1);
            weekViewButton.fire();
        } else {
            currentDate = currentDate.plusMonths(1);
            monthViewButton.fire();
        }
    }

    @FXML
    void navToday(ActionEvent event) {
        currentDate = todaysDate;

        if(isWeekView) {
            weekViewButton.fire();
        } else {
            monthViewButton.fire();
        }

    }

    public void refreshAppointments() {
        if(isWeekView) {
            weekViewButton.fire();
        } else {
            monthViewButton.fire();
        }
    }

    @Override
    public void onReturnAction(CallBackController controller, Consumer<CallBackController> action) {
        this.returnAction = action;
        this.controller = controller;
    }

    @FXML
    void openReports(ActionEvent event) {
        try {
            Stage reportsStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ReportsView.fxml"), resources);
            Parent root = loader.load();
            reportsStage.setScene(new Scene(root));
            reportsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        assert nextButton != null : "fx:id=\"nextButton\" was not injected: check your FXML file 'CalendarFrame.fxml'.";
        assert weekViewButton != null : "fx:id=\"weekViewButton\" was not injected: check your FXML file 'CalendarFrame.fxml'.";
        assert previousButton != null : "fx:id=\"previousButton\" was not injected: check your FXML file 'CalendarFrame.fxml'.";
        assert addressBookButton != null : "fx:id=\"addressBookButton\" was not injected: check your FXML file 'CalendarFrame.fxml'.";
        assert newEventButton != null : "fx:id=\"newEventButton\" was not injected: check your FXML file 'CalendarFrame.fxml'.";
        assert menuGrid != null : "fx:id=\"menuGrid\" was not injected: check your FXML file 'CalendarFrame.fxml'.";
        assert calendarArea != null : "fx:id=\"calendarArea\" was not injected: check your FXML file 'CalendarFrame.fxml'.";
        assert reportsButton != null : "fx:id=\"reportsButton\" was not injected: check your FXML file 'CalendarFrame.fxml'.";
        assert monthViewButton != null : "fx:id=\"monthViewButton\" was not injected: check your FXML file 'CalendarFrame.fxml'.";
        assert todayButton != null : "fx:id=\"todayButton\" was not injected: check your FXML file 'CalendarFrame.fxml'.";

        appointmentManager = new AppointmentManager(resources, this);
        todaysDate = LocalDate.now();
        currentDate = todaysDate;

        weekViewButton.fireEvent(new ActionEvent());

        if(appointmentManager.isAppointmentScheduledSoon()) {
            QuickDialog.informationDialog(resources, "appointmentSoonMessage", "appointmentSoonTitle",
                                            "appointmentSoonHeader");
        }


    }
}
