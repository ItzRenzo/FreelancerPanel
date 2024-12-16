package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditInvoiceController {

    @FXML
    private TextField TotalValueTF;

    @FXML
    private DatePicker Deadline;

    @FXML
    private ComboBox<String> StatusComboBox;

    @FXML
    private ComboBox<Client> ClientComboBox;

    @FXML
    private ComboBox<String> PaymentMethodComboBox;

    @FXML
    private TextArea TitleTA;

    @FXML
    private TextArea MemoTA;

    private int invoiceId; // ID of the invoice being edited
    private InvoiceController invoiceController; // Reference to the parent controller

    private int userId;

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
        loadInvoiceData(); // Load invoice details
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadClientsIntoComboBox();
        loadPaymentMethods();
    }

    public void setInvoiceController(InvoiceController invoiceController) {
        this.invoiceController = invoiceController;
    }

    @FXML
    private void initialize() {
        initializeStatusComboBox();
    }

    private void initializeStatusComboBox() {
        StatusComboBox.getItems().addAll("Pending", "Paid", "Cancelled", "Overdue");
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
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load clients: " + e.getMessage());
        }
    }

    private void loadPaymentMethods() {
        // Example predefined payment methods
        PaymentMethodComboBox.getItems().addAll("Bank Transfer", "PayPal", "Credit Card", "Cash");
    }

    private void loadInvoiceData() {
        String query = "SELECT client_id, invoice_amount, invoice_due_at, invoice_status, invoice_title, invoice_memo, invoice_payment_link " +
                "FROM invoice WHERE invoice_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, invoiceId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Populate the fields with the existing data
                int clientId = resultSet.getInt("client_id");
                Client client = getClientById(clientId);
                if (client != null) {
                    ClientComboBox.setValue(client);
                }

                TotalValueTF.setText(String.valueOf(resultSet.getDouble("invoice_amount")));
                Deadline.setValue(resultSet.getDate("invoice_due_at").toLocalDate());
                StatusComboBox.setValue(resultSet.getString("invoice_status"));
                TitleTA.setText(resultSet.getString("invoice_title"));
                MemoTA.setText(resultSet.getString("invoice_memo"));
                PaymentMethodComboBox.setValue(resultSet.getString("invoice_payment_link"));
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invoice not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load invoice: " + e.getMessage());
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
        Client selectedClient = ClientComboBox.getValue();
        String totalValue = TotalValueTF.getText();
        LocalDate deadline = Deadline.getValue();
        String status = StatusComboBox.getValue();
        String title = TitleTA.getText();
        String memo = MemoTA.getText();
        String paymentMethod = PaymentMethodComboBox.getValue();

        if (selectedClient == null || totalValue.isEmpty() || deadline == null || status == null || title.isEmpty() || paymentMethod == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "All fields are required.");
            return;
        }

        String updateQuery = "UPDATE invoice " +
                "SET client_id = ?, invoice_amount = ?, invoice_due_at = ?, invoice_status = ?, " +
                "invoice_title = ?, invoice_memo = ?, invoice_open_account = ?, " +
                "invoice_paid_at = CASE WHEN ? = 'Paid' THEN CURRENT_DATE ELSE invoice_paid_at END " +
                "WHERE invoice_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setInt(1, selectedClient.getId()); // Client ID
            preparedStatement.setDouble(2, Double.parseDouble(totalValue)); // Invoice amount
            preparedStatement.setDate(3, java.sql.Date.valueOf(deadline)); // Invoice due date
            preparedStatement.setString(4, status); // Invoice status
            preparedStatement.setString(5, title); // Invoice title
            preparedStatement.setString(6, memo); // Invoice memo
            preparedStatement.setString(7, paymentMethod); // Payment method
            preparedStatement.setString(8, status); // Condition for updating invoice_paid_at
            preparedStatement.setInt(9, invoiceId); // Invoice ID

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice updated successfully.");
                if (invoiceController != null) {
                    invoiceController.loadInvoiceData(); // Refresh the invoice table
                }
                closeWindow(event); // Close the current window
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update invoice.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update invoice: " + e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
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
