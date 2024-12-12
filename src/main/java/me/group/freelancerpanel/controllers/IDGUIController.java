package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class IDGUIController {

    @FXML
    private TextFlow IDText;

    @FXML
    private TextFlow EnterIDText;

    @FXML
    private TextField IDInputField;

    private String categoryName; // For dynamic titles like "Product ID"
    private ProductController productsController; // Reference for ProductController
    private ClientsController clientsController; // Reference for ClientsController

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        Text idText = new Text(categoryName + " ID");
        idText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Dynamically update the label text
        Text enterIDText = new Text("Enter " + categoryName + " ID:");
        enterIDText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 18px; -fx-font-weight: bold;");

        IDText.getChildren().clear();
        IDText.getChildren().addAll(idText);

        EnterIDText.getChildren().clear();
        EnterIDText.getChildren().addAll(enterIDText);
    }

    public void setProductsController(ProductController productsController) {
        this.productsController = productsController;
    }

    public void setClientsController(ClientsController clientsController) {
        this.clientsController = clientsController;
    }

    public void EnterClicked(MouseEvent event) {
        String enteredID = IDInputField.getText();

        if (enteredID == null || enteredID.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("No ID Entered");
            alert.setContentText("Please enter a valid ID to proceed.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader;
            Parent editRoot;

            // Load the appropriate editing GUI based on the controller reference
            if (productsController != null) {
                loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/EditProduct.fxml"));
                editRoot = loader.load();

                EditProductController editController = loader.getController();
                editController.setProductID(Integer.parseInt(enteredID));
                editController.setProductsController(productsController);
            } else if (clientsController != null) {
                loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/EditClient.fxml"));
                editRoot = loader.load();

                EditClientController editController = loader.getController();
                editController.setClientID(Integer.parseInt(enteredID));
                editController.setClientsController(clientsController);
            } else {
                throw new IllegalStateException("No controller reference set!");
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(editRoot));
            stage.setTitle("Edit " + categoryName);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

            // Close the ID GUI after loading the edit screen
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Edit GUI");
            alert.setContentText("An error occurred while loading the Edit GUI: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void CloseClicked(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
