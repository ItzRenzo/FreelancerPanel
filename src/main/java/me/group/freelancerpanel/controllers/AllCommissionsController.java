package me.group.freelancerpanel.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AllCommissionsController {

    @FXML
    private TableView<Commission> commissionTable;

    @FXML
    private TableColumn<Commission, Integer> CommissionID;

    @FXML
    private TableColumn<Commission, String> CommissionTitle;

    @FXML
    private TableColumn<Commission, String> CommissionClient;

    @FXML
    private TableColumn<Commission, Double> CommissionTotalValue;

    @FXML
    private TableColumn<Commission, Double> CommissionTotalPaid;

    @FXML
    private TableColumn<Commission, String> CommissionStartDate;

    @FXML
    private TableColumn<Commission, String> CommissionDeadline;

    @FXML
    private TableColumn<Commission, String> CommissionProduct;

    @FXML
    private TableColumn<Commission, String> CommissionStatus;

    @FXML
    private TreeView<String> CommissionTree;
    @FXML
    private TreeView<String> RequestTree;
    @FXML
    private TreeView<String> QuotesTree;

    @FXML
    private Text User_name;

    @FXML
    private TextFlow User_name2;

    @FXML
    private Text User_email;

    private int userId;
    private String username;
    private String email;

    // Setter for userId
    public void setUserId(int userId) {
        this.userId = userId;
        loadCommissionData();
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
        if (User_name != null && User_name2 != null) { // Ensure UI components are initialized
            User_name.setText(username);
            // Create the username text
            Text usernameText = new Text(username);
            usernameText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 14px; -fx-font-weight: bold;");

            // Create the " / Overview" text
            Text overviewText = new Text(" / Commissions");
            overviewText.setStyle("-fx-fill: #aeaeae; -fx-font-size: 14px; -fx-font-weight: bold;");

            // Add both Text nodes to the TextFlow
            User_name2.getChildren().clear(); // Clear any existing content
            User_name2.getChildren().addAll(usernameText, overviewText);
        }
    }

    // Setter for user email
    public void setUserEmail(String email) {
        this.email = email;
        if (User_email != null) { // Ensure UI components are initialized
            User_email.setText(email);
        }
    }


    @FXML
    public void initialize() {
        initializeCommissionTree();
        initializeRequestTree();
        initializeQuotesTree();

        CommissionID.setStyle("-fx-text-fill: white;");
        CommissionTitle.setStyle("-fx-text-fill: white;");
        CommissionClient.setStyle("-fx-text-fill: white;");
        CommissionTotalValue.setStyle("-fx-text-fill: white;");
        CommissionTotalPaid.setStyle("-fx-text-fill: white;");
        CommissionStartDate.setStyle("-fx-text-fill: white;");
        CommissionDeadline.setStyle("-fx-text-fill: white;");
        CommissionProduct.setStyle("-fx-text-fill: white;");
        CommissionStatus.setStyle("-fx-text-fill: white;");

        // Bind columns to model properties
        CommissionID.setCellValueFactory(cellData -> cellData.getValue().commissionIDProperty().asObject());
        CommissionTitle.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        CommissionClient.setCellValueFactory(cellData -> cellData.getValue().clientProperty());
        CommissionTotalValue.setCellValueFactory(cellData -> cellData.getValue().totalValueProperty().asObject());
        CommissionTotalPaid.setCellValueFactory(cellData -> cellData.getValue().totalPaidProperty().asObject());
        CommissionStartDate.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        CommissionDeadline.setCellValueFactory(cellData -> cellData.getValue().deadlineProperty());
        CommissionProduct.setCellValueFactory(cellData -> cellData.getValue().productProperty());
        CommissionStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Load data into the table
        loadCommissionData();

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

        // Update the UI fields with initial values (if they are already set)
        if (username != null) {
            User_name.setText(username);
            // Create the username text
            Text usernameText = new Text(username);
            usernameText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 14px; -fx-font-weight: bold;");

            // Create the " / Overview" text
            Text overviewText = new Text(" / Commissions");
            overviewText.setStyle("-fx-fill: #aeaeae; -fx-font-size: 14px; -fx-font-weight: bold;");

            // Add both Text nodes to the TextFlow
            User_name2.getChildren().clear(); // Clear any existing content
            User_name2.getChildren().addAll(usernameText, overviewText);
        }
        if (email != null) {
            User_email.setText(email);
        }
    }

    public void loadCommissionData() {
        // ObservableList to hold commission data
        ObservableList<Commission> commissions = FXCollections.observableArrayList();

        try {
            // Assuming a database connection is established
            Connection connection = DatabaseHandler.getConnection();
            // SQL query to join the commission, client, and product tables
            String query = "SELECT c.commission_id, c.commission_title, cl.client_name, c.commission_total_value, " +
                    "c.commission_total_paid, c.commission_start_date, c.commission_deadline, " +
                    "p.product_name, c.commission_status " +
                    "FROM commission c " +
                    "LEFT JOIN client cl ON c.client_id = cl.client_id " +
                    "LEFT JOIN product p ON c.product_id = p.product_id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                // Populate the Commission object with the data
                commissions.add(new Commission(
                        resultSet.getInt("commission_id"),
                        resultSet.getString("commission_title"),
                        resultSet.getString("client_name"), // Fetch client_name instead of client_id
                        resultSet.getDouble("commission_total_value"),
                        resultSet.getDouble("commission_total_paid"),
                        resultSet.getString("commission_start_date"),
                        resultSet.getString("commission_deadline"),
                        resultSet.getString("product_name"), // Fetch product_name instead of product_id
                        resultSet.getString("commission_status")
                ));
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set the data to the TableView
        commissionTable.setItems(commissions);
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

            AllCommissionsController allcommissionController = loader.getController();
            allcommissionController.setUserId(userId);
            allcommissionController.setUsername(username);
            allcommissionController.setUserEmail(email);

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

            AllRequestController allrequestController = loader.getController();
            allrequestController.setUserId(userId);
            allrequestController.setUsername(username);
            allrequestController.setUserEmail(email);

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

            AllQuotesController allquotesController = loader.getController();
            allquotesController.setUserId(userId);
            allquotesController.setUsername(username);
            allquotesController.setUserEmail(email);

            // Get the current stage and set the scene only once
            Stage stage = (Stage) CommissionTree.getScene().getWindow();
            Scene adminScene = new Scene(adminRoot);
            stage.setScene(adminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OverviewClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/Dashboard.fxml"));
            Parent AdminRoot = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUserId(userId);
            dashboardController.setUsername(username);
            dashboardController.setUserEmail(email);

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

            ClientsController clientsController = loader.getController();
            clientsController.setUserId(userId);
            clientsController.setUsername(username);
            clientsController.setUserEmail(email);

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

            InvoiceController invoiceController = loader.getController();
            invoiceController.setUserId(userId);
            invoiceController.setUsername(username);
            invoiceController.setUserEmail(email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene AdminScene = new Scene(AdminRoot);
            stage.setScene(AdminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Invoice.fxml: " + e.getMessage());
        }
    }

    public void ProductClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/Product.fxml"));
            Parent AdminRoot = loader.load();

            ProductController productController = loader.getController();
            productController.setUserId(userId);
            productController.setUsername(username);
            productController.setUserEmail(email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene AdminScene = new Scene(AdminRoot);
            stage.setScene(AdminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Product.fxml: " + e.getMessage());
        }
    }

    public void EditClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/ID_GUI.fxml"));
            Parent idRoot = loader.load();

            IDGUIController idController = loader.getController();
            idController.setUserId(userId);
            idController.setCategoryName("Commission"); // Set category name to "Product"
            idController.setAllCommissionsController(this); // Pass reference for further actions

            Stage stage = new Stage();
            stage.setScene(new Scene(idRoot));
            stage.setTitle("Enter Commission ID");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load ID GUI");
            alert.setContentText("An error occurred while loading the ID GUI: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void NewClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/NewCommission.fxml"));
            Parent adminRoot = loader.load();

            NewCommissionController newCommissionController = loader.getController();
            newCommissionController.setUserId(userId);
            newCommissionController.setAllCommissionsController(this);

            Stage newStage = new Stage();
            Scene adminScene = new Scene(adminRoot);

            newStage.setResizable(false);
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.setScene(adminScene);
            newStage.setTitle("New Commission");
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading NewClient.fxml: " + e.getMessage());
        }
    }
}

