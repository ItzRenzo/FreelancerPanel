package me.group.freelancerpanel.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AllQuotesController {

    @FXML
    private TreeView<String> CommissionTree;
    @FXML
    private TreeView<String> RequestTree;
    @FXML
    private TreeView<String> QuotesTree;
    @FXML
    private Text User_name;
    @FXML
    private Text User_email;

    @FXML
    public void initialize() {
        initializeCommissionTree();
        initializeRequestTree();
        initializeQuotesTree();

        CommissionTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("All commissions")) {
                loadAllCommissionsView();
            }
        });
        RequestTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("All requests")) {
                loadAllRequestView();
            }
        });
        QuotesTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("All quotes")) {
                loadAllQuotesView();
            }
        });
    }

    @FXML
    private void initializeCommissionTree() {
        // Load the image
        String imagePathCommission = "/me/group/freelancerpanel/images/money.png";
        Image rootImageCommission = new Image(getClass().getResourceAsStream(imagePathCommission));

        // Create an ImageView for the root item
        ImageView rootImageViewCommission = new ImageView(rootImageCommission);
        rootImageViewCommission.setFitHeight(28);
        rootImageViewCommission.setFitWidth(31);

        // Initialize the root item
        TreeItem<String> rootItemCommission = new TreeItem<>("  Commissions", rootImageViewCommission);
        rootItemCommission.setExpanded(false);

        // Sub-items
        TreeItem<String> allCommissions = new TreeItem<>("All commissions");
        TreeItem<String> activeCommissions = new TreeItem<>("Active commissions");
        TreeItem<String> unstartedCommissions = new TreeItem<>("Unstarted commissions");

        // Add sub-items to the root
        rootItemCommission.getChildren().addAll(allCommissions, activeCommissions, unstartedCommissions);

        // Set the root for the TreeView
        CommissionTree.setRoot(rootItemCommission);
        CommissionTree.setShowRoot(true);

        // Add a listener to the root item's expanded property
        rootItemCommission.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded) {
                CommissionTree.setPrefWidth(230);
                CommissionTree.setPrefHeight(125);
            } else {
                CommissionTree.setPrefWidth(218);
                CommissionTree.setPrefHeight(45);
            }
        });
    }

    @FXML
    private void initializeRequestTree() {
        // Request TreeView
        // Load the image
        String imagePathRequest = "/me/group/freelancerpanel/images/question.png";
        Image rootImageRequest = new Image(getClass().getResourceAsStream(imagePathRequest));

        // Create an ImageView for the root item
        ImageView rootImageViewRequest = new ImageView(rootImageRequest);
        rootImageViewRequest.setFitHeight(28);  // Set the height of the image
        rootImageViewRequest.setFitWidth(31);   // Set the width of the image

        // Initialize the root item
        TreeItem<String> rootItemRequest = new TreeItem<>("  Requests", rootImageViewRequest);
        rootItemRequest.setExpanded(false); // Keep the root collapsed by default

        // Sub-items
        TreeItem<String> allRequest = new TreeItem<>("All requests");
        TreeItem<String> pendingRequest = new TreeItem<>("Pending requests");
        TreeItem<String> completedRequest = new TreeItem<>("Completed requests");

        // Add sub-items to the root
        rootItemRequest.getChildren().addAll(allRequest, pendingRequest, completedRequest);

        // Set the root for the TreeView
        RequestTree.setRoot(rootItemRequest);
        RequestTree.setShowRoot(true); // Show the root item

        // Add a listener to the root item's expanded property
        rootItemRequest.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded) {
                RequestTree.setPrefWidth(230);
                RequestTree.setPrefHeight(125); // Set to desired height when expanded
            } else {
                RequestTree.setPrefWidth(218);
                RequestTree.setPrefHeight(45); // Set to smaller height when collapsed
            }
        });
    }

    @FXML
    private void initializeQuotesTree() {
        // Quotes TreeView
        // Load the image
        String imagePathQuotes = "/me/group/freelancerpanel/images/double-quotes.png";
        Image rootImageQuotes = new Image(getClass().getResourceAsStream(imagePathQuotes));

        // Create an ImageView for the root item
        ImageView rootImageViewQuotes = new ImageView(rootImageQuotes);
        rootImageViewQuotes.setFitHeight(28);  // Set the height of the image
        rootImageViewQuotes.setFitWidth(31);   // Set the width of the image

        // Initialize the root item
        TreeItem<String> rootItemQuotes = new TreeItem<>("  Quotes", rootImageViewQuotes);
        rootItemQuotes.setExpanded(false); // Keep the root collapsed by default

        // Sub-items
        TreeItem<String> allQuotes = new TreeItem<>("All quotes");
        TreeItem<String> pendingQuotes = new TreeItem<>("Pending quotes");
        TreeItem<String> acceptedQuotes = new TreeItem<>("Accepted quotes");

        // Add sub-items to the root
        rootItemQuotes.getChildren().addAll(allQuotes, pendingQuotes, acceptedQuotes);

        // Set the root for the TreeView
        QuotesTree.setRoot(rootItemQuotes);
        QuotesTree.setShowRoot(true); // Show the root item

        // Add a listener to the root item's expanded property
        rootItemQuotes.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded) {
                QuotesTree.setPrefWidth(230);
                QuotesTree.setPrefHeight(125); // Set to desired height when expanded
            } else {
                QuotesTree.setPrefWidth(218);
                QuotesTree.setPrefHeight(45); // Set to smaller height when collapsed
            }
        });
    }

    private void loadAllCommissionsView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/AllCommissions.fxml"));
            Parent adminRoot = loader.load();

            // Get the current stage and set the scene only once
            Stage stage = (Stage) CommissionTree.getScene().getWindow();
            Scene adminScene = new Scene(adminRoot);
            stage.setScene(adminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllRequestView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/AllRequest.fxml"));
            Parent adminRoot = loader.load();

            // Get the current stage and set the scene only once
            Stage stage = (Stage) CommissionTree.getScene().getWindow();
            Scene adminScene = new Scene(adminRoot);
            stage.setScene(adminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllQuotesView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/AllQuotes.fxml"));
            Parent adminRoot = loader.load();

            // Get the current stage and set the scene only once
            Stage stage = (Stage) CommissionTree.getScene().getWindow();
            Scene adminScene = new Scene(adminRoot);
            stage.setScene(adminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void NewClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/NewQuote.fxml"));
            Parent adminRoot = loader.load();

            Stage newStage = new Stage();
            Scene adminScene = new Scene(adminRoot);

            newStage.setResizable(false);
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.setScene(adminScene);
            newStage.setTitle("New Quote");
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading NewQuote.fxml: " + e.getMessage());
        }
    }

    public void OverviewClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/Dashboard.fxml"));
            Parent AdminRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene AdminScene = new Scene(AdminRoot);
            stage.setScene(AdminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Dashboard.fxml: " + e.getMessage());
        }
    }

    public void ClientsClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/Clients.fxml"));
            Parent AdminRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene AdminScene = new Scene(AdminRoot);
            stage.setScene(AdminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Clients.fxml: " + e.getMessage());
        }
    }

    public void InvoiceClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/Invoice.fxml"));
            Parent AdminRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene AdminScene = new Scene(AdminRoot);
            stage.setScene(AdminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Invoice.fxml: " + e.getMessage());
        }
    }
}