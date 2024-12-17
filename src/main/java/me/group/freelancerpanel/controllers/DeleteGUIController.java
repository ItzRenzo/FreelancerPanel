package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteGUIController {

    @FXML
    private TextFlow IDText;

    @FXML
    private TextFlow EnterIDText;

    @FXML
    private TextField IDInputField;

    private String tableName; // Table to delete from, passed dynamically

    private Runnable reloadTableMethod; // A reference to the method that reloads the table data

    public void setTableName(String tableName) {
        this.tableName = tableName;

        // Update dynamic UI text based on the table name
        Text idText = new Text("Delete " + tableName + " by ID");
        idText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 18px; -fx-font-weight: bold;");

        Text enterIDText = new Text("Enter " + tableName + " ID:");
        enterIDText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 14px;");

        IDText.getChildren().clear();
        IDText.getChildren().add(idText);

        EnterIDText.getChildren().clear();
        EnterIDText.getChildren().add(enterIDText);
    }

    public void setReloadTableMethod(Runnable reloadMethod) {
        this.reloadTableMethod = reloadMethod; // Set the method to be called after deletion
    }

    @FXML
    public void DeleteClicked(MouseEvent event) {
        String idInput = IDInputField.getText();

        if (idInput == null || idInput.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "No ID Entered", "Please enter a valid ID to delete.");
            return;
        }

        try {
            int id = Integer.parseInt(idInput); // Ensure the input is an integer

            // Perform the delete operation
            if (deleteRowById(tableName, id)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Deletion Successful", "Record with ID " + id + " has been deleted.");

                // Reload the table data after successful deletion
                if (reloadTableMethod != null) {
                    reloadTableMethod.run();
                }

                closeWindow(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Record Not Found", "No record found with ID " + id + " in table " + tableName + ".");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid ID Format", "Please enter a valid numeric ID.");
        }
    }

    private boolean deleteRowById(String tableName, int id) {
        String query = "DELETE FROM " + tableName + " WHERE " + tableName + "_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Deletion Failed", "An error occurred while deleting the record: " + e.getMessage());
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void CloseClicked(MouseEvent event) {
        closeWindow(event);
    }
}
