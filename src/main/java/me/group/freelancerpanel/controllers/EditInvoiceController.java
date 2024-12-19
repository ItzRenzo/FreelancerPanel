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

    private int invoiceId;
    private InvoiceController invoiceController;

    private int userId;

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
        loadInvoiceData();
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

            // Configure ComboBox to display client names
            ClientComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? null : client.getName());
                }
            });

            ClientComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? null : client.getName());
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load clients: " + e.getMessage());
        }
    }

    private void loadPaymentMethods() {
        PaymentMethodComboBox.getItems().addAll("Bank Transfer", "PayPal", "Credit Card", "Cash");
    }

    private void loadInvoiceData() {
        String query = "SELECT client_id, invoice_amount, invoice_due_at, invoice_status, invoice_title, invoice_memo, invoice_open_account " +
                "FROM invoice WHERE invoice_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, invoiceId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
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
                PaymentMethodComboBox.setValue(resultSet.getString("invoice_open_account"));
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

            preparedStatement.setInt(1, selectedClient.getId());
            preparedStatement.setDouble(2, Double.parseDouble(totalValue));
            preparedStatement.setDate(3, java.sql.Date.valueOf(deadline));
            preparedStatement.setString(4, status);
            preparedStatement.setString(5, title);
            preparedStatement.setString(6, memo);
            preparedStatement.setString(7, paymentMethod);
            preparedStatement.setString(8, status);
            preparedStatement.setInt(9, invoiceId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice updated successfully.");
                if (invoiceController != null) {
                    invoiceController.loadInvoiceData();
                }

                // Update the text file with the new invoice details
                editInvoiceTextFile(invoiceId, userId, selectedClient.getId(), new BigDecimal(totalValue), paymentMethod, deadline.toString(), title, memo, status);

                closeWindow(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update invoice.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update invoice: " + e.getMessage());
        }
    }

private void editInvoiceTextFile(int invoiceId, int userId, int clientId, BigDecimal amount, String paymentMethod, String dueDate, String title, String memo, String status) {
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
        showAlert(Alert.AlertType.ERROR, "File Error", "Failed to Edit Invoice File: " + e.getMessage());
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