package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import me.group.freelancerpanel.controllers.CreateAccountPageController;

import java.net.URL;
import java.util.ResourceBundle;

public class TOSGUIController implements Initializable {
    @FXML
    private TextArea TOSTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set the Terms of Service text
        TOSTextArea.setText("""
        FREELANCING PANEL - TERMS OF SERVICE

        1. ACCEPTANCE OF TERMS
        By creating an account and using the Freelancing Panel application, you agree to be bound by these Terms of Service. If you do not agree with these terms, do not use our service.

        2. USER ACCOUNTS
        2.1 You must provide accurate, current, and complete information during the registration process.
        2.2 You are responsible for maintaining the confidentiality of your account and password.
        2.3 You agree to accept responsibility for all activities that occur under your account.

        3. COMMISSION MANAGEMENT
        3.1 The Freelancing Panel is a tool to help manage freelance commissions and projects.
        3.2 The application provides tracking and organizational features but does not guarantee payment or project completion.
        3.3 Users are solely responsible for their client interactions and agreements.

        4. DATA AND PRIVACY
        4.1 We collect and store user information to provide and improve our service.
        4.2 Personal and project data will be kept confidential and secured.
        4.3 Users can request data deletion by contacting our support team.

        5. INTELLECTUAL PROPERTY
        5.1 The Freelancing Panel application and its original content are owned by the application developers.
        5.2 Users retain ownership of their personal data and project information.

        6. LIMITATION OF LIABILITY
        6.1 The Freelancing Panel is provided "as is" without any warranties.
        6.2 We are not liable for any direct, indirect, incidental, or consequential damages arising from the use of our application.

        7. PROHIBITED ACTIVITIES
        7.1 Users may not use the application for illegal or unethical purposes.
        7.2 Harassment, fraud, or misuse of the platform is strictly prohibited.

        8. MODIFICATIONS TO SERVICE
        8.1 We reserve the right to modify or discontinue the service at any time.
        8.2 Changes to these terms will be communicated to users via the application or email.

        9. TERMINATION
        9.1 We may terminate or suspend your account at our discretion.
        9.2 Users can request account deletion at any time.

        10. GOVERNING LAW
        10.1 These terms are governed by the laws of [Your Jurisdiction].
        10.2 Any disputes will be resolved through appropriate legal channels.

        By checking "I Agree" and creating an account, you acknowledge that you have read, understood, and agree to these Terms of Service.

        Last Updated: December 2024
        """);

        // Make the TextArea non-editable
        TOSTextArea.setEditable(false);
    }

    public void AcceptClicked(MouseEvent event) {
        // Get reference to the CreateAccountPageController
        CreateAccountPageController createAccountController = CreateAccountPageController.getInstance();
        if (createAccountController != null) {
            // Programmatically check the TOSCheckBox
            createAccountController.getTOSCheckBox().setSelected(true);

            // Close the TOS window
            closeWindow(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Controller Error", "Unable to access Create Account Controller");
        }
    }

    // Close the current window
    public void CloseClicked(MouseEvent event) {
        closeWindow(event);
    }

    // Helper to close the window
    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Helper to show an alert
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}