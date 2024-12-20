package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NewClientController {

    @FXML
    private TextField NameTF;

    @FXML
    private TextField DiscordTF;

    @FXML
    private TextField EmailTF;

    private int userId; // Store the current logged-in user's ID

    private ClientsController clientsController;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setClientsController(ClientsController clientsController) {
        this.clientsController = clientsController;
    }

    public void CreateClicked(MouseEvent event) {
        String nameText = NameTF.getText();
        String discordText = DiscordTF.getText();
        String emailText = EmailTF.getText();

        if (nameText.isEmpty() || discordText.isEmpty() || emailText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("All fields are required");
            alert.setContentText("Please fill in all fields before proceeding.");
            alert.showAndWait();
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String clientSince = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String insertQuery = "INSERT INTO client (user_id, client_name, client_discord, client_email, client_since) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, nameText);
            preparedStatement.setString(3, discordText);
            preparedStatement.setString(4, emailText);
            preparedStatement.setString(5, clientSince);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Client Created");
                alert.setContentText("The new client has been successfully added.");
                alert.showAndWait();

                if (clientsController != null) {
                    clientsController.loadClientData();
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to Create Client");
                alert.setContentText("An error occurred while adding the client. Please try again.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Create Client");
            alert.setContentText("An error occurred while connecting to the database: " + e.getMessage());
            alert.showAndWait();
        }
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
