package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.*;

public class EditCommissionController {

    @FXML
    private TextField TitleTF;

    @FXML
    private ComboBox<String> ProductComboBox;

    @FXML
    private ComboBox<Client> ClientComboBox;

    @FXML
    private TextField TotalValueTF;

    @FXML
    private TextField TotalPaidTF;

    @FXML
    private DatePicker StartDate;

    @FXML
    private DatePicker Deadline;

    @FXML
    private ComboBox<String> StatusComboBox;

    @FXML
    private TextArea NotesTA;

    private int commissionID;
    private AllCommissionsController allcommissionController; // Reference to parent controller

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        loadClientsIntoComboBox(); // Load clients after setting user ID
        loadProductsIntoComboBox(); // Load products after setting user ID
    }

    public void setCommissionID(int commissionID) {
        this.commissionID = commissionID;
        loadCommissionData(); // Load data when ID is set
    }

    public void setCommissionController(AllCommissionsController allcommissionController) {
        this.allcommissionController = allcommissionController;
    }

    // Helper method to load client details by client_id
    private Client loadClientById(int clientId) {
        String query = "SELECT client_id, client_name, client_email, client_discord, client_total_commission, client_total_revenue, client_since FROM client WHERE client_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Client(
                        resultSet.getInt("client_id"),
                        resultSet.getString("client_name"),
                        resultSet.getString("client_email"),
                        resultSet.getString("client_discord"),
                        resultSet.getInt("client_total_commission"),
                        resultSet.getDouble("client_total_revenue"),
                        resultSet.getString("client_since")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @FXML
    private void initialize() {
        initializeStatusComboBox(); // Load status options on controller load
    }

    // Populate status ComboBox with predefined values
    private void initializeStatusComboBox() {
        StatusComboBox.getItems().addAll("Not Started", "In Progress", "Completed", "Cancelled", "Paused");
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

    // Populate the ProductComboBox
    private void loadProductsIntoComboBox() {
        String query = "SELECT product_id, product_name FROM product WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String productName = resultSet.getString("product_name");
                Product product = new Product(productId, productName, null);  // Assuming product description is optional

                ProductComboBox.getItems().add(String.valueOf(product));  // Add Product object to ComboBox
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Load Products");
            alert.setContentText("An error occurred while loading products: " + e.getMessage());
            alert.showAndWait();
        }
    }


    private Product getProductById(int productId) {
        String query = "SELECT product_id, product_name FROM product WHERE product_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Product(
                        resultSet.getInt("product_id"),
                        resultSet.getString("product_name"),
                        null  // Assuming description is optional
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getProductId(String productName) {
        String query = "SELECT product_id FROM product WHERE product_name = ? AND user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, productName);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("product_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Get Product ID");
            alert.setContentText("An error occurred while fetching the product ID: " + e.getMessage());
            alert.showAndWait();
        }

        return -1; // Return an invalid id if not found
    }



    private void loadCommissionData() {
        String query = "SELECT commission_title, client_id, product_id, commission_total_value, commission_total_paid, commission_start_date, commission_deadline, commission_status " +
                "FROM commission " +
                "WHERE commission_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, commissionID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                TitleTF.setText(resultSet.getString("commission_title"));

                // Load the client into the ComboBox using the client ID from the database
                int clientId = resultSet.getInt("client_id");
                Client client = loadClientById(clientId);
                ClientComboBox.setValue(client);  // Set the client in the ComboBox

                // Load the product into the ComboBox using the product_id
                int productId = resultSet.getInt("product_id");
                Product selectedProduct = getProductById(productId);  // This method should fetch the Product object based on product_id
                ProductComboBox.setValue(String.valueOf(selectedProduct));  // Set the selected Product in the ComboBox

                TotalValueTF.setText(String.valueOf(resultSet.getDouble("commission_total_value")));
                TotalPaidTF.setText(String.valueOf(resultSet.getDouble("commission_total_paid")));
                StartDate.setValue(java.time.LocalDate.parse(resultSet.getString("commission_start_date")));
                Deadline.setValue(java.time.LocalDate.parse(resultSet.getString("commission_deadline")));
                StatusComboBox.setValue(resultSet.getString("commission_status"));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Commission Not Found");
                alert.setContentText("No commission found with the provided ID.");
                alert.showAndWait();

                // Close the window if data is not found
                Stage stage = (Stage) TitleTF.getScene().getWindow();
                stage.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Load Commission");
            alert.setContentText("An error occurred while loading commission data: " + e.getMessage());
            alert.showAndWait();
        }
    }



    // Helper method to load the product name by product_id
    private String getProductName(int productId) {
        String query = "SELECT product_name FROM product WHERE product_id = ? AND user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("product_name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void EditClicked(MouseEvent event) {
        String title = TitleTF.getText();
        Client client = ClientComboBox.getValue();
        String totalValue = TotalValueTF.getText();
        String totalPaid = TotalPaidTF.getText();
        String startDate = StartDate.getValue().toString();
        String deadline = Deadline.getValue().toString();
        String status = StatusComboBox.getValue();
        String selectedProduct = ProductComboBox.getValue();

        if (title.isEmpty() || client == null || totalValue.isEmpty() || totalPaid.isEmpty() || startDate.isEmpty() || deadline.isEmpty() || status.isEmpty() || selectedProduct == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("All fields are required");
            alert.setContentText("Please fill in all fields before proceeding.");
            alert.showAndWait();
            return;
        }

        BigDecimal total;
        BigDecimal paid;
        try {
            total = new BigDecimal(totalValue);
            paid = new BigDecimal(totalPaid);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Total/Paid fields should be numbers", "Please enter valid numbers for total and paid amounts.");
            return;
        }

        // Validate that the total paid is not greater than the total value
        if (paid.compareTo(total) > 0) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Paid amount exceeds Total Value", "The total paid cannot be greater than the total value.");
            return;
        }

        // Get product_id from product name (ensure the product name is valid and exists in the combo box)
        int productId = getProductId(selectedProduct);

        if (productId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Product", "The selected product is invalid. Please try again.");
            return;
        }

        String updateQuery = """
            UPDATE commission
            SET commission_title = ?, client_id = ?, commission_total_value = ?, commission_total_paid = ?, commission_start_date = ?, commission_deadline = ?, commission_status = ?, product_id = ?
            WHERE commission_id = ?
            """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, client.getId()); // Pass client ID
            preparedStatement.setBigDecimal(3, total);
            preparedStatement.setBigDecimal(4, paid);
            preparedStatement.setDate(5, Date.valueOf(startDate));
            preparedStatement.setDate(6, Date.valueOf(deadline));
            preparedStatement.setString(7, status);
            preparedStatement.setInt(8, productId); // Set the product_id
            preparedStatement.setInt(9, commissionID); // The commission ID for updating the correct record

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Commission Updated");
                alert.setContentText("The commission has been successfully updated.");
                alert.showAndWait();

                if (allcommissionController != null) {
                    allcommissionController.loadCommissionData(); // Refresh data in the parent table
                }

                // Close the editing window
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Update Failed", "No rows were updated. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Update Commission", "An error occurred while updating the commission: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void CancelClicked(MouseEvent event) {
        closeWindow(event);
    }

    public void CloseClicked(MouseEvent event) {
        closeWindow(event);
    }
}
