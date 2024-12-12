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
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditProductController {

    @FXML
    private TextField ProductNameTF;

    @FXML
    private TextArea ProductDescriptionTF;

    private int productId; // Product ID to edit
    private ProductController productController;

    public void setProductID(int productId) {
        this.productId = productId;
        loadProductData(); // Load the data of the product
    }

    public void setProductsController(ProductController productController) {
        this.productController = productController;
    }

    // Load product data into the form fields
    private void loadProductData() {
        String query = "SELECT product_name, product_description FROM product WHERE product_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ProductNameTF.setText(resultSet.getString("product_name"));
                ProductDescriptionTF.setText(resultSet.getString("product_description"));
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Not Found");
                alert.setHeaderText("Product Not Found");
                alert.setContentText("No product found with the provided ID.");
                alert.showAndWait();
                closeWindow();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Load Product Data");
            alert.setContentText("An error occurred while loading the product data: " + e.getMessage());
            alert.showAndWait();
            closeWindow();
        }
    }

    // Update product details in the database
    public void EditClicked(MouseEvent event) {
        String productName = ProductNameTF.getText();
        String productDescription = ProductDescriptionTF.getText();

        if (productName.isEmpty() || productDescription.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("All fields are required");
            alert.setContentText("Please fill in all fields before proceeding.");
            alert.showAndWait();
            return;
        }

        String updateQuery = "UPDATE product SET product_name = ?, product_description = ? WHERE product_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, productName);
            preparedStatement.setString(2, productDescription);
            preparedStatement.setInt(3, productId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Product Updated");
                alert.setContentText("The product has been successfully updated.");
                alert.showAndWait();

                if (productController != null) {
                    productController.loadProductData(); // Reload data in the product table
                }

                closeWindow();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to Update Product");
                alert.setContentText("An error occurred while updating the product. Please try again.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Update Product");
            alert.setContentText("An error occurred while connecting to the database: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void CancelClicked(MouseEvent event) {
        closeWindow();
    }

    public void CloseClicked(MouseEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) ProductNameTF.getScene().getWindow();
        stage.close();
    }
}
