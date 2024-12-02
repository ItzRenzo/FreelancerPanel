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
