package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import javafx.scene.media.MediaView;

public class MyViewController implements IView, Observer {
    private Image Hero;
    private static MyViewModel mVModel;
    @FXML
    private TextField textField_mazeRows;
    @FXML
    private TextField textField_mazeColumns;
    @FXML
    public  MazeDisplay mazeDisplay;
    @FXML
    private MediaView mediaView;
    private int rows;
    private int cols;

    public void setViewModel(MyViewModel viewModel) {
        mVModel = viewModel;
        mVModel.addObserver(this);
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
            mazeDisplay.requestFocus();

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
        System.out.println("key pressed" + keyEvent.getCode() );
        mVModel.movePlayer(keyEvent.getCode());
        keyEvent.consume();
    }

    public void AboutAction(ActionEvent event) throws IOException {

        Object[] descLabel = TestView.about_root.lookupAll(".descText").toArray();
        double percentage = 0.0215;
        for (Object node : descLabel) {
            Label tempLabel = (Label) node;
            tempLabel.styleProperty().bind(
                    Bindings.concat("-fx-font-size: ",TestView.about_stage.widthProperty().multiply(percentage), "px;")
            );
        }
        TestView.about_stage.show();
        TestView.mainStage.hide();
    }

    public void HelpAction(ActionEvent event) throws IOException {

        Object[] descLabel = TestView.help_root.lookupAll(".descText").toArray();
        double percentage = 0.0215;
        for (Object node : descLabel) {
            Label tempLabel = (Label) node;
            tempLabel.styleProperty().bind(
                    Bindings.concat("-fx-font-size: ", TestView.help_stage.widthProperty().multiply(percentage), "px;")
            );
        }
        TestView.help_stage.show();
        TestView.mainStage.hide();
    }

    public void backToMenu(ActionEvent event) {
        Button btm = (Button) event.getSource();
        btm.getStyleClass().add("Btn_pressed");
        btm.getStyleClass().remove("Btn_start");

        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> {
            btm.getStyleClass().add("Btn_start");
            btm.getStyleClass().remove("Btn_pressed");

            // Get the current stage from the event source
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Show the main stage
            TestView.mainStage.show();

            // Hide the current stage
            currentStage.hide();
        });
        pause.play();
    }
    public void PlaySound(){
        if(mediaView.getMediaPlayer() == null){
            try {
                String path = getClass().getResource("/backgroundSound.mp3").toURI().toString();
                Media media = new Media(path);
                MediaPlayer player = new MediaPlayer(media);
                mediaView.setMediaPlayer(player);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        mediaView.getMediaPlayer().play();

    }
    public void AssingHero(MouseEvent mouseEvent) {
        Hero = ((ImageView)(mouseEvent.getSource())).getImage();
        if(mazeDisplay!= null) {
            mazeDisplay.setHero(Hero);
            mVModel.generateMaze(rows, cols);
            TestView.mazeDisplay_stage.show();
            TestView.characterChoose_stage.hide();
        }
        else{
            System.out.println("maze display is null :(");
        }
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
            TestView.characterChoose_stage.show();
            TestView.mainStage.hide();

        });
        pause.play();


    }

    private File getFileFromUser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fileChooser.setInitialDirectory(new File(System.getProperty("./resources")));
        return fileChooser.showSaveDialog(TestView.mainStage);
    }

}
