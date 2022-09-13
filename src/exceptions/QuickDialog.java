package exceptions;

import javafx.scene.control.Alert;

import java.util.ResourceBundle;

public class QuickDialog {

    public static void errorDialog(ResourceBundle resources, String messageKey, String titleKey, String headerKey) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resources.getString(titleKey));
        alert.setHeaderText(resources.getString(headerKey));
        alert.setContentText(resources.getString(messageKey));
        alert.showAndWait();
    }

    public static void warningDialog(ResourceBundle resources, String messageKey, String titleKey, String headerKey) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(resources.getString(titleKey));
        alert.setHeaderText(resources.getString(headerKey));
        alert.setContentText(resources.getString(messageKey));
        alert.showAndWait();
    }

    public static void informationDialog(ResourceBundle resources, String messageKey, String titleKey, String headerKey) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(resources.getString(titleKey));
        alert.setHeaderText(resources.getString(headerKey));
        alert.setContentText(resources.getString(messageKey));
        alert.showAndWait();
    }
}
