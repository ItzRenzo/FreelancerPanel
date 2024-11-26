package me.group.freelancerpanel.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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
    private CheckBox TOXCheckBox;

    @FXML
    private CheckBox showpass;

    @FXML
    public void ShowPassCheck(ActionEvent event) {
        if (showpass.isSelected()) {
            PasswordTF.setPromptText(PasswordTF.getText());
            PasswordTF.clear();
        } else {
            PasswordTF.setText(PasswordTF.getPromptText());
            PasswordTF.setPromptText("");
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
        String password = new String(PasswordTF.getText());

        // Establish database connection
        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "Username already exists");
                } else {
                    // Insert the registration information into the database
                    String insertQuery = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setString(1, name);
                    insertStatement.setString(2, email);
                    insertStatement.setString(3, password);

                    int rowsAffected = insertStatement.executeUpdate();

                    if (rowsAffected == 1) {
                        JOptionPane.showMessageDialog(null, "Registration Successful");

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/Login.fxml"));
                        Parent mainRoot = loader.load();

                        Scene mainScene = new Scene(mainRoot);
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(mainScene);
                        stage.show();
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration Failed");
                    }

                    insertStatement.close();
                }

                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
