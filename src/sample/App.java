package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("UDP chat");
        primaryStage.setScene(new Scene(root, 635, 502));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
        Platform.exit();
    }
}
