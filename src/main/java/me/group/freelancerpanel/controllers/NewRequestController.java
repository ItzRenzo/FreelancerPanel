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
import java.sql.SQLException;

public class NewRequestController {

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

    private AllRequestController allrequestController;

    public void setAllRequestController(AllRequestController allrequestController) {
        this.allrequestController = allrequestController;
    }

    private int userId; // Store the current logged-in user's ID

    public void setUserId(int userId) {
        this.userId = userId;
        loadCommissionsIntoComboBox(); // Load commissions after setting user ID
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
            var resultSet = preparedStatement.executeQuery();

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

    public void CreateClicked(MouseEvent event) {
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

        // SQL query to insert a new request
        String insertQuery = "INSERT INTO request (user_id, request_description, commission_id, " +
                "request_offered_amount, request_deadline, request_status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            // Set query parameters
            preparedStatement.setInt(1, userId); // User ID
            preparedStatement.setString(2, descText); // Request Description
            preparedStatement.setInt(3, selectedCommission.commissionIDProperty().get()); // Commission ID
            preparedStatement.setBigDecimal(4, offeredAmount); // Offered Amount
            preparedStatement.setDate(5, java.sql.Date.valueOf(deadlineDate)); // Deadline
            preparedStatement.setString(6, statusSelection); // Status

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Request Created",
                        "The new request has been successfully added.");

                if (allrequestController != null) {
                    allrequestController.loadRequestData();
                }

                // Close the window after success
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Create Request",
                        "An error occurred while adding the request. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Create Request",
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
