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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    private AllQuotesController allquotesController;
    private PendingQuotesController pendingquotesController;
    private AcceptedQuotesController acceptedquotesController;

    public void setAllQuotesController(AllQuotesController allquotesController) {
        this.allquotesController = allquotesController;
    }

    public void setPendingQuotesController(PendingQuotesController pendingquotesController) {
        this.pendingquotesController = pendingquotesController;
    }

    public void setAcceptedQuotesController(AcceptedQuotesController acceptedquotesController) {
        this.acceptedquotesController = acceptedquotesController;
    }

    private int userId; // Store the current logged-in user's ID

    public void setUserId(int userId) {
        this.userId = userId;
        loadClientsIntoComboBox(); // Load clients after setting user ID
    }

    @FXML
    private void initialize() {
        initializeStatusComboBox(); // Load status options on controller load
        initializePaymentTermsComboBox(); // Load status options on controller load
    }

    // Populate status ComboBox with predefined values
    private void initializePaymentTermsComboBox() {
        PaymentTermsComboBox.getItems().addAll("Full Payment before", "Full payment after", "50% before, 50% after", "25% before, 75% after");
    }

    // Populate status ComboBox with predefined values
    private void initializeStatusComboBox() {
        StatusComboBox.getItems().addAll("Rejected", "Pending", "Accepted", "Cancelled");
    }

    // Populate the ClientComboBox
    private void loadClientsIntoComboBox() {
        String query = "SELECT client_id, client_name FROM client WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Client client = new Client(
                        resultSet.getInt("client_id"),
                        resultSet.getString("client_name"),
                        null, // Email
                        null, // Discord
                        0,    // Total Commission
                        0.0,  // Total Revenue
                        null  // Since
                );
                ClientComboBox.getItems().add(client);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Load Clients");
            alert.setContentText("An error occurred while loading clients: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void CreateClicked(MouseEvent event) {
    String titleText = TitleTF.getText();
    Client selectedClient = (Client) ClientComboBox.getValue(); // Cast to Client
    String proposedPriceText = ProposedPriceTF.getText();
    Object startDate = StartDate.getValue();
    Object deadlineDate = Deadline.getValue();
    Object paymentTermsSelection = PaymentTermsComboBox.getValue();
    Object statusSelection = StatusComboBox.getValue();

    // Validate input fields
    if (titleText == null || titleText.isEmpty() ||
            selectedClient == null ||
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

    BigDecimal proposedPrice;
    try {
        proposedPrice = new BigDecimal(proposedPriceText); // Convert to BigDecimal
    } catch (NumberFormatException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid Proposed Price");
        alert.setContentText("Please enter a valid number for the proposed price.");
        alert.showAndWait();
        return;
    }

    // SQL query to insert a new quote and retrieve the generated quote_id
    String insertQuery = "INSERT INTO quote (user_id, quote_title, client_id, quote_price, quote_start_date, " +
            "quote_deadline, quote_payment_terms, quote_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    String selectQuery = "SELECT LAST_INSERT_ID() AS quote_id";

    try (Connection connection = DatabaseHandler.getConnection();
         PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
         PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

        // Set query parameters
        insertStatement.setInt(1, userId); // User ID
        insertStatement.setString(2, titleText); // Quote Title
        insertStatement.setInt(3, selectedClient.getId()); // Client ID
        insertStatement.setBigDecimal(4, proposedPrice); // Quote Price
        insertStatement.setDate(5, java.sql.Date.valueOf(startDate.toString())); // Start Date
        insertStatement.setDate(6, java.sql.Date.valueOf(deadlineDate.toString())); // Deadline
        insertStatement.setString(7, paymentTermsSelection.toString()); // Payment Terms
        insertStatement.setString(8, statusSelection.toString()); // Status

        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected == 1) {
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                int quoteId = resultSet.getInt("quote_id");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Quote Created");
                alert.setContentText("The new quote has been successfully added.");
                alert.showAndWait();

                // Generate the text file
                generateQuoteTextFile(quoteId, titleText, proposedPrice, startDate.toString(), deadlineDate.toString(), selectedClient.getName(), paymentTermsSelection.toString());

                if (allquotesController != null) {
                    allquotesController.loadQuoteData();
                }

                if (pendingquotesController != null) {
                    pendingquotesController.loadQuoteData();
                }

                if (acceptedquotesController != null) {
                    acceptedquotesController.loadQuoteData();
                }

                // Close the window after success
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Create Quote");
            alert.setContentText("An error occurred while adding the quote. Please try again.");
            alert.showAndWait();
        }

    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText("Failed to Create Quote");
        alert.setContentText("An error occurred while connecting to the database: " + e.getMessage());
        alert.showAndWait();
    }
}

private void generateQuoteTextFile(int quoteId, String title, BigDecimal price, String startDate, String deadline, String clientName, String paymentTerms) {
    String termsAndAgreement = """
        TERMS AND AGREEMENT FOR THE COMMISSION

        1. The client agrees to the proposed price and payment terms.
        2. The client agrees to the start date and deadline.
        3. The client agrees to provide necessary information and resources for the project.
        4. The client agrees to the terms of service as outlined in the application.
        """;

    String content = String.format("""
        Quote Title: %s
        Quote Price: %s
        Start Date: %s
        Deadline: %s
        Client Name: %s
        Payment Terms: %s

        %s
        """, title, price, startDate, deadline, clientName, paymentTerms, termsAndAgreement);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(quoteId + "_quote.txt"))) {
        writer.write(content);
    } catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("File Error");
        alert.setHeaderText("Failed to Generate Quote File");
        alert.setContentText("An error occurred while generating the quote file: " + e.getMessage());
        alert.showAndWait();
    }
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
