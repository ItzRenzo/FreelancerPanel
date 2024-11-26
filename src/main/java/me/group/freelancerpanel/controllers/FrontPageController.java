package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class FrontPageController {

    @FXML
    private Button LoginButton;

    @FXML
    private Button CreateAccountButton;

    public void LoginClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/Login.fxml"));
            Parent AdminRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene AdminScene = new Scene(AdminRoot);
            stage.setScene(AdminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Login.fxml: " + e.getMessage());
        }
    }
    public void CreateAccountClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/CreateAccountPage.fxml"));
            Parent AdminRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene AdminScene = new Scene(AdminRoot);
            stage.setScene(AdminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading CreateAccountPage.fxml: " + e.getMessage());
        }
    }
}
