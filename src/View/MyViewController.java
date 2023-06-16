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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements IView, Observer {
    public Parent root;
    public Scene scene;
    public Stage stage;
    private Image Hero;
    private MyViewModel mVModel;
    @FXML
    private TextField textField_mazeRows;
    @FXML
    private TextField textField_mazeColumns;
    @FXML
    public MazeDisplay mazeDisplay;
    private int rows;
    private int cols;

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


    public void NewAction(ActionEvent actionEvent) {
        mVModel.generateMaze(100, 100);
        actionEvent.consume();
    }

    public void SaveAction(ActionEvent actionEvent) {
        if(mVModel.getMaze() == null)
        {
            Alert prob = new Alert(Alert.AlertType.ERROR);
            prob.setContentText("You Can't Save a Maze Before Generation");
            prob.showAndWait();
        }
        File file = getFileFromUser("Save Game");
        mVModel.saveMaze(file);
        actionEvent.consume();
    }

    public void LoadAction(ActionEvent actionEvent) {
        File file = getFileFromUser("Load Game");
        mVModel.loadMaze(file);
        actionEvent.consume();
    }

    public void PropertiesAction(ActionEvent actionEvent) {
        // todo : die !
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
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("About.fxml")));
        fxmlLoader.setController(this);
        root = fxmlLoader.load();
        scene = new Scene(root);

        MenuItem menuItem = (MenuItem) event.getSource();

        // Find the root Parent node
        Parent parent = (Parent) menuItem.getParentPopup().getOwnerNode();
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }

        Scene scene2 = parent.getScene();

        // Get the Window (Stage) associated with the Scene
        stage = (Stage) scene2.getWindow();

        root.getStylesheets().add(googleFontsCSS);
        stage.setScene(scene);
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
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Help.fxml")));
        fxmlLoader.setController(this);
        root = fxmlLoader.load();
        scene = new Scene(root);

        MenuItem menuItem = (MenuItem) event.getSource();

        // Find the root Parent node
        Parent parent = (Parent) menuItem.getParentPopup().getOwnerNode();
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }

        Scene scene2 = parent.getScene();

        // Get the Window (Stage) associated with the Scene
        stage = (Stage) scene2.getWindow();

        root.getStylesheets().add(googleFontsCSS);
        stage.setScene(scene);
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
                FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("MyView.fxml")));
                fxmlLoader.setController(this);
                root = fxmlLoader.load();
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                System.out.println(mazeDisplay);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        pause.play();
    }

    public void AssingHero(MouseEvent mouseEvent) {
        Hero = ((ImageView)(mouseEvent.getSource())).getImage();
        System.out.println(mazeDisplay);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("mazeDisplay.fxml")));
            fxmlLoader.setController(this);
            root = fxmlLoader.load();
            stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        mazeDisplay.setHero(Hero);
        mVModel.generateMaze(rows, cols);
        //display maze......
    }
    public void startGame(ActionEvent event){
        try {
            rows = Integer.parseInt(textField_mazeRows.getText());
            cols = Integer.parseInt(textField_mazeColumns.getText());
        } catch (NumberFormatException e) {
            Alert prob = new Alert(Alert.AlertType.ERROR);
            prob.setContentText("Please Enter Valid Maze Sizes");
            prob.showAndWait();
            return;
        }
        Button btm = (Button) event.getSource();
        btm.getStyleClass().add("Btn_pressed");
        btm.getStyleClass().remove("Btn_start");

        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> {
            btm.getStyleClass().add("Btn_start");
            btm.getStyleClass().remove("Btn_pressed");


            try {

                FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("CharacterChoose.fxml")));
                fxmlLoader.setController(this);
                root = fxmlLoader.load();
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                } catch(IOException ex){
                    ex.printStackTrace();
                }

        });
        pause.play();


    }

    private File getFileFromUser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fileChooser.setInitialDirectory(new File(System.getProperty("./resources")));
        return fileChooser.showSaveDialog(TestView.myStage);
    }

}
