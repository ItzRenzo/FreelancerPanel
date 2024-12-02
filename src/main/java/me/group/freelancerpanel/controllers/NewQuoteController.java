package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.*;

public class NewQuoteController {

    @FXML
    private TextField TitleTF;

    @FXML
    private ComboBox ClientComboBox;

    @FXML
    private TextField ProposedPriceTF;

    @FXML
    private DatePicker StartDate;

    @FXML
    private DatePicker Deadline;

    @FXML
    private ComboBox PaymentTermsComboBox;

    @FXML
    private ComboBox StatusComboBox;

    public void CreateClicked(MouseEvent event) {
        String titleText = TitleTF.getText();
        Object clientSelection = ClientComboBox.getValue();
        String proposedPriceText = ProposedPriceTF.getText();
        Object startDate = StartDate.getValue();
        Object deadlineDate = Deadline.getValue();
        Object paymentTermsSelection = PaymentTermsComboBox.getValue();
        Object statusSelection = StatusComboBox.getValue();

        if (titleText == null || titleText.isEmpty() ||
                clientSelection == null ||
                proposedPriceText == null || proposedPriceText.isEmpty() ||
                startDate == null ||
                deadlineDate == null ||
                paymentTermsSelection == null ||
                statusSelection == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("All fields are required");
            alert.setContentText("Please fill in all fields before proceeding.");
            alert.showAndWait();
            return;
        }

        System.out.println("All fields are filled. Proceeding with creating the quote...");
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
