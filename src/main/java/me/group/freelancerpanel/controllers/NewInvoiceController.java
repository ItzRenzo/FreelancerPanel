package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class NewInvoiceController {

    @FXML
    private ComboBox PaymentMethodComboBox;

    @FXML
    private ComboBox ClientComboBox;

    @FXML
    private TextField TotalValueTF;

    @FXML
    private DatePicker Deadline;

    @FXML
    private ComboBox StatusComboBox;

    @FXML
    private TextArea TitleTA;

    @FXML
    private TextArea MemoTA;

    public void CreateClicked(MouseEvent event) {
        Object paymentmethodSelection = PaymentMethodComboBox.getValue();
        Object clientSelection = ClientComboBox.getValue();
        String totalvalueText = TotalValueTF.getText();
        Object deadlineDate = Deadline.getValue();
        Object statusSelection = StatusComboBox.getValue();
        String titleText = TitleTA.getText();
        String memoText = MemoTA.getText();

        if (titleText == null || titleText.isEmpty() ||
                paymentmethodSelection == null ||
                clientSelection == null ||
                totalvalueText == null || totalvalueText.isEmpty() ||
                memoText == null || memoText.isEmpty() ||
                deadlineDate == null ||
                statusSelection == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("All fields are required");
            alert.setContentText("Please fill in all fields before proceeding.");
            alert.showAndWait();
            return;
        }

        System.out.println("All fields are filled. Proceeding with creating the invoice...");
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
