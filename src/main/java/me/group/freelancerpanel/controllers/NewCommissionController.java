package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private TextField TitleTF;

    @FXML
    private ComboBox ProductComboBox;

    @FXML
    private ComboBox ClientComboBox;

    @FXML
    private TextField TotalValueTF;

    @FXML
    private TextField TotalPaidTF;

    @FXML
    private DatePicker StartDate;

    @FXML
    private DatePicker Deadline;

    @FXML
    private ComboBox StatusComboBox;

    @FXML
    private TextArea NotesTA;

    public void CreateClicked(MouseEvent event) {
        String titleText = TitleTF.getText();
        Object productSelection = ProductComboBox.getValue();
        Object clientSelection = ClientComboBox.getValue();
        String totalvalueText = TotalValueTF.getText();
        String totalpaidText = TotalPaidTF.getText();
        Object startdateDate = StartDate.getValue();
        Object deadlineDate = Deadline.getValue();
        Object statusSelection = StatusComboBox.getValue();
        String notesText = NotesTA.getText();

        if (titleText == null || titleText.isEmpty() ||
                productSelection == null ||
                clientSelection == null ||
                totalvalueText == null || totalvalueText.isEmpty() ||
                totalpaidText == null || totalpaidText.isEmpty() ||
                notesText == null || notesText.isEmpty() ||
                startdateDate == null ||
                deadlineDate == null ||
                statusSelection == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("All fields are required");
            alert.setContentText("Please fill in all fields before proceeding.");
            alert.showAndWait();
            return;
        }

        System.out.println("All fields are filled. Proceeding with creating the commission...");
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
