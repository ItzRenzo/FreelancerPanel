package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditRequestController {

    @FXML
    private TextField DescTF;

    @FXML
    private TextField OfferedAmountTF;

    @FXML
    private ComboBox<Commission> CommissionComboBox;

    @FXML
    private DatePicker Deadline;

    @FXML
    private ComboBox<String> StatusComboBox;

    private AllRequestController allrequestController; // Reference to parent controller
    private int userId; // Store the current logged-in user's ID
    private int requestId; // Store the ID of the request being edited

    public void setAllRequestController(AllRequestController allrequestController) {
        this.allrequestController = allrequestController;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadCommissionsIntoComboBox(); // Load commissions when userId is set
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
        loadRequestData(); // Load request details when requestId is set
    }

    @FXML
    private void initialize() {
        initializeStatusComboBox(); // Load status options on controller load

        // Set styles for DatePicker and ComboBoxes
        Deadline.setStyle("-fx-text-fill: white; -fx-background-color: #282828;");
        StatusComboBox.setStyle("-fx-text-fill: white; -fx-background-color: #282828;");
        CommissionComboBox.setStyle("-fx-text-fill: white; -fx-background-color: #282828;");
    }

    // Populate status ComboBox with predefined values
    private void initializeStatusComboBox() {
        StatusComboBox.getItems().addAll("Rejected", "Pending", "Accepted", "Cancelled");
    }

    // Populate the CommissionComboBox
    private void loadCommissionsIntoComboBox() {
        String query = "SELECT commission_id, commission_title FROM commission WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Commission commission = new Commission(
                        resultSet.getInt("commission_id"),
                        resultSet.getString("commission_title"),
                        null, 0.0, 0.0, null, null, null, null
                );
                CommissionComboBox.getItems().add(commission);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Load Commissions",
                    "An error occurred while loading commissions: " + e.getMessage());
        }
    }

    // Load existing request details into fields
    private void loadRequestData() {
        String query = "SELECT request_description, request_offered_amount, commission_id, " +
                "request_deadline, request_status FROM request WHERE request_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, requestId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                DescTF.setText(resultSet.getString("request_description"));
                OfferedAmountTF.setText(String.valueOf(resultSet.getBigDecimal("request_offered_amount")));

                int commissionId = resultSet.getInt("commission_id");
                Commission commission = getCommissionById(commissionId);
                if (commission != null) {
                    CommissionComboBox.setValue(commission);
                }

                Deadline.setValue(resultSet.getDate("request_deadline").toLocalDate());
                StatusComboBox.setValue(resultSet.getString("request_status"));
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Request Not Found", "No request found with the provided ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Load Request",
                    "An error occurred while loading the request: " + e.getMessage());
        }
    }

    // Helper to get a Commission object by ID
    private Commission getCommissionById(int commissionId) {
        String query = "SELECT commission_id, commission_title FROM commission WHERE commission_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, commissionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Commission(
                        resultSet.getInt("commission_id"),
                        resultSet.getString("commission_title"),
                        null, 0.0, 0.0, null, null, null, null
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void EditClicked(MouseEvent event) {
        String descText = DescTF.getText();
        String offeredamountText = OfferedAmountTF.getText();
        Commission selectedCommission = CommissionComboBox.getValue();
        var deadlineDate = Deadline.getValue();
        String statusSelection = (String) StatusComboBox.getValue();

        // Validate input fields
        if (descText == null || descText.isEmpty() ||
                selectedCommission == null ||
                offeredamountText == null || offeredamountText.isEmpty() ||
                deadlineDate == null ||
                statusSelection == null) {

            showAlert(Alert.AlertType.WARNING, "Missing Information", "All fields are required",
                    "Please fill in all fields before proceeding.");
            return;
        }

        BigDecimal offeredAmount;
        try {
            offeredAmount = new BigDecimal(offeredamountText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid Offered Amount",
                    "Please enter a valid number for the offered amount.");
            return;
        }

        // SQL query to update the request
        String updateQuery = "UPDATE request SET request_description = ?, commission_id = ?, " +
                "request_offered_amount = ?, request_deadline = ?, request_status = ? " +
                "WHERE request_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            // Set query parameters
            preparedStatement.setString(1, descText); // Request Description
            preparedStatement.setInt(2, selectedCommission.commissionIDProperty().get()); // Commission ID
            preparedStatement.setBigDecimal(3, offeredAmount); // Offered Amount
            preparedStatement.setDate(4, java.sql.Date.valueOf(deadlineDate)); // Deadline
            preparedStatement.setString(5, statusSelection); // Status
            preparedStatement.setInt(6, requestId); // Request ID

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Request Updated",
                        "The request has been successfully updated.");

                if (allrequestController != null) {
                    allrequestController.loadRequestData();
                }

                // Close the window after success
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Update Request",
                        "An error occurred while updating the request. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Update Request",
                    "An error occurred while connecting to the database: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void CancelClicked(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void CloseClicked(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
