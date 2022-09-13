package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import calendar.CallBackController;
import calendar.entries.Record;
import exceptions.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logging.Logger;

public class ContactEditorController implements CallBackController {

    private boolean editorMode = false;
    private Record record = null;
    private Consumer<CallBackController> returnAction = null;
    private CallBackController controller = null;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button cancelButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField addressLineField;

    @FXML
    private Button saveButton;

    @FXML
    private TextField countryField;

    @FXML
    private TextField addressLine2Field;

    @Override
    public void onReturnAction(CallBackController controller, Consumer<CallBackController> action) {
        returnAction = action;
        this.controller = controller;
    }

    public void setRecord(Record record) {
        this.record = record;

        fullNameField.setText(record.getCustomer().getCustomerName());
        addressLineField.setText(record.getAddress().getAddress());
        addressLine2Field.setText(record.getAddress().getAddress2());
        cityField.setText(record.getCity().getCity());
        countryField.setText(record.getCountry().getCountry());
        postalCodeField.setText(record.getAddress().getPostalCode());
        phoneNumberField.setText(record.getAddress().getPhone());

    }

    @FXML
    void saveContact(ActionEvent event) {
        //Check that all fields are filled
        if(fullNameField.getText().isEmpty() || addressLineField.getText().isEmpty() ||
            cityField.getText().isEmpty() || countryField.getText().isEmpty() || postalCodeField.getText().isEmpty() ||
            phoneNumberField.getText().isEmpty()) {
            QuickDialog.informationDialog(resources, "createContactRequiredFields", "createContactInfoTitle", "createContactInfoHeader");
            return;
        }
        saveButton.setDisable(true);

        if(editorMode) {

            if(record == null) {
                Logger.getInstance().error("Contact Editor opened in edit mode but either was not given a record to edit or failed to set it.");
            } else {
                try {

                    Record.updateExistingRecord(record, fullNameField.getText(), addressLineField.getText(), addressLine2Field.getText(),
                            cityField.getText(), countryField.getText(), postalCodeField.getText(), phoneNumberField.getText());
                } catch (CountryCreationException e) {
                    Logger.getInstance().error("There was an error creating a new country.");
                } catch (CityCreationException e) {
                    Logger.getInstance().error("There was an error creating a new city.");
                } catch (AddressCreationException e) {
                    Logger.getInstance().error("There was an error creating a new address.");
                } catch (CustomerUpdateException e) {
                    Logger.getInstance().error("There was an error creating a new customer.");
                } catch (CountEntriesException e) {
                    Logger.getInstance().error("There was an error counting the entries in the database.");
                }
            }

            saveButton.getScene().getWindow().hide();

            if(returnAction != null) {
                returnAction.accept(controller);
            }

        } else {
            Record record = null;
            try {
                record = Record.createNewRecord(fullNameField.getText(), addressLineField.getText(), addressLine2Field.getText(),
                        cityField.getText(), countryField.getText(), postalCodeField.getText(), phoneNumberField.getText());
            } catch (ContactAlreadyExistsException e) {
                Logger.getInstance().error("An attempt was made to save a new contact that already exists.");
                QuickDialog.errorDialog(resources, "contactEditorAlreadyExists", "contactEditorAlreadyExistsTitle",
                        "contactEditorAlreadyExistsHeader");
            }

            if(record == null) {
                QuickDialog.errorDialog(resources, "saveContactFailedMessage", "saveContactErrorTitle",
                        "saveContactErrorHeader");
            } else {
                saveButton.getScene().getWindow().hide();

                if(returnAction != null) {
                    returnAction.accept(controller);
                }
            }
        }

        saveButton.setDisable(false);
    }

    @FXML
    void cancelEditor(ActionEvent event) {
        cancelButton.getScene().getWindow().hide();

        if(returnAction != null) {
            returnAction.accept(controller);
        }
    }

    @FXML
    void deleteContact(ActionEvent event) {

        if(editorMode) {

            if(record == null) {
                Logger.getInstance().error("Contact Editor opened in edit mode but either was not given a record to edit or failed to set it.");
            } else {
                try {
                    Record.deleteRecord(record);
                } catch (CountEntriesException e) {
                    Logger.getInstance().error("There was an error counting the entries in the database.");
                } catch (CustomerDeletionException e) {
                    Logger.getInstance().error("There was an issue deleting the customer record. Record might not have been deleted.");
                }
            }
        }

        saveButton.getScene().getWindow().hide();

        if(returnAction != null) {
            returnAction.accept(controller);
        }

    }

    public void enableEditorMode() {
        enableEditorMode(true);
    }

    public void enableEditorMode(boolean enabled) {
        editorMode = enabled;
        deleteButton.setDisable(false);
        deleteButton.setVisible(true);
    }

    public void enableViewMode() {
        editorMode=false;
        deleteButton.setDisable(true);
        deleteButton.setVisible(false);
        saveButton.setDisable(true);
        saveButton.setVisible(false);
        cancelButton.setDisable(true);
        cancelButton.setVisible(false);

        fullNameField.setEditable(false);
        addressLineField.setEditable(false);
        addressLine2Field.setEditable(false);
        cityField.setEditable(false);
        countryField.setEditable(false);
        postalCodeField.setEditable(false);
        phoneNumberField.setEditable(false);
    }

    @FXML
    void initialize() {
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'ContactEditor.fxml'.";
        assert deleteButton != null : "fx:id=\"deleteButton\" was not injected: check your FXML file 'ContactEditor.fxml'.";
        assert phoneNumberField != null : "fx:id=\"phoneNumberField\" was not injected: check your FXML file 'ContactEditor.fxml'.";
        assert fullNameField != null : "fx:id=\"fullNameField\" was not injected: check your FXML file 'ContactEditor.fxml'.";
        assert cityField != null : "fx:id=\"cityField\" was not injected: check your FXML file 'ContactEditor.fxml'.";
        assert postalCodeField != null : "fx:id=\"postalCodeField\" was not injected: check your FXML file 'ContactEditor.fxml'.";
        assert addressLineField != null : "fx:id=\"addressLineField\" was not injected: check your FXML file 'ContactEditor.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'ContactEditor.fxml'.";
        assert countryField != null : "fx:id=\"countryField\" was not injected: check your FXML file 'ContactEditor.fxml'.";
        assert addressLine2Field != null : "fx:id=\"addressLine2Field\" was not injected: check your FXML file 'ContactEditor.fxml'.";

    }
}
