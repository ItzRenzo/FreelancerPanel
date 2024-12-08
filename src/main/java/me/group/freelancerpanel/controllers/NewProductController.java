package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NewProductController {

    @FXML
    private TextField ProductNameTF;

    @FXML
    private TextArea ProductDescriptionTF;

    private int userId; // Store the current logged-in user's ID

    private ProductController productController;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setProductsController(ProductController productController) {
        this.productController = productController;
    }

    public void CreateClicked(MouseEvent event) {
        String productnameText = ProductNameTF.getText();
        String productdescText = ProductDescriptionTF.getText();

        if (productnameText.isEmpty() || productdescText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("All fields are required");
            alert.setContentText("Please fill in all fields before proceeding.");
            alert.showAndWait();
            return;
        }

        String insertQuery = "INSERT INTO product (user_id, product_name, product_description) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, productnameText);
            preparedStatement.setString(3, productdescText);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Product Created");
                alert.setContentText("The new product has been successfully added.");
                alert.showAndWait();

                if (productController != null) {
                    productController.loadProductData();
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to Create Product");
                alert.setContentText("An error occurred while adding the product. Please try again.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Create Product");
            alert.setContentText("An error occurred while connecting to the database: " + e.getMessage());
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
