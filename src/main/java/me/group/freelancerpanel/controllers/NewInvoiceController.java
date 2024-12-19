package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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

    private InvoiceController invoiceController;

    public void setInvoiceController(InvoiceController invoiceController) {
        this.invoiceController = invoiceController;
    }

    private int userId; // Store the current logged-in user's ID

    public void setUserId(int userId) {
        this.userId = userId;
        loadClientsIntoComboBox(); // Load clients after setting user ID
    }

    @FXML
    private void initialize() {
        initializeStatusComboBox(); // Load status options on controller load
        initializePaymentMethodComboBox(); // Load status options on controller load
    }

    // Populate status ComboBox with predefined values
    private void initializePaymentMethodComboBox() {
        PaymentMethodComboBox.getItems().addAll("Paypal", "Stripe");
    }


    // Populate status ComboBox with predefined values
    private void initializeStatusComboBox() {
        StatusComboBox.getItems().addAll("Not paid", "Partially paid", "Cancelled", "Paid");
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
    // Validate inputs
    Object paymentMethodSelection = PaymentMethodComboBox.getValue();
    Client selectedClient = (Client) ClientComboBox.getValue();
    String totalValueText = TotalValueTF.getText();
    Object deadlineDate = Deadline.getValue();
    Object statusSelection = StatusComboBox.getValue();
    String titleText = TitleTA.getText();
    String memoText = MemoTA.getText();

    if (titleText == null || titleText.isEmpty() ||
            paymentMethodSelection == null ||
            selectedClient == null ||
            totalValueText == null || totalValueText.isEmpty() ||
            deadlineDate == null ||
            statusSelection == null) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Missing Information");
        alert.setHeaderText("All fields are required");
        alert.setContentText("Please fill in all fields before proceeding.");
        alert.showAndWait();
        return;
    }

    BigDecimal totalValue;
    try {
        totalValue = new BigDecimal(totalValueText);
    } catch (NumberFormatException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid Total Value");
        alert.setContentText("Please enter a valid number for the total value.");
        alert.showAndWait();
        return;
    }

    // SQL query to insert a new invoice and retrieve the generated invoice_id
    String insertQuery = "INSERT INTO invoice (user_id, client_id, invoice_amount, invoice_status, " +
            "invoice_payment_link, invoice_due_at, invoice_title, invoice_memo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    String selectQuery = "SELECT LAST_INSERT_ID() AS invoice_id";

    try (Connection connection = DatabaseHandler.getConnection();
         PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
         PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

        // Set query parameters
        insertStatement.setInt(1, userId); // User ID
        insertStatement.setInt(2, selectedClient.getId()); // Client ID
        insertStatement.setBigDecimal(3, totalValue); // Invoice Amount
        insertStatement.setString(4, statusSelection.toString()); // Invoice Status
        insertStatement.setString(5, paymentMethodSelection.toString()); // Payment Method
        insertStatement.setDate(6, java.sql.Date.valueOf(deadlineDate.toString())); // Invoice Due Date
        insertStatement.setString(7, titleText); // Invoice Title
        insertStatement.setString(8, memoText); // Invoice Memo

        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected == 1) {
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                int invoiceId = resultSet.getInt("invoice_id");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Invoice Created");
                alert.setContentText("The new invoice has been successfully added.");
                alert.showAndWait();

                // Generate the text file
                generateInvoiceTextFile(invoiceId, userId, selectedClient.getId(), totalValue, paymentMethodSelection.toString(), deadlineDate.toString(), titleText, memoText, statusSelection.toString());

                if (invoiceController != null) {
                    invoiceController.loadInvoiceData();
                }

                // Close the window after success
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Create Invoice");
            alert.setContentText("An error occurred while adding the invoice. Please try again.");
            alert.showAndWait();
        }

    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText("Failed to Create Invoice");
        alert.setContentText("An error occurred while connecting to the database: " + e.getMessage());
        alert.showAndWait();
    }
}

private void generateInvoiceTextFile(int invoiceId, int userId, int clientId, BigDecimal amount, String paymentMethod, String dueDate, String title, String memo, String status) {
    StringBuilder content = new StringBuilder(String.format("""
        User ID: %d
        Client ID: %d
        Invoice Amount: %s
        Payment Method: %s
        Due Date: %s
        Title: %s
        Memo: %s
        """, userId, clientId, amount, paymentMethod, dueDate, title, memo));

    if ("Paid".equals(status)) {
        content.append(String.format("""
            Invoice Status: %s
            Invoice Paid At: %s
            """, status, java.time.LocalDate.now().toString()));
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(invoiceId + "_invoice.txt"))) {
        writer.write(content.toString());
    } catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("File Error");
        alert.setHeaderText("Failed to Generate Invoice File");
        alert.setContentText("An error occurred while generating the invoice file: " + e.getMessage());
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
