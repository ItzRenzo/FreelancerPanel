package me.group.freelancerpanel.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateAccountPageController {

    @FXML
    private Button BackButton;

    @FXML
    private TextField UserTF;

    @FXML
    private TextField EmailTF;

    @FXML
    private PasswordField PasswordTF;

    @FXML
    private Button CreateButton;

    @FXML
    private CheckBox TOSCheckBox;

    @FXML
    private CheckBox showpass;

    @FXML
    private Pane MainPane;

    private static CreateAccountPageController instance;

    // Modify your constructor
    public CreateAccountPageController() {
        instance = this;
    }

    // Static method to get the instance
    public static CreateAccountPageController getInstance() {
        return instance;
    }

    // Add a getter for the TOSCheckBox
    public CheckBox getTOSCheckBox() {
        return TOSCheckBox;
    }

    @FXML
    public void ShowPassCheck(ActionEvent event) {
        if (showpass.isSelected()) {
            // Create a new TextField to show the password
            TextField plainTextField = new TextField(PasswordTF.getText());

            // Copy exact properties from PasswordField
            plainTextField.setLayoutX(PasswordTF.getLayoutX());
            plainTextField.setLayoutY(PasswordTF.getLayoutY());
            plainTextField.setPrefWidth(PasswordTF.getPrefWidth());
            plainTextField.setPrefHeight(PasswordTF.getPrefHeight());

            // Replace PasswordField with TextField at the exact same index
            int passwordFieldIndex = MainPane.getChildren().indexOf(PasswordTF);
            MainPane.getChildren().remove(PasswordTF);
            MainPane.getChildren().add(passwordFieldIndex, plainTextField);
            plainTextField.requestFocus();
        } else {
            // Find the temporary TextField
            TextField tempTextField = (TextField) MainPane.getChildren().stream()
                    .filter(node -> node instanceof TextField &&
                            node.getLayoutX() == PasswordTF.getLayoutX() &&
                            node.getLayoutY() == PasswordTF.getLayoutY())
                    .findFirst()
                    .orElse(null);

            if (tempTextField != null) {
                // Restore PasswordField
                PasswordField restoredPasswordField = new PasswordField();
                restoredPasswordField.setText(tempTextField.getText());

                // Copy exact properties
                restoredPasswordField.setLayoutX(tempTextField.getLayoutX());
                restoredPasswordField.setLayoutY(tempTextField.getLayoutY());
                restoredPasswordField.setPrefWidth(tempTextField.getPrefWidth());
                restoredPasswordField.setPrefHeight(tempTextField.getPrefHeight());

                // Replace TextField with PasswordField at the same index
                int textFieldIndex = MainPane.getChildren().indexOf(tempTextField);
                MainPane.getChildren().remove(tempTextField);
                MainPane.getChildren().add(textFieldIndex, restoredPasswordField);

                // Restore original reference
                PasswordTF = restoredPasswordField;
                restoredPasswordField.requestFocus();
            }
        }
    }

    public void TOSClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/TOSGUI.fxml"));
            Parent idRoot = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(idRoot));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load ID GUI");
            alert.setContentText("An error occurred while loading the ID GUI: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void BackClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/FrontPage.fxml"));
            Parent AdminRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene AdminScene = new Scene(AdminRoot);
            stage.setScene(AdminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FrontPage.fxml: " + e.getMessage());
        }
    }

    @FXML
    public void CreateButtonClicked(MouseEvent event) {
        String name = UserTF.getText();
        String email = EmailTF.getText();
        String password = PasswordTF.getText(); // Always get the text from PasswordField

        // Check if the TOSCheckBox is checked
        if (!TOSCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(null, "You must agree to the terms of service to register.");
            return;
        }

        // Check if all fields are filled
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required.");
            return;
        }

        // Establish database connection
        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM Freelancer WHERE username = ?")) {

            // Check if the username already exists
            checkStatement.setString(1, name);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "Username already exists.");
                    return;
                }
            }

            // Insert the registration information into the Freelancer table
            String insertQuery = "INSERT INTO Freelancer (username, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, name);
                insertStatement.setString(2, email);
                insertStatement.setString(3, password);

                int rowsAffected = insertStatement.executeUpdate();

                if (rowsAffected == 1) {
                    JOptionPane.showMessageDialog(null, "Registration Successful");

                    // Redirect to Login.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/Login.fxml"));
                    Parent mainRoot = loader.load();
                    Scene mainScene = new Scene(mainRoot);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(mainScene);
                    stage.centerOnScreen();
                    stage.show();
                } else {
                    JOptionPane.showMessageDialog(null, "Registration Failed. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while connecting to the database.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while loading the login screen.");
        }
    }
}
