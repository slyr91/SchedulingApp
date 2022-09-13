package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import calendar.CallBackController;
import calendar.RecordManager;
import calendar.entries.Customer;
import calendar.entries.Record;
import exceptions.QuickDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logging.Logger;

public class AddressBookController implements CallBackController {

    private Customer selectedCustomer;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<Record, String> nameColumn;

    @FXML
    private TableView<Record> contactTable;

    @FXML
    private Button createContactButton;

    @FXML
    private Button editContactButton;

    @FXML
    private Button selectButton;
    private Consumer<CallBackController> returnAction;
    private CallBackController controller;

    @FXML
    void createNewContact(ActionEvent event) {
        try {
            Stage createContactStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ContactEditor.fxml"), resources);
            Parent root = loader.load();
            ContactEditorController controller = loader.getController();
            controller.onReturnAction(this, t -> refreshContacts());

            createContactStage.setScene(new Scene(root));
            createContactStage.setTitle("Create Contact");
            createContactStage.initOwner(createContactButton.getScene().getWindow());
            createContactStage.initModality(Modality.WINDOW_MODAL);
            createContactStage.show();

        } catch (IOException e) {
            QuickDialog.errorDialog(resources, "contactsOpenCreateWindowFailure", "contactsErrorTitle", "contactsErrorHeader");
            Logger.getInstance().error("There was an issue opening the create contact window.");
        }
    }

    @FXML
    void openEditContactWindow(ActionEvent event) {
        if(contactTable.getSelectionModel().getSelectedItem() != null) {

            try {
                Stage createContactStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ContactEditor.fxml"), resources);
                Parent root = loader.load();
                ContactEditorController controller = loader.getController();

                controller.setRecord(contactTable.getSelectionModel().getSelectedItem());
                controller.enableEditorMode();
                controller.onReturnAction(this, t -> refreshContacts());

                createContactStage.setScene(new Scene(root));
                createContactStage.setTitle("Create Contact");
                createContactStage.initOwner(createContactButton.getScene().getWindow());
                createContactStage.initModality(Modality.WINDOW_MODAL);
                createContactStage.show();

            } catch (IOException e) {
                QuickDialog.errorDialog(resources, "contactsOpenCreateWindowFailure", "contactsErrorTitle", "contactsErrorHeader");
                Logger.getInstance().error("There was an issue opening the create contact window.");
            }
        }
    }

    public void refreshContacts() {

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        List<Record> records = RecordManager.fetchRecords();

        if(records.isEmpty()) {
            contactTable.setPlaceholder(new Label(resources.getString("noCustomerRecords")));
            contactTable.getItems().clear();
        } else {
            contactTable.getItems().clear();
            contactTable.getItems().addAll(records);
        }
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    @FXML
    void setSelectedCustomer(ActionEvent event) {
        Record record = contactTable.getSelectionModel().getSelectedItem();

        if(record != null) {
            selectedCustomer = record.getCustomer();
            returnAction.accept(controller);
            selectButton.getScene().getWindow().hide();
        }
    }

    @Override
    public void onReturnAction(CallBackController controller, Consumer<CallBackController> action) {
        this.returnAction = action;
        this.controller = controller;
    }

    public void enableSelectorMode() {
        selectButton.setDisable(false);
        selectButton.setVisible(true);

        createContactButton.setDisable(true);
        createContactButton.setVisible(false);

        editContactButton.setDisable(true);
        editContactButton.setVisible(false);
    }

    @FXML
    void initialize() {
        assert nameColumn != null : "fx:id=\"nameColumn\" was not injected: check your FXML file 'AddressBook.fxml'.";
        assert contactTable != null : "fx:id=\"contactTable\" was not injected: check your FXML file 'AddressBook.fxml'.";
        assert createContactButton != null : "fx:id=\"createContactButton\" was not injected: check your FXML file 'AddressBook.fxml'.";
        assert editContactButton != null : "fx:id=\"editContactButton\" was not injected: check your FXML file 'AddressBook.fxml'.";
        assert selectButton != null : "fx:id=\"selectButton\" was not injected: check your FXML file 'AddressBook.fxml'.";

        refreshContacts();

    }
}
