package View;

import ViewModel.MyViewModel;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;


public class MyViewController implements IView, Observer {
    private Image Hero;
    private static MyViewModel mVModel;
    @FXML
    private TextField textField_mazeRows;
    @FXML
    private TextField textField_mazeColumns;
    @FXML
    public MazeDisplay mazeDisplay;
    @FXML
    private MediaView mediaView;
    @FXML
    private AnchorPane anchorPane;
    private File file;
    private Media media;
    private MediaPlayer mediaPlayer;
    private int rows = 21;
    private int cols = 21;
    private Logger logger = LogManager.getLogger();



    public void setViewModel(MyViewModel viewModel) {
        mVModel = viewModel;
        mVModel.addObserver(this);
        Configurator.setRootLevel(Level.DEBUG);
    }

    public void Solve(ActionEvent actionEvent) {
        if (((CheckBox) (actionEvent.getSource())).isSelected()) {
            logger.info("Player has asked for maze solution");
            mazeDisplay.setSol(true);
        } else {
            logger.info("Player has asked to hide the maze solution");
            mazeDisplay.setSol(false);
        }
        mazeDisplay.requestFocus();
    }

    public void displayFinish() {
        Image cong = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/congrats.gif")));
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Congratulations!");
        DialogPane dialogPane = new DialogPane();

        dialogPane.setBackground(new Background(new BackgroundImage(cong, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, new BackgroundSize(1.0, 1.0, true, true, false, false))));
        dialogPane.setMinHeight(300);

        dialogPane.setMinWidth(450);

        alert.setDialogPane(dialogPane);

        ButtonType okButton = new ButtonType("Thank You");

        alert.getButtonTypes().setAll(okButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                alert.close();
            }
        });
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
                mazeDisplay.draw();
                displayFinish();
                mVModel.endGame();
                System.exit(0);
            }
            if(message.contains("loadFromStart")){
                try{
                MainApplication.mainStage.hide();
                MainApplication.characterChoose_stage.hide();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    MainApplication.mazeDisplay_stage.show();
                }
            }
            mazeDisplay.draw();
            mazeDisplay.requestFocus();

        }
    }

    public void onDragged(MouseEvent mouseEvent) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();
        int playerX = mVModel.getPlayerRow();
        int playerY = mVModel.getPlayerCol();
        double propX = mazeDisplay.getPropX();
        double propY = mazeDisplay.getPropY();
        double dx = mouseX - (playerY * propY);
        double dy = mouseY - (playerX * propX);
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                mVModel.movePlayer(KeyCode.RIGHT);
            } else {
                mVModel.movePlayer(KeyCode.LEFT);
            }
        } else {
            if (dy > 0) {
                mVModel.movePlayer(KeyCode.DOWN);
            } else {
                mVModel.movePlayer(KeyCode.UP);
            }
        }
        mouseEvent.consume();
    }


    public void PlaySound(ActionEvent actionEvent) {
        if (mediaPlayer == null) {
            file = new File("C:\\Users\\omera\\OneDrive\\שולחן העבודה\\לימודים\\נושאים מתקדמים בתכנות\\ATP-Project-PartC\\ATP-Project-PartC\\resources\\backgroundSound.mp3");
            media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
        }
        if (((CheckMenuItem) actionEvent.getSource()).isSelected()) {
            mediaPlayer.play();
            mediaPlayer.setVolume(0.3);
            logger.info("Player has chosen to play some music");

        } else {
            mediaPlayer.pause();
            logger.info("Player has stopped the music");
        }


    }

    public void NewAction(ActionEvent actionEvent) {

        MainApplication.characterChoose_stage.show();
        // Get the current stage from the event source
        Stage currentStage = (Stage) ((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow();
        // Hide the current stage
        currentStage.hide();
        MainApplication.characterChoose_stage.show();
    }


    public void SaveAction(ActionEvent actionEvent) {
        if (mVModel.getMaze() == null) {
            Alert prob = new Alert(Alert.AlertType.ERROR);
            prob.setContentText("You Can't Save a Maze Before Generation");
            prob.showAndWait();
        } else {
            File file = getFileFromUser("Save Game",true);
            mVModel.saveMaze(file);
            actionEvent.consume();
        }
    }

    public void LoadAction(ActionEvent actionEvent) {
        File file = getFileFromUser("Load Game",false);
        mVModel.loadMaze(file);
        mazeDisplay.draw();
        actionEvent.consume();
    }

    public void PropertiesAction(ActionEvent actionEvent) {
        InputStream inputStream = getClass().getResourceAsStream("/config.properties");

        if (inputStream != null) {
            // Read the file content
            StringBuilder content = new StringBuilder();
            content.append("Game Properties: \n");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            MainApplication.props_stage.show();
            TextArea textArea = (TextArea) (MainApplication.props_root.lookup("#PropsText"));
            textArea.setText(content.toString());
            textArea.setEditable(false);
            MainApplication.mainStage.hide();
        }
    }

    public void ExitAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are You Sure You Want To Exit The Game?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) { // User chose to exit
            mVModel.endGame();
            System.exit(0);
        }

    }


    public void keyPressed(KeyEvent keyEvent) {
        mVModel.movePlayer(keyEvent.getCode());
        keyEvent.consume();
    }

    public void AboutAction(ActionEvent event) throws IOException {

        Object[] descLabel = MainApplication.about_root.lookupAll(".descText").toArray();
        double percentage = 0.0215;
        for (Object node : descLabel) {
            Label tempLabel = (Label) node;
            tempLabel.styleProperty().bind(
                    Bindings.concat("-fx-font-size: ", MainApplication.about_stage.widthProperty().multiply(percentage), "px;")
            );
        }
        MainApplication.about_stage.show();

        MainApplication.mainStage.hide();
    }

    public void HelpAction(ActionEvent event) throws IOException {

        Object[] descLabel = MainApplication.help_root.lookupAll(".descText").toArray();
        double percentage = 0.0215;
        for (Object node : descLabel) {
            Label tempLabel = (Label) node;
            tempLabel.styleProperty().bind(
                    Bindings.concat("-fx-font-size: ", MainApplication.help_stage.widthProperty().multiply(percentage), "px;")
            );
        }
        MainApplication.help_stage.show();
        MainApplication.mainStage.hide();
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
            MainApplication.mainStage.show();

            // Hide the current stage
            currentStage.hide();
        });
        pause.play();
    }

    public void AssingHero(MouseEvent mouseEvent) {
        mazeDisplay.setAnchorPane(anchorPane);
        Hero = ((ImageView) (mouseEvent.getSource())).getImage();
        if (mazeDisplay != null) {
            mazeDisplay.setHero(Hero);
            mVModel.generateMaze(rows, cols);
            MainApplication.mazeDisplay_stage.show();
            MainApplication.characterChoose_stage.hide();
            logger.info("Player has chosen a new hero!");
        }
    }

    public void startGame(ActionEvent event) {
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
            MainApplication.characterChoose_stage.show();
            MainApplication.mainStage.hide();

        });
        pause.play();


    }

    private File getFileFromUser(String title,boolean save) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fileChooser.setInitialDirectory(new File(getResourcesFolderPath()));
        File selectedFile;
        if(save)
            selectedFile = fileChooser.showSaveDialog(MainApplication.mainStage);
        else{
            selectedFile = fileChooser.showOpenDialog(MainApplication.mainStage);
            return selectedFile;

        }
        if (selectedFile != null) {
            String filePath = getResourcesFolderPath() + selectedFile.getName();
            File destinationFile = new File(filePath);
            selectedFile.renameTo(destinationFile);
        }
        return selectedFile;
    }


    private String getResourcesFolderPath() {
        File file = new File("resources");
        String resourcesFolderPath = file.getAbsolutePath()+"/savedMazes";
        return resourcesFolderPath;
    }
}
