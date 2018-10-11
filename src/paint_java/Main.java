package paint_java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    public static Stage root1;
    public static Main main1;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));

        // TODO выяснить как запускать проект из ИДЕА

        root1 = primaryStage;
        main1 = this;

        primaryStage.setTitle("Test image");
        primaryStage.setScene(new Scene(root, primaryStage.getWidth(), primaryStage.getHeight()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}