package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import calendar.Appointment;
import calendar.CallBackController;
import calendar.RecordManager;
import calendar.entries.Customer;
import calendar.entries.Record;
import exceptions.AppointmentOutsideBusinessHours;
import exceptions.AppointmentOverlapException;
import exceptions.QuickDialog;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logging.Logger;

public class AppointmentEditorController implements CallBackController {

    private boolean editMode = false;
    private Appointment appointment = null;
    private Customer customer = null;
    private Consumer<CallBackController> returnAction;
    private CallBackController controller;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button selectCustomerButton;

    @FXML
    private TextField locationField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField titleField;

    @FXML
    private Button viewCustomerButton;

    @FXML
    private ComboBox<String> endTimeComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<String> startTimeComboBox;

    @FXML
    private TextField customerField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button cancelButton;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button deleteAppointment;

    @FXML
    private TextField contactField;

    @FXML
    private Button saveButton;

    @FXML
    void cancelEdit(ActionEvent event) {
        cancelButton.getScene().getWindow().hide();
    }

    @FXML
    void saveAppointment(ActionEvent event) {
        if(titleField.getText().isEmpty() || customerField.getText().isEmpty() || descriptionTextArea.getText().isEmpty()
        || typeComboBox.getSelectionModel().getSelectedItem() == null || startTimeComboBox.getSelectionModel().getSelectedItem() == null ||
        endTimeComboBox.getSelectionModel().getSelectedItem() == null) {
            QuickDialog.informationDialog(resources, "appointmentSaveRequiredFieldsMessage", "appointmentInfoTitle", "appointmentInfoHeader");
            return;
        }

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
                .appendPattern("h:mm a").toFormatter(Locale.getDefault());

        LocalDateTime startDateTime = startDatePicker.getValue().atTime(LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(),
                dateTimeFormatter));
        LocalDateTime endDateTime = endDatePicker.getValue().atTime(LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem(),
                dateTimeFormatter));

        if(endDateTime.isBefore(startDateTime)) {
            QuickDialog.warningDialog(resources, "appointmentSaveInvalidStartMessage", "appointmentSaveWarningTitle", "appointmentSaveWarningHeader");
            return;
        }

        if(editMode) {
            try {
                Appointment.updateExistingAppointment(appointment, customer.getCustomerId(), titleField.getText(), descriptionTextArea.getText(),
                        locationField.getText(), contactField.getText(), typeComboBox.getSelectionModel().getSelectedItem(),
                        startDateTime, endDateTime);
            } catch (AppointmentOverlapException e) {
                QuickDialog.informationDialog(resources, "appointmentOverlapMessage", "appointmentEditorInfoTitle",
                        "appointmentEditorInfoHeader");
                Logger.getInstance().information(Main.getUser().getUsername() + " tried to update an appointment that overlaps" +
                        " with another appointment.");
                return;
            } catch (AppointmentOutsideBusinessHours appointmentOutsideBusinessHours) {
                QuickDialog.informationDialog(resources, "appointmentOutsideBusinessHoursMessage", "appointmentEditorInfoTitle",
                        "appointmentEditorInfoHeader");
                Logger.getInstance().information(Main.getUser().getUsername() + " tried to update an appointment that is" +
                        " outside of business hours.");
                return;
            }
        } else {

            try {
                Appointment.createNewAppointment(customer.getCustomerId(), titleField.getText(), descriptionTextArea.getText(),
                        locationField.getText(), contactField.getText(), typeComboBox.getSelectionModel().getSelectedItem(),
                        startDateTime, endDateTime);
            } catch (AppointmentOverlapException e) {
                QuickDialog.informationDialog(resources, "appointmentOverlapMessage", "appointmentEditorInfoTitle",
                        "appointmentEditorInfoHeader");
                Logger.getInstance().information(Main.getUser().getUsername() + " tried to create an appointment that overlaps" +
                        " with another appointment.");
                return;
            } catch (AppointmentOutsideBusinessHours appointmentOutsideBusinessHours) {
                QuickDialog.informationDialog(resources, "appointmentOutsideBusinessHoursMessage", "appointmentEditorInfoTitle",
                        "appointmentEditorInfoHeader");
                Logger.getInstance().information(Main.getUser().getUsername() + " tried to create an appointment that is" +
                        " outside of business hours.");
                return;
            }
        }

        returnAction.accept(controller);
        saveButton.getScene().getWindow().hide();

    }

    @FXML
    void delete(ActionEvent event) {
        if(appointment == null) {
            QuickDialog.errorDialog(resources, "appointmentEditorFailedDelete", "appointmentEditorDeleteTitle", "appointmentEditorDeleteHeader");
            Logger.getInstance().error("The appointment editor was loaded in edit mode without setting the appointment.");
        }

        if(Appointment.deleteAppointment(appointment)) {
            returnAction.accept(controller);
            deleteAppointment.getScene().getWindow().hide();
        }
    }

    @FXML
    void selectCustomer(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/AddressBook.fxml"), resources);
            Stage addressBookStage = new Stage();
            Parent root = loader.load();
            AddressBookController controller = loader.getController();

            controller.enableSelectorMode();
            controller.onReturnAction(this, c -> this.setCustomer(controller.getSelectedCustomer()));

            addressBookStage.setScene(new Scene(root));
            addressBookStage.setTitle("Address Book");

            addressBookStage.show();
        } catch (IOException e) {
            QuickDialog.errorDialog(resources, "appointmentEditorFailedOpen", "appointmentEditorErrorTitle",
                    "appointmentEditorErrorHeader");
            Logger.getInstance().error("Failed to open the addressbook from the new appointment editor.");
        }

    }

    public void enableEditMode() {
        editMode = true;

        deleteAppointment.setDisable(false);
        deleteAppointment.setVisible(true);
    }

    @FXML
    void viewCustomer(ActionEvent event) {
        if(customer == null) {
            return;
        }

        Record record = new Record(customer);
        try {
            Stage customerStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ContactEditor.fxml"), resources);
            Parent root = loader.load();
            ContactEditorController controller = loader.getController();

            controller.setRecord(record);
            controller.enableViewMode();

            customerStage.setScene(new Scene(root));
            customerStage.show();

        } catch (IOException e) {
            QuickDialog.errorDialog(resources, "appointmentViewCustomerFailed", "appointmentEditorErrorTitle",
                    "appointmentEditorErrorHeader");
            Logger.getInstance().error("Failed to open the view customer window from the new appointment editor.");
        }


        //TODO: Open a slimmed down version of the contact editor to just view the customer data.

    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        Customer customer = Customer.getCustomer(appointment.getCustomerID());

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
                .appendPattern("h:mm a").toFormatter(Locale.getDefault());

        titleField.setText(appointment.getTitle());
        setCustomer(customer);
        locationField.setText(appointment.getLocation());
        contactField.setText(appointment.getContact());
        typeComboBox.getSelectionModel().select(appointment.getType());
        startDatePicker.setValue(appointment.getStart().toLocalDate());
        endDatePicker.setValue(appointment.getEnd().toLocalDate());
        startTimeComboBox.getSelectionModel().select(appointment.getStart().toLocalTime().format(dateTimeFormatter).toLowerCase());
        endTimeComboBox.getSelectionModel().select(appointment.getEnd().toLocalTime().format(dateTimeFormatter).toLowerCase());
        descriptionTextArea.setText(appointment.getDescription());
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        customerField.setText(customer.getCustomerName());
    }


    @Override
    public void onReturnAction(CallBackController controller, Consumer<CallBackController> action) {
        returnAction = action;
        this.controller = controller;
    }

    @FXML
    void initialize() {
        assert selectCustomerButton != null : "fx:id=\"selectCustomerButton\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert locationField != null : "fx:id=\"locationField\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert typeComboBox != null : "fx:id=\"typeComboBox\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert titleField != null : "fx:id=\"titleField\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert viewCustomerButton != null : "fx:id=\"viewCustomerButton\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert endTimeComboBox != null : "fx:id=\"endTimeComboBox\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert startDatePicker != null : "fx:id=\"startDatePicker\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert startTimeComboBox != null : "fx:id=\"startTimeComboBox\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert customerField != null : "fx:id=\"customerField\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert descriptionTextArea != null : "fx:id=\"descriptionTextArea\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert endDatePicker != null : "fx:id=\"endDatePicker\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert deleteAppointment != null : "fx:id=\"deleteAppointment\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert contactField != null : "fx:id=\"contactField\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'AppointmentEditor.fxml'.";

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());

        BooleanBinding customerFieldFilled = Bindings.createBooleanBinding(() -> customerField.getText().isEmpty(), customerField.textProperty());

        viewCustomerButton.disableProperty().bind(customerFieldFilled);
    }
}
