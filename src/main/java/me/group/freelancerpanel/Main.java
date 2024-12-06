package me.group.freelancerpanel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.group.freelancerpanel.controllers.LoginController;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FrontPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 738, 623); // 738, 623 // 1200, 758
        stage.setTitle("Waltera");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
