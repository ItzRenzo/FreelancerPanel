package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewRequestController {

    @FXML
    private TextField DescTF;

    @FXML
    private TextField OfferedAmountTF;

    @FXML
    private ComboBox CommissionComboBox;

    @FXML
    private DatePicker Deadline;

    @FXML
    private ComboBox StatusComboBox;

    private AllRequestController allrequestController;

    public void setAllRequestController(AllRequestController allrequestController) {
        this.allrequestController = allrequestController;
    }

    private int userId; // Store the current logged-in user's ID

    public void setUserId(int userId) {
        this.userId = userId;
        loadCommissionsIntoComboBox(); // Load clients after setting user ID
    }

    @FXML
    private void initialize() {
        initializeStatusComboBox(); // Load status options on controller load
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
                // Create a Commission object with the minimal constructor
                Commission commission = new Commission(
                        resultSet.getInt("commission_id"),
                        resultSet.getString("commission_title"),
                        null, // client_name (not retrieved in this query)
                        0.0,  // total_value (default value)
                        0.0,  // total_paid (default value)
                        null, // start_date (not retrieved in this query)
                        null, // deadline (not retrieved in this query)
                        null, // product_name (not retrieved in this query)
                        null  // status (not retrieved in this query)
                );
                CommissionComboBox.getItems().add(commission);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Load Commissions");
            alert.setContentText("An error occurred while loading commissions: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void CreateClicked(MouseEvent event) {
        String descText = DescTF.getText();
        String offeredamountText = OfferedAmountTF.getText();
        Object commissionSelection = CommissionComboBox.getValue();
        Object deadlineDate = Deadline.getValue();
        Object statusSelection = StatusComboBox.getValue();

        if (descText == null || descText.isEmpty() ||
                commissionSelection == null ||
                offeredamountText == null || offeredamountText.isEmpty() ||
                deadlineDate == null ||
                statusSelection == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("All fields are required");
            alert.setContentText("Please fill in all fields before proceeding.");
            alert.showAndWait();
            return;
        }

        System.out.println("All fields are filled. Proceeding with creating the request...");
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
