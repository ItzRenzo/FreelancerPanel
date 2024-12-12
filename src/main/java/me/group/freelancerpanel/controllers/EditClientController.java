package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditClientController {

    @FXML
    private TextField ClientNameTF;

    @FXML
    private TextField ClientEmailTF;

    @FXML
    private TextField ClientDiscordTF;

    @FXML
    private TextField ClientTotalCommissionTF;

    @FXML
    private TextField ClientTotalRevenueTF;

    @FXML
    private TextField ClientSinceTF;

    private int clientId; // Client ID to edit
    private ClientsController clientsController;

    public void setClientID(int clientId) {
        this.clientId = clientId;
        loadClientData(); // Load client details for editing
    }

    public void setClientsController(ClientsController clientsController) {
        this.clientsController = clientsController;
    }

    // Load client data into the form fields
    private void loadClientData() {
        String query = "SELECT client_name, client_email, client_discord, client_total_commission, client_total_revenue, client_since " +
                "FROM client WHERE client_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ClientNameTF.setText(resultSet.getString("client_name"));
                ClientEmailTF.setText(resultSet.getString("client_email"));
                ClientDiscordTF.setText(resultSet.getString("client_discord"));
                ClientTotalCommissionTF.setText(String.valueOf(resultSet.getInt("client_total_commission")));
                ClientTotalRevenueTF.setText(String.valueOf(resultSet.getDouble("client_total_revenue")));
                ClientSinceTF.setText(resultSet.getString("client_since"));
            } else {
                showAlert(Alert.AlertType.WARNING, "Client Not Found", "No client found with the provided ID.");
                closeWindow();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load client data: " + e.getMessage());
            closeWindow();
        }
    }

    // Update client details in the database
    public void EditClicked(MouseEvent event) {
        String name = ClientNameTF.getText();
        String email = ClientEmailTF.getText();
        String discord = ClientDiscordTF.getText();
        String totalCommissionText = ClientTotalCommissionTF.getText();
        String totalRevenueText = ClientTotalRevenueTF.getText();
        String since = ClientSinceTF.getText();

        if (name.isEmpty() || email.isEmpty() || discord.isEmpty() ||
                totalCommissionText.isEmpty() || totalRevenueText.isEmpty() || since.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "All fields are required.");
            return;
        }

        int totalCommission;
        double totalRevenue;

        try {
            totalCommission = Integer.parseInt(totalCommissionText);
            totalRevenue = Double.parseDouble(totalRevenueText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Total Commission and Revenue must be numeric.");
            return;
        }

        String updateQuery = "UPDATE client SET client_name = ?, client_email = ?, client_discord = ?, " +
                "client_total_commission = ?, client_total_revenue = ?, client_since = ? WHERE client_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, discord);
            preparedStatement.setInt(4, totalCommission);
            preparedStatement.setDouble(5, totalRevenue);
            preparedStatement.setString(6, since);
            preparedStatement.setInt(7, clientId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Client details updated successfully.");
                if (clientsController != null) {
                    clientsController.loadClientData();
                }
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update client details. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the client: " + e.getMessage());
        }
    }

    public void CancelClicked(MouseEvent event) {
        closeWindow();
    }

    public void CloseClicked(MouseEvent event) {
        closeWindow();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) ClientNameTF.getScene().getWindow();
        stage.close();
    }
}
