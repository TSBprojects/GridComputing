package ru.sstu.vak.gridComputing.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sstu.vak.gridComputing.ui.gui.MainController;

import java.io.IOException;
import java.util.Arrays;

public class Main extends Application {

    private static final Logger log = LogManager.getLogger(Main.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
            primaryStage.setTitle("Route builder");
            primaryStage.setScene(new Scene(root, 800, 500));
            //            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main.png")));
            primaryStage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            primaryStage.show();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            MainController.showError(e.getMessage() + " \nSee logs: 'logs.log'");
        }
    }


    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
