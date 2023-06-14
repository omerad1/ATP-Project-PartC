package View;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Objects;

public class TestView extends Application {
    public static Stage myStage;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        myStage = stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MyView.fxml")));
        String googleFontsCSS = "https://fonts.googleapis.com/css2?family=Diphylleia&display=swap";
        root.getStylesheets().add(googleFontsCSS);

        Label descLabel = (Label) root.lookup("#descLabel");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


        double percentage = 0.0215;
        descLabel.styleProperty().bind(
                Bindings.concat("-fx-font-size: ", stage.widthProperty().multiply(percentage), "px;")
        );
    }
}

