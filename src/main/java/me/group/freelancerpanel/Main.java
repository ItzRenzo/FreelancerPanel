package me.group.freelancerpanel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FrontPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 738, 623); // 738, 623 // 1200, 758
        stage.setTitle("Axiom");
        stage.setScene(scene);

        Image appIcon = new Image(getClass().getResourceAsStream("/me/group/freelancerpanel/images/logo.png"));
        stage.getIcons().add(appIcon);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
