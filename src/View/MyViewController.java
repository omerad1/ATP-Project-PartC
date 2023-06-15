package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.IOException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements IView, Observer {

    private ImageView Hero;
    private MyViewModel mVModel;
    @FXML
    private TextField textField_mazeRows;
    @FXML
    private TextField textField_mazeColumns;
    private MazeDisplay mazeDisplay;

    public void setViewModel(MyViewModel viewModel) {
        this.mVModel = viewModel;
        this.mVModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == mVModel) {
            String message = (String) arg;
            if (message.contains("UpdateMaze"))
                mazeDisplay.setMaze(mVModel.getMaze());
            if (message.contains("UpdatePlayerPosition"))
                mazeDisplay.setPlayerPos(mVModel.getPlayerRow(), mVModel.getPlayerCol());
            if (message.contains("UpdateSolution"))
                mazeDisplay.setMazeSolution(mVModel.getMazeSolution());
            if (message.contains("FoundGoal")) {
                // todo : do something
            }
            mazeDisplay.draw();
        }
    }

    public void generateMaze(ActionEvent event) {
        try {

            int rows = Integer.parseInt(textField_mazeRows.getText());
            int cols = Integer.parseInt(textField_mazeColumns.getText());
            System.out.println("rows : " + rows + "cols : " + cols);

            mVModel.generateMaze(rows, cols);
        } catch (NumberFormatException e) {
            // add a message to the user?
        }
    }

    public void NewAction(ActionEvent actionEvent) {
        mVModel.generateMaze(100, 100);
        actionEvent.consume();
    }

    public void SaveAction(ActionEvent actionEvent) {
        mVModel.saveMaze();
        actionEvent.consume();
    }

    public void LoadAction(ActionEvent actionEvent) {
        mVModel.loadMaze();
        actionEvent.consume();
    }

    public void PropertiesAction(ActionEvent actionEvent) {
        // todo : die
    }

    public void ExitAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are You Sure You Want To Exit The Game?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) { // User chose to exit
            mVModel.endGame();
            System.exit(0);
        }
        // User canceled or closed the dialog
        actionEvent.consume();

    }


    public void keyPressed(KeyEvent keyEvent) {
        mVModel.movePlayer(keyEvent.getCode());
        keyEvent.consume();
    }

    public void AboutAction(ActionEvent event) throws IOException {
        String googleFontsCSS = "https://fonts.googleapis.com/css2?family=Diphylleia&display=swap";
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("About.fxml")));
        Scene scene2 = new Scene(root);

        MenuItem menuItem = (MenuItem) event.getSource();

        // Find the root Parent node
        Parent parent = (Parent) menuItem.getParentPopup().getOwnerNode();
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }

        Scene scene = parent.getScene();

        // Get the Window (Stage) associated with the Scene
        Stage stage = (Stage) scene.getWindow();

        root.getStylesheets().add(googleFontsCSS);
        stage.setScene(scene2);
        stage.show();

        Object[] descLabel = root.lookupAll(".descText").toArray();
        double percentage = 0.0215;

        for (Object node : descLabel) {
            Label tempLabel = (Label) node;
            tempLabel.styleProperty().bind(
                    Bindings.concat("-fx-font-size: ", stage.widthProperty().multiply(percentage), "px;")
            );
        }
    }

    public void HelpAction(ActionEvent event) throws IOException {
        String googleFontsCSS = "https://fonts.googleapis.com/css2?family=Diphylleia&display=swap";
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Help.fxml")));
        Scene scene2 = new Scene(root);

        MenuItem menuItem = (MenuItem) event.getSource();

        // Find the root Parent node
        Parent parent = (Parent) menuItem.getParentPopup().getOwnerNode();
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }

        Scene scene = parent.getScene();

        // Get the Window (Stage) associated with the Scene
        Stage stage = (Stage) scene.getWindow();

        root.getStylesheets().add(googleFontsCSS);
        stage.setScene(scene2);
        stage.show();

        Object[] descLabel = root.lookupAll(".descText").toArray();
        double percentage = 0.0215;

        for (Object node : descLabel) {
            Label tempLabel = (Label) node;
            tempLabel.styleProperty().bind(
                    Bindings.concat("-fx-font-size: ", stage.widthProperty().multiply(percentage), "px;")
            );
        }
    }

    public void backToMenu(ActionEvent event)  {
        Button btm = (Button) event.getSource();
        btm.getStyleClass().add("Btn_pressed");
        btm.getStyleClass().remove("Btn_start");

        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> {
            btm.getStyleClass().add("Btn_start");
            btm.getStyleClass().remove("Btn_pressed");

            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MyView.fxml")));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        pause.play();
    }

    public void AssingHero(MouseEvent mouseEvent) {
        Hero = (ImageView) mouseEvent.getSource();
        System.out.println(Hero);
    }
    public void startGame(ActionEvent event){
        Button btm = (Button) event.getSource();
        btm.getStyleClass().add("Btn_pressed");
        btm.getStyleClass().remove("Btn_start");

        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> {
            btm.getStyleClass().add("Btn_start");
            btm.getStyleClass().remove("Btn_pressed");


            try {

                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("CharacterChoose.fxml")));

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.setFullScreen(true);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();


    }

}
