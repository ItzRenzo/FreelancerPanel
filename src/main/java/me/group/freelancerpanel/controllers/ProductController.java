package me.group.freelancerpanel.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductController {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, String> productName;

    @FXML
    private TableColumn<Product, String> productDescription;


    @FXML
    private TreeView<String> CommissionTree;
    @FXML
    private TreeView<String> RequestTree;
    @FXML
    private TreeView<String> QuotesTree;

    @FXML
    private Text ActiveCommissionValue;

    @FXML
    private Text NumberCommissions;

    @FXML
    private Text User_name;

    @FXML
    private TextFlow User_name2;

    @FXML
    private Text User_email;

    @FXML
    private ImageView ProfilePicture;

    private int userId;
    private String username;
    private String email;

    // Setter for userId
    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("ProductController: User ID set to " + userId);
        loadProductData(); // Ensure data is loaded after userId is set
        loadDashboardData();
        loadProfilePicture();
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
        if (User_name != null && User_name2 != null) { // Ensure UI components are initialized
            User_name.setText(username);
            // Create the username text
            Text usernameText = new Text(username);
            usernameText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 14px; -fx-font-weight: bold;");

            Text overviewText = new Text(" / Products");
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

        // Set up the TableView columns
        productName.setStyle("-fx-text-fill: white;");
        productDescription.setStyle("-fx-text-fill: white;");

        // Configure the product table columns
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productDescription.setCellValueFactory(new PropertyValueFactory<>("productDescription"));

        // Load product data into the table
        loadProductData();

        CommissionTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("All commissions")) {
                loadAllCommissionsView();
            }
        });
        CommissionTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("Active commissions")) {
                loadActiveCommissionsView();
            }
        });
        CommissionTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("Unstarted commissions")) {
                loadUnstartedCommissionsView();
            }
        });
        RequestTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("All requests")) {
                loadAllRequestView();
            }
        });
        RequestTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("Pending requests")) {
                loadPendingRequestView();
            }
        });
        RequestTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("Completed requests")) {
                loadCompletedRequestView();
            }
        });
        QuotesTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("All quotes")) {
                loadAllQuotesView();
            }
        });
        QuotesTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("Pending quotes")) {
                loadPendingQuotesView();
            }
        });
        QuotesTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().equals("Accepted quotes")) {
                loadAcceptedQuotesView();
            }
        });

        // Update the UI fields with initial values (if they are already set)
        if (username != null) {
            User_name.setText(username);
            // Create the username text
            Text usernameText = new Text(username);
            usernameText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 14px; -fx-font-weight: bold;");

            // Create the " / Overview" text
            Text overviewText = new Text(" / Products");
            overviewText.setStyle("-fx-fill: #aeaeae; -fx-font-size: 14px; -fx-font-weight: bold;");

            // Add both Text nodes to the TextFlow
            User_name2.getChildren().clear(); // Clear any existing content
            User_name2.getChildren().addAll(usernameText, overviewText);
        }
        if (email != null) {
            User_email.setText(email);
        }
    }

    @FXML
    private void loadProfilePicture() {
        String query = "SELECT profile_picture FROM Freelancer WHERE user_id = ?";
        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String filePath = resultSet.getString("profile_picture");
                if (filePath != null && !filePath.isEmpty()) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        // Load the image for ProfilePicture (43x43)
                        Image smallImage = new Image(file.toURI().toString(), 43, 43, true, true);
                        ProfilePicture.setImage(smallImage);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Load Profile Picture", e.getMessage());
        }
    }

    private void loadDashboardData() {
        loadActiveCommissionValue();
        loadNumberCommissions();
    }

    private void loadActiveCommissionValue() {
        String query = """
        SELECT SUM(commission_total_value) AS active_value
        FROM commission
        WHERE user_id = ? AND commission_status != 'Completed';
    """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ActiveCommissionValue.setText("₱ " + resultSet.getDouble("active_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNumberCommissions() {
        String query = """
            SELECT COUNT(*) AS open_commissions
            FROM commission
            WHERE user_id = ?
            AND commission_status IN ('Not Started', 'In Progress', 'Paused');
            """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int openCommissions = resultSet.getInt("open_commissions");
                NumberCommissions.setText(openCommissions + " Commissions");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Load Open Commissions", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void loadProductData() {
        ObservableList<Product> productList = FXCollections.observableArrayList();

        String query = "SELECT product_id, product_name, product_description FROM product WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId); // Use the logged-in user's ID
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("product_id");
                String name = resultSet.getString("product_name");
                String description = resultSet.getString("product_description");

                productList.add(new Product(id, name, description));
            }

            productTable.setItems(productList);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Load Products");
            alert.setContentText("An error occurred while loading product data.");
            alert.showAndWait();
        }
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

    private void loadActiveCommissionsView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/ActiveCommissions.fxml"));
            Parent adminRoot = loader.load();

            ActiveCommissionsController activecommissionsController = loader.getController();
            activecommissionsController.setUserId(userId);
            activecommissionsController.setUsername(username);
            activecommissionsController.setUserEmail(email);

            // Get the current stage and set the scene only once
            Stage stage = (Stage) CommissionTree.getScene().getWindow();
            Scene adminScene = new Scene(adminRoot);
            stage.setScene(adminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUnstartedCommissionsView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/UnstartedCommissions.fxml"));
            Parent adminRoot = loader.load();

            UnstartedCommissionsController unstartedcommissionsController = loader.getController();
            unstartedcommissionsController.setUserId(userId);
            unstartedcommissionsController.setUsername(username);
            unstartedcommissionsController.setUserEmail(email);

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

    private void loadPendingRequestView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/PendingRequest.fxml"));
            Parent adminRoot = loader.load();

            PendingRequestController pendingrequestController = loader.getController();
            pendingrequestController.setUserId(userId);
            pendingrequestController.setUsername(username);
            pendingrequestController.setUserEmail(email);

            // Get the current stage and set the scene only once
            Stage stage = (Stage) CommissionTree.getScene().getWindow();
            Scene adminScene = new Scene(adminRoot);
            stage.setScene(adminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCompletedRequestView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/CompletedRequest.fxml"));
            Parent adminRoot = loader.load();

            CompletedRequestController completedrequestController = loader.getController();
            completedrequestController.setUserId(userId);
            completedrequestController.setUsername(username);
            completedrequestController.setUserEmail(email);

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

    private void loadPendingQuotesView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/PendingQuotes.fxml"));
            Parent adminRoot = loader.load();

            PendingQuotesController pendingquotesController = loader.getController();
            pendingquotesController.setUserId(userId);
            pendingquotesController.setUsername(username);
            pendingquotesController.setUserEmail(email);

            // Get the current stage and set the scene only once
            Stage stage = (Stage) CommissionTree.getScene().getWindow();
            Scene adminScene = new Scene(adminRoot);
            stage.setScene(adminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAcceptedQuotesView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/AcceptedQuotes.fxml"));
            Parent adminRoot = loader.load();

            AcceptedQuotesController acceptedquotesController = loader.getController();
            acceptedquotesController.setUserId(userId);
            acceptedquotesController.setUsername(username);
            acceptedquotesController.setUserEmail(email);

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

    public void DeleteClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/DeleteGUI.fxml"));
            Parent root = loader.load();

            DeleteGUIController idController = loader.getController();
            idController.setTableName("product"); // Set the table name dynamically
            idController.setReloadTableMethod(this::loadProductData); // Pass the reload method

            Stage stage = new Stage();
            stage.setTitle("Delete Product");
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void EditClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/ID_GUI.fxml"));
            Parent idRoot = loader.load();

            IDGUIController idController = loader.getController();
            idController.setCategoryName("Product"); // Set category name to "Product"
            idController.setProductsController(this); // Pass reference for further actions

            Stage stage = new Stage();
            stage.setScene(new Scene(idRoot));
            stage.setTitle("Enter Product ID");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/NewProduct.fxml"));
            Parent adminRoot = loader.load();

            NewProductController controller = loader.getController();
            controller.setUserId(userId);
            controller.setProductsController(this); // Pass the reference

            Stage newStage = new Stage();
            Scene adminScene = new Scene(adminRoot);

            newStage.setResizable(false);
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.setScene(adminScene);
            newStage.setTitle("New Product");
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading NewProduct.fxml: " + e.getMessage());
        }
    }

    public void ProfileClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/group/freelancerpanel/Profile.fxml"));
            Parent AdminRoot = loader.load();

            ProfileController profileController = loader.getController();
            profileController.setUserId(userId);
            profileController.setUsername(username);
            profileController.setUserEmail(email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene AdminScene = new Scene(AdminRoot);
            stage.setScene(AdminScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Profile.fxml: " + e.getMessage());
        }
    }
}
