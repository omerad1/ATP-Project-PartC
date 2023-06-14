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

        Object[] descLabel =  root.lookupAll(".descText").toArray();
        System.out.println(descLabel);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


        double percentage = 0.0215;

        for (Object node : descLabel) {
            Label tempLabel = (Label) node;
            tempLabel.styleProperty().bind(
                    Bindings.concat("-fx-font-size: ", stage.widthProperty().multiply(percentage), "px;")
            );
        }


    }
}

