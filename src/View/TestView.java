package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Objects;

public class TestView extends Application {
    public static Stage myStage;
    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        myStage = stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MyView.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
}
