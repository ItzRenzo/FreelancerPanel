package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class NewCommissionController {

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

    private AllCommissionsController allcommissionsController;
    private ActiveCommissionsController activecommissionsController;
    private UnstartedCommissionsController unstartedcommissionsController;

    public void setAllCommissionsController(AllCommissionsController allcommissionsController) {
        this.allcommissionsController = allcommissionsController;
    }

    public void setActiveCommissionsController(ActiveCommissionsController activecommissionsController) {
        this.activecommissionsController = activecommissionsController;
    }

    public void setUnstartedCommissionsController(UnstartedCommissionsController unstartedcommissionsController) {
        this.unstartedcommissionsController = unstartedcommissionsController;
    }

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        loadClientsIntoComboBox(); // Load clients after setting user ID
        loadProductsIntoComboBox(); // Load products after setting user ID
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
        String query = "SELECT product_name FROM product WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String productName = resultSet.getString("product_name");
                ProductComboBox.getItems().add(productName);
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

    // Get the product_id based on the selected product name
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

    public void CreateClicked(MouseEvent event) {
        Client selectedClient = ClientComboBox.getValue();
        String selectedProduct = ProductComboBox.getValue();
        String title = TitleTF.getText();
        String status = StatusComboBox.getValue();
        LocalDate startDate = StartDate.getValue();
        LocalDate dueDate = Deadline.getValue();

        if (selectedClient == null || selectedProduct == null || title.isEmpty() || status == null || startDate == null || dueDate == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "All fields are required", "Please fill in all fields before proceeding.");
            return;
        }

        BigDecimal total;
        BigDecimal paid;
        try {
            total = new BigDecimal(TotalValueTF.getText());
            paid = new BigDecimal(TotalPaidTF.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Total/Paid fields should be numbers", "Please enter valid numbers for total and paid amounts.");
            return;
        }

        // Validate that the total paid is not greater than the total value
        if (paid.compareTo(total) > 0) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Paid amount exceeds Total Value", "The total paid cannot be greater than the total value.");
            return;
        }

        // Get the product_id based on the selected product name
        int productId = getProductId(selectedProduct);

        if (productId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Product", "The selected product is invalid. Please try again.");
            return;
        }

        // Insert commission with product_id
        String insertQuery = "INSERT INTO commission (user_id, client_id, commission_title, commission_total_value, commission_total_paid, commission_start_date, commission_deadline, commission_status, product_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, selectedClient.getId());
            preparedStatement.setString(3, title);
            preparedStatement.setBigDecimal(4, total);
            preparedStatement.setBigDecimal(5, paid);
            preparedStatement.setDate(6, Date.valueOf(startDate));
            preparedStatement.setDate(7, Date.valueOf(dueDate));
            preparedStatement.setString(8, status);
            preparedStatement.setInt(9, productId);  // Set the product_id

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Commission Created", "The new commission has been successfully added.");

                if (allcommissionsController != null) {
                    allcommissionsController.loadCommissionData();
                }

                closeWindow(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Create Commission", "An error occurred while adding the commission. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Create Commission", "An error occurred while connecting to the database: " + e.getMessage());
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


