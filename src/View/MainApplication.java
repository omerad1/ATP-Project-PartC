package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApplication extends Application {
    public static Stage mainStage;
    static FXMLLoader help_FxmlLoader;
    static FXMLLoader myView_FxmlLoader;
    static FXMLLoader mazeDisplay_FxmlLoader;
    static FXMLLoader characterChoose_FxmlLoader;
    static FXMLLoader about_FxmlLoader;
    static FXMLLoader props_FxmlLoader;
    static Parent help_root;
    static Parent myView_root;
    static Parent mazeDisplay_root;
    static Parent characterChoose_root;
    static Parent about_root;
    static Parent props_root;
    static Scene myView_scene;
    static Scene mazeDisplay_scene;
    static Scene characterChoose_scene;
    static Scene about_scene;
    static Scene help_scene;
    static Scene props_scene;
    static Stage help_stage;
    static Stage mazeDisplay_stage;
    static Stage characterChoose_stage;
    static Stage about_stage;
    static Stage props_stage;
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {
        String googleFontsCSS = "https://fonts.googleapis.com/css2?family=Diphylleia&display=swap";
        mainStage = stage;


        /** - - - - - -- -- - - - - - - - - - - - - - FXML loaders - - - - - - - - - - - - - - - - - -- - - - - - -*/
        myView_FxmlLoader= new FXMLLoader(Objects.requireNonNull(getClass().getResource("MyView.fxml")));
        mazeDisplay_FxmlLoader= new FXMLLoader(Objects.requireNonNull(getClass().getResource("mazeDisplay.fxml")));
        characterChoose_FxmlLoader= new FXMLLoader(Objects.requireNonNull(getClass().getResource("CharacterChoose.fxml")));
        about_FxmlLoader= new FXMLLoader(Objects.requireNonNull(getClass().getResource("About.fxml")));
        help_FxmlLoader= new FXMLLoader(Objects.requireNonNull(getClass().getResource("Help.fxml")));
        props_FxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Properties.fxml")));

        /** - - - - - -- -- - - - - - - - - - - - - - Setting Controllers - - - - - - - - - - - - - - - - - -- - - - - - -*/
        MyViewController controller = new MyViewController();

        myView_FxmlLoader.setController(controller);
        mazeDisplay_FxmlLoader.setController(controller);
        characterChoose_FxmlLoader.setController(controller);
        about_FxmlLoader.setController(controller);
        help_FxmlLoader.setController(controller);
        props_FxmlLoader.setController(controller);

        /** - - - - - -- -- - - - - - - - - - - - - - Roots - - - - - - - - - - - - - - - - - -- - - - - - -*/

        myView_root = myView_FxmlLoader.load();
        mazeDisplay_root = mazeDisplay_FxmlLoader.load();
        characterChoose_root = characterChoose_FxmlLoader.load();
        about_root = about_FxmlLoader.load();
        help_root = help_FxmlLoader.load();
        props_root = props_FxmlLoader.load();

        /** - - - - - -- -- - - - - - - - - - - - - - applay Fonts - - - - - - - - - - - - - - - - - -- - - - - - -*/

        myView_root.getStylesheets().add(googleFontsCSS);
        mazeDisplay_root.getStylesheets().add(googleFontsCSS);
        characterChoose_root.getStylesheets().add(googleFontsCSS);
        about_root.getStylesheets().add(googleFontsCSS);
        help_root.getStylesheets().add(googleFontsCSS);
        props_root.getStylesheets().add(googleFontsCSS);

        /** - - - - - -- -- - - - - - - - - - - - - - applay controller - - - - - - - - - - - - - - - - - -- - - - - - -*/

        IModel model = new MyModel();
        MyViewModel myViewModel = new MyViewModel(model);
        MyViewController myViewController = myView_FxmlLoader.getController();
        myViewController.setViewModel(myViewModel);


        /** - - - - - -- -- - - - - - - - - - - - - - Creating Scenes - - - - - - - - - - - - - - - - - -- - - - - - -*/
        myView_scene = new Scene(myView_root);
        mazeDisplay_scene = new Scene(mazeDisplay_root);
        characterChoose_scene = new Scene(characterChoose_root);
        about_scene = new Scene(about_root);
        help_scene = new Scene(help_root);
        props_scene = new Scene(props_root);

        /** - - - - - -- -- - - - - - - - - - - - - - Creating Stages - - - - - - - - - - - - - - - - - -- - - - - - -*/

        mazeDisplay_stage = new Stage();
        characterChoose_stage = new Stage();
        about_stage = new Stage();
        help_stage = new Stage();
        props_stage = new Stage();

        characterChoose_stage.setOnCloseRequest(t -> {
            controller.ExitAction();
        });
        about_stage.setOnCloseRequest(t -> {
            controller.ExitAction();
        });
        help_stage.setOnCloseRequest(t -> {
            controller.ExitAction();
        });
        props_stage.setOnCloseRequest(t -> {
            controller.ExitAction();
        });
        mainStage.setOnCloseRequest(t -> {
            controller.ExitAction();
        });
        mainStage.setOnCloseRequest(t -> {
            controller.ExitAction();
        });
        /** - - - - - -- -- - - - - - - - - - - - - - Applying Scenes - - - - - - - - - - - - - - - - - -- - - - - - -*/
        mazeDisplay_stage.setScene(mazeDisplay_scene);
        characterChoose_stage.setScene(characterChoose_scene);
        about_stage.setScene(about_scene);
        help_stage.setScene(help_scene);
        mainStage.setScene(myView_scene);
        props_stage.setScene(props_scene);

        Object[] descLabel = myView_root.lookupAll(".descText").toArray();
        double percentage = 0.0215;

        for (Object node : descLabel) {
            Label tempLabel = (Label) node;
            tempLabel.styleProperty().addListener((observable, oldValue, newValue) -> {
                if (stage.getWidth() > 0) {
                    tempLabel.setStyle("-fx-font-size: " + stage.getWidth() * percentage + "px;");
                }
            });
        }

        mainStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > 0) {
                for (Object node : descLabel) {
                    Label tempLabel = (Label) node;
                    tempLabel.setStyle("-fx-font-size: " + newValue.doubleValue() * percentage + "px;");
                }
            }
        });

        mainStage.show();

    }
}

