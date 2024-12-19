package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditQuoteController {

    @FXML
    private TextField TitleTF;

    @FXML
    private ComboBox<Client> ClientComboBox;

    @FXML
    private TextField ProposedPriceTF;

    @FXML
    private DatePicker StartDate;

    @FXML
    private DatePicker Deadline;

    @FXML
    private ComboBox<String> PaymentTermsComboBox;

    @FXML
    private ComboBox<String> StatusComboBox;

    private int quoteId; // The ID of the quote being edited
    private int userId; // The ID of the logged-in user
    private AllQuotesController allquotesController; // Reference to the parent controller
    private PendingQuotesController pendingquotesController; // Reference to the parent controller
    private AcceptedQuotesController acceptedquotesController; // Reference to the parent controller

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
        loadQuoteData(); // Load data for the quote being edited
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadClientsIntoComboBox(); // Populate clients based on user ID
    }

    public void setAllQuotesController(AllQuotesController allquotesController) {
        this.allquotesController = allquotesController;
    }

    public void setPendingQuotesController(PendingQuotesController pendingquotesController) {
        this.pendingquotesController = pendingquotesController;
    }

    public void setAcceptedQuotesController(AcceptedQuotesController acceptedquotesController) {
        this.acceptedquotesController = acceptedquotesController;
    }

    @FXML
    private void initialize() {
        initializeStatusComboBox();
        initializePaymentTermsComboBox();
    }

    private void initializeStatusComboBox() {
        StatusComboBox.getItems().addAll("Rejected", "Pending", "Accepted", "Cancelled");
    }

    private void initializePaymentTermsComboBox() {
        PaymentTermsComboBox.getItems().addAll("Full Payment before", "Full payment after", "50% before, 50% after", "25% before, 75% after");
    }

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
                        null, null, 0, 0.0, null
                );
                ClientComboBox.getItems().add(client);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Load Clients",
                    "An error occurred while loading clients: " + e.getMessage());
        }
    }

    private void loadQuoteData() {
        String query = "SELECT quote_title, client_id, quote_price, quote_start_date, quote_deadline, quote_payment_terms, quote_status " +
                "FROM quote WHERE quote_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, quoteId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                TitleTF.setText(resultSet.getString("quote_title"));

                int clientId = resultSet.getInt("client_id");
                Client client = getClientById(clientId);
                if (client != null) {
                    ClientComboBox.setValue(client);
                }

                ProposedPriceTF.setText(resultSet.getBigDecimal("quote_price").toString());
                StartDate.setValue(resultSet.getDate("quote_start_date").toLocalDate());
                Deadline.setValue(resultSet.getDate("quote_deadline").toLocalDate());
                PaymentTermsComboBox.setValue(resultSet.getString("quote_payment_terms"));
                StatusComboBox.setValue(resultSet.getString("quote_status"));
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Quote Not Found", "No quote found with the provided ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Load Quote",
                    "An error occurred while loading the quote: " + e.getMessage());
        }
    }

    private Client getClientById(int clientId) {
        String query = "SELECT client_id, client_name FROM client WHERE client_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Client(
                        resultSet.getInt("client_id"),
                        resultSet.getString("client_name"),
                        null, null, 0, 0.0, null
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void EditClicked(MouseEvent event) {
    String title = TitleTF.getText();
    Client selectedClient = ClientComboBox.getValue();
    String proposedPriceText = ProposedPriceTF.getText();
    LocalDate startDate = StartDate.getValue();
    LocalDate deadline = Deadline.getValue();
    String paymentTerms = PaymentTermsComboBox.getValue();
    String status = StatusComboBox.getValue();

    if (title == null || title.isEmpty() ||
            selectedClient == null ||
            proposedPriceText == null || proposedPriceText.isEmpty() ||
            startDate == null || deadline == null ||
            paymentTerms == null || status == null) {

        showAlert(Alert.AlertType.WARNING, "Missing Information", "All fields are required.",
                "Please fill in all fields before proceeding.");
        return;
    }

    BigDecimal proposedPrice;
    try {
        proposedPrice = new BigDecimal(proposedPriceText);
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid Proposed Price",
                "Please enter a valid number for the proposed price.");
        return;
    }

    String updateQuery = "UPDATE quote SET quote_title = ?, client_id = ?, quote_price = ?, quote_start_date = ?, " +
            "quote_deadline = ?, quote_payment_terms = ?, quote_status = ? WHERE quote_id = ?";

    try (Connection connection = DatabaseHandler.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

        preparedStatement.setString(1, title);
        preparedStatement.setInt(2, selectedClient.getId());
        preparedStatement.setBigDecimal(3, proposedPrice);
        preparedStatement.setDate(4, java.sql.Date.valueOf(startDate));
        preparedStatement.setDate(5, java.sql.Date.valueOf(deadline));
        preparedStatement.setString(6, paymentTerms);
        preparedStatement.setString(7, status);
        preparedStatement.setInt(8, quoteId);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected == 1) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Quote Updated",
                    "The quote has been successfully updated.");

            // Update the text file with the new quote details
            editQuoteTextFile(quoteId, title, proposedPrice, startDate.toString(), deadline.toString(), selectedClient.getName(), paymentTerms);

            if (allquotesController != null) {
                allquotesController.loadQuoteData();
            }

            if (pendingquotesController != null) {
                pendingquotesController.loadQuoteData();
            }

            if (acceptedquotesController != null) {
                acceptedquotesController.loadQuoteData();
            }

            closeWindow(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Update Quote",
                    "An error occurred while updating the quote. Please try again.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Update Quote",
                "An error occurred while connecting to the database: " + e.getMessage());
    }
}

private void editQuoteTextFile(int quoteId, String title, BigDecimal price, String startDate, String deadline, String clientName, String paymentTerms) {
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
        showAlert(Alert.AlertType.ERROR, "File Error", "Failed to Edit Quote File",
                "An error occurred while editing the quote file: " + e.getMessage());
    }
}

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void CancelClicked(MouseEvent event) {
        closeWindow(event);
    }

    public void CloseClicked(MouseEvent event) {
        closeWindow(event);
    }

    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
