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
    private AllCommissionsController allcommissionController; // Reference for AllCommissionsController
    private ActiveCommissionsController activecommissionsController; // Reference for ActiveCommissionsController
    private UnstartedCommissionsController unstartedcommissionsController; // Reference for UnstartedCommissionsController
    private InvoiceController invoiceController; // Reference for InvoiceController
    private AllRequestController allrequestController; // Reference for AllRequestController
    private PendingRequestController pendingrequestController; // Reference for PendingRequestController
    private CompletedRequestController completedrequestController; // Reference for CompletedRequestController
    private AllQuotesController allquotesController; // Reference for AllQuoteController
    private PendingQuotesController pendingquotesController; // Reference for PendingQuoteController
    private AcceptedQuotesController acceptedquotesController; // Reference for AcceptedQuoteController


    private int userId;

    // Set the user ID
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Set the category name and update the UI text
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        Text idText = new Text(categoryName + " ID");
        idText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 18px; -fx-font-weight: bold;");

        Text enterIDText = new Text("Enter " + categoryName + " ID:");
        enterIDText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 18px; -fx-font-weight: bold;");

        IDText.getChildren().clear();
        IDText.getChildren().add(idText);

        EnterIDText.getChildren().clear();
        EnterIDText.getChildren().add(enterIDText);
    }

    // Setters for the different controllers
    public void setProductsController(ProductController productsController) {
        this.productsController = productsController;
    }

    public void setClientsController(ClientsController clientsController) {
        this.clientsController = clientsController;
    }

    public void setAllCommissionsController(AllCommissionsController allcommissionController) {
        this.allcommissionController = allcommissionController;
    }

    public void setActiveCommissionsController(ActiveCommissionsController activecommissionsController) {
        this.activecommissionsController = activecommissionsController;
    }

    public void setUnstartedCommissionsController(UnstartedCommissionsController unstartedcommissionsController) {
        this.unstartedcommissionsController = unstartedcommissionsController;
    }

    public void setInvoiceController(InvoiceController invoiceController) {
        this.invoiceController = invoiceController;
    }

    public void setAllRequestController(AllRequestController allrequestController) {
        this.allrequestController = allrequestController;
    }

    public void setPendingRequestController(PendingRequestController pendingrequestController) {
        this.pendingrequestController = pendingrequestController;
    }

    public void setCompletedRequestController(CompletedRequestController completedrequestController) {
        this.completedrequestController = completedrequestController;
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

    // Handle the "Enter" button click
    public void EnterClicked(MouseEvent event) {
        String enteredID = IDInputField.getText();

        if (enteredID == null || enteredID.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "No ID Entered", "Please enter a valid ID to proceed.");
            return;
        }

        try {
            FXMLLoader loader;
            Parent editRoot;

            // Determine which editor to load based on the set controller
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
            } else if (allcommissionController != null || activecommissionsController != null || unstartedcommissionsController != null) {
                loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/EditCommission.fxml"));
                editRoot = loader.load();

                EditCommissionController editController = loader.getController();
                editController.setCommissionID(Integer.parseInt(enteredID));
                editController.setUserId(userId);
                editController.setCommissionController(allcommissionController);
                editController.setActiveCommissionsController(activecommissionsController);
                editController.setUnstartedCommissionsController(unstartedcommissionsController);
            } else if (invoiceController != null) {
                loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/EditInvoice.fxml"));
                editRoot = loader.load();

                EditInvoiceController editController = loader.getController();
                editController.setInvoiceId(Integer.parseInt(enteredID));
                editController.setUserId(userId);
                editController.setInvoiceController(invoiceController);
            } else if (allrequestController != null || pendingrequestController != null || completedrequestController != null) {
                loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/EditRequest.fxml"));
                editRoot = loader.load();

                EditRequestController editController = loader.getController();
                editController.setRequestId(Integer.parseInt(enteredID));
                editController.setUserId(userId);
                editController.setAllRequestController(allrequestController);
            } else if (allquotesController != null || pendingquotesController != null || acceptedquotesController != null) {
                loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/EditQuote.fxml"));
                editRoot = loader.load();

                EditQuoteController editController = loader.getController();
                editController.setQuoteId(Integer.parseInt(enteredID));
                editController.setUserId(userId);
                editController.setAllQuotesController(allquotesController);
            } else {
                throw new IllegalStateException("No controller reference set!");
            }

            // Open the editor window
            Stage stage = new Stage();
            stage.setScene(new Scene(editRoot));
            stage.setTitle("Edit " + categoryName);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

            // Close the current window
            closeWindow(event);

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Load Edit GUI", "An error occurred: " + e.getMessage());
        }
    }

    // Close the current window
    public void CloseClicked(MouseEvent event) {
        closeWindow(event);
    }

    // Helper to close the window
    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Helper to show an alert
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
