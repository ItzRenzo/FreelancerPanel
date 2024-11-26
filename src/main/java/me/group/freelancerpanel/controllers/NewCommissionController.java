package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewCommissionController {

    @FXML
    private TextField NameTF;

    @FXML
    private TextField DiscordTF;

    @FXML
    private TextField EmailTF;


    public void CreateClicked(MouseEvent event) {
        String nameText = NameTF.getText();
        String discordText = DiscordTF.getText();
        String emailText = EmailTF.getText();

        if (nameText.isEmpty() || (discordText.isEmpty() && !emailText.isEmpty())) {
            JOptionPane.showMessageDialog(null, "Name, Discord Name, and Email is empty");
        } else {
        }
    }

    public void CancelClicked(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.close();
    }


    public void CloseClicked(MouseEvent event) {
        // Get the current Stage using the event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Close the stage
        stage.close();
    }

}
