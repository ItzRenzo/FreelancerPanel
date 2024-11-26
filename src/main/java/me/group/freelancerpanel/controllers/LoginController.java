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

public class LoginController {
    @FXML
    private TextField UserTF;
    @FXML
    private PasswordField PasswordTF;
    @FXML
    private CheckBox showpass;
    @FXML
    private Button LoginButton;

    Connection connection;
    PreparedStatement statement;
    ResultSet resultSet;

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



    @FXML
    public void LoginClicked(MouseEvent event) {
        String userText = UserTF.getText();
        String pwdText = PasswordTF.getText();

        if (userText.isEmpty() || (pwdText.isEmpty() && !showpass.isSelected())) {
            JOptionPane.showMessageDialog(null, "Username or Password is empty");
        } else {
            try (Connection connection = me.group.freelancerpanel.controllers.DatabaseHandler.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
                statement.setString(1, userText);

                if (showpass.isSelected()) {
                    statement.setString(2, PasswordTF.getPromptText());
                } else {
                    statement.setString(2, pwdText);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(null, "Login Successful");

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/Dashboard.fxml"));
                        Parent AdminRoot = loader.load();
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Scene AdminScene = new Scene(AdminRoot);
                        stage.setScene(AdminScene);
                        stage.show();
                    } else {
                        JOptionPane.showMessageDialog(null, "Login Failed. Please try again!");
                        UserTF.setText("");
                        PasswordTF.setText("");
                        UserTF.requestFocus();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
