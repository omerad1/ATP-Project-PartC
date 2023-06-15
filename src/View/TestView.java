package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
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
        String googleFontsCSS = "https://fonts.googleapis.com/css2?family=Diphylleia&display=swap";


        myStage = stage;
        FXMLLoader fxmlLoader= new FXMLLoader(Objects.requireNonNull(getClass().getResource("MyView.fxml")));
        Parent root = fxmlLoader.load();
        root.getStylesheets().add(googleFontsCSS);

        IModel model = new MyModel();
        MyViewModel myViewModel = new MyViewModel(model);
        MyViewController myViewController = fxmlLoader.getController();
        myViewController.setViewModel(myViewModel);



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

