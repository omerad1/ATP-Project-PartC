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
import java.net.URL;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

/**
 * The controller class for the GUI view of the application.
 */
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

    /**
     * Sets the view model for the controller.
     *
     * @param viewModel The view model to be set.
     */
    public void setViewModel(MyViewModel viewModel) {
        mVModel = viewModel;
        mVModel.addObserver(this);
        Configurator.setRootLevel(Level.DEBUG);
    }

    /**
     * Handles the "Solve" button action.
     * Shows or hides the maze solution based on the checkbox selection.
     * Requests focus for the maze display.
     *
     * @param actionEvent The event triggered by the button action.
     */
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

    /**
     * Displays a finish message with a congratulatory image.
     */
    public void displayFinish() {
        // Load congratulatory image
        Image cong = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/congrats.gif")));

        // Create an alert dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Congratulations!");
        DialogPane dialogPane = new DialogPane();

        // Set the congratulatory image as the background of the dialog pane
        dialogPane.setBackground(new Background(new BackgroundImage(cong, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, new BackgroundSize(1.0, 1.0, true, true, false, false))));
        dialogPane.setMinHeight(300);
        dialogPane.setMinWidth(450);

        alert.setDialogPane(dialogPane);

        // Create a "Thank You" button
        ButtonType okButton = new ButtonType("Thank You");

        alert.getButtonTypes().setAll(okButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                alert.close();
            }
        });
    }

    /**
     * Updates the view when the observable object (view model) changes.
     *
     * @param o   The observable object.
     * @param arg The argument passed by the observable.
     */
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
            if (message.contains("loadFromStart")) {
                try {
                    MainApplication.mainStage.hide();
                    MainApplication.characterChoose_stage.hide();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    MainApplication.mazeDisplay_stage.show();
                }
            }
            mazeDisplay.draw();
            mazeDisplay.requestFocus();

        }
    }

    /**
     * Handles the mouse dragged event in the maze display.
     * Calculates the movement direction based on the mouse position relative to the player position
     * and sends the corresponding movement command to the view model.
     *
     * @param mouseEvent The mouse event triggered by dragging the mouse.
     */
    public void onDragged(MouseEvent mouseEvent) {
        // Get the current mouse position
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        // Get the player's current position
        int playerX = mVModel.getPlayerRow();
        int playerY = mVModel.getPlayerCol();

        // Get the proportional values for the maze display
        double propX = mazeDisplay.getPropX();
        double propY = mazeDisplay.getPropY();

        // Calculate the distance between the mouse position and the player's position
        double dx = mouseX - (playerY * propY);
        double dy = mouseY - (playerX * propX);

        // Determine the movement direction based on the mouse position
        if (Math.abs(dx) > Math.abs(dy)) {
            // Horizontal movement
            if (dx > 0) {
                mVModel.movePlayer(KeyCode.RIGHT);
            } else {
                mVModel.movePlayer(KeyCode.LEFT);
            }
        } else {
            // Vertical movement
            if (dy > 0) {
                mVModel.movePlayer(KeyCode.DOWN);
            } else {
                mVModel.movePlayer(KeyCode.UP);
            }
        }

        // Consume the event to prevent further handling
        mouseEvent.consume();
    }


    /**
     * Handles the action event for playing or stopping background sound.
     *
     * @param actionEvent The action event triggered by the user.
     */
    public void PlaySound(ActionEvent actionEvent) {
        // Check if the media player is not initialized
        if (mediaPlayer == null) {
            // Get the resource URL for the background sound file
            URL resource = getClass().getResource("/backgroundSound.mp3");

            // Check if the resource URL is not null
            if (resource != null) {
                // Create a new media instance using the resource URL
                media = new Media(resource.toString());

                // Create a new media player using the media instance
                mediaPlayer = new MediaPlayer(media);
            }
        }

        // Check if the source of the action event is a CheckMenuItem and it is selected
        if (((CheckMenuItem) actionEvent.getSource()).isSelected()) {
            // Check if the media player is not null
            if (mediaPlayer != null) {
                // Start playing the media
                mediaPlayer.play();

                // Set the volume of the media player to 0.3 (30%)
                mediaPlayer.setVolume(0.3);

                // Log a message indicating that the player has chosen to play music
                logger.info("Player has chosen to play some music");
            }
        } else {
            // Check if the media player is not null
            if (mediaPlayer != null) {
                // Pause the media player
                mediaPlayer.pause();

                // Log a message indicating that the player has stopped the music
                logger.info("Player has stopped the music");
            }
        }
    }


    /**
     * Handles the action event for the "New" menu item.
     *
     * @param actionEvent The action event triggered by the user.
     */
    public void NewAction(ActionEvent actionEvent) {
        // Show the character choose stage
        MainApplication.characterChoose_stage.show();

        // Get the current stage from the event source
        Stage currentStage = (Stage) ((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow();

        // Hide the current stage
        currentStage.hide();

        // Show the character choose stage again
        MainApplication.characterChoose_stage.show();
    }


    /**
     * Handles the action event for the "Save" menu item.
     *
     * @param actionEvent The action event triggered by the user.
     */
    public void SaveAction(ActionEvent actionEvent) {
        if (mVModel.getMaze() == null) {
            Alert prob = new Alert(Alert.AlertType.ERROR);
            prob.setContentText("You Can't Save a Maze Before Generation");
            prob.showAndWait();
        } else {
            File file = getFileFromUser("Save Game", true);
            mVModel.saveMaze(file);
            actionEvent.consume();
        }
    }

    /**
     * Handles the action event for the "Load" menu item.
     *
     * @param actionEvent The action event triggered by the user.
     */
    public void LoadAction(ActionEvent actionEvent) {
        File file = getFileFromUser("Load Game", false);
        mVModel.loadMaze(file);
        mazeDisplay.draw();
        actionEvent.consume();
    }

    /**
     * Handles the action event for the "Properties" menu item.
     *
     * @param actionEvent The action event triggered by the user.
     */
    public void PropertiesAction(ActionEvent actionEvent) {
        // Get the input stream for the config.properties file
        InputStream inputStream = getClass().getResourceAsStream("/config.properties");

        // Check if the input stream is not null (file exists)
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

            // Show the properties stage
            MainApplication.props_stage.show();

            // Get the text area from the properties root
            TextArea textArea = (TextArea) (MainApplication.props_root.lookup("#PropsText"));

            // Set the content of the text area with the file content
            textArea.setText(content.toString());

            // Make the text area non-editable
            textArea.setEditable(false);

            // Hide the main stage
            MainApplication.mainStage.hide();
        }
    }

    /**
     * Handles the action event for the "Exit" menu item.
     */
    public void ExitAction() {
        // Display a confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are You Sure You Want To Exit The Game?");
        Optional<ButtonType> result = alert.showAndWait();

        // Check if the user chose to exit
        if (result.get() == ButtonType.OK) {
            // End the game
            mVModel.endGame();

            // Get the output from the game
            String output = MainApplication.baos.toString();

            // Log the output
            logger.info(output);

            // Exit the application
            System.exit(0);
        }
    }

    /**
     * Handles the key pressed event.
     *
     * @param keyEvent The KeyEvent object representing the key press event.
     */
    public void keyPressed(KeyEvent keyEvent) {
        // Move the player based on the pressed key
        mVModel.movePlayer(keyEvent.getCode());

        // Consume the event to prevent it from being processed further
        keyEvent.consume();
    }

    /**
     * Handles the action when the About menu item is clicked.
     *
     * @param event The ActionEvent object representing the menu item click event.
     * @throws IOException If an I/O error occurs while loading the About view.
     */
    public void AboutAction(ActionEvent event) throws IOException {
        // Retrieve all descText labels in the About view
        Object[] descLabels = MainApplication.about_root.lookupAll(".descText").toArray();

        // Calculate the font size percentage based on the stage width
        double percentage = 0.0215;

        // Iterate through each descText label and bind its font size to the stage width
        for (Object node : descLabels) {
            Label tempLabel = (Label) node;
            tempLabel.styleProperty().addListener((observable, oldValue, newValue) -> {
                if (MainApplication.about_stage.getWidth() > 0) {
                    tempLabel.setStyle("-fx-font-size: " + MainApplication.about_stage.getWidth() * percentage + "px;");
                }
            });
        }

        // Show the About stage
        MainApplication.about_stage.show();

        // Hide the main stage
        MainApplication.mainStage.hide();
    }

    /**
     * Handles the action when the Help menu item is clicked.
     *
     * @param event The ActionEvent object representing the menu item click event.
     * @throws IOException If an I/O error occurs while loading the Help view.
     */
    public void HelpAction(ActionEvent event) throws IOException {
        // Retrieve all descText labels in the Help view
        Object[] descLabels = MainApplication.help_root.lookupAll(".descText").toArray();

        // Calculate the font size percentage based on the stage width
        double percentage = 0.0215;

        // Iterate through each descText label and bind its font size to the stage width
        for (Object node : descLabels) {
            Label tempLabel = (Label) node;
            tempLabel.styleProperty().bind(
                    Bindings.concat("-fx-font-size: ", MainApplication.help_stage.widthProperty().multiply(percentage), "px;")
            );
        }

        // Show the Help stage
        MainApplication.help_stage.show();

        // Hide the main stage
        MainApplication.mainStage.hide();
    }

    /**
     * Handles the action when the Back to Menu button is clicked.
     *
     * @param event The ActionEvent object representing the button click event.
     */
    public void backToMenu(ActionEvent event) {
        // Get the button that triggered the event
        Button button = (Button) event.getSource();

        // Add the "Btn_pressed" style class and remove the "Btn_start" style class
        button.getStyleClass().add("Btn_pressed");
        button.getStyleClass().remove("Btn_start");

        // Create a pause transition to simulate a button press effect
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> {
            // Remove the "Btn_pressed" style class and add the "Btn_start" style class
            button.getStyleClass().remove("Btn_pressed");
            button.getStyleClass().add("Btn_start");

            // Get the current stage from the event source
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Show the main stage
            MainApplication.mainStage.show();

            // Hide the current stage
            currentStage.hide();
        });

        // Start the pause transition
        pause.play();
    }

    /**
     * Handles the action when the user assigns a hero image.
     *
     * @param mouseEvent The MouseEvent object representing the mouse event.
     */
    public void AssingHero(MouseEvent mouseEvent) {
        // Set the anchor pane for maze display
        mazeDisplay.setAnchorPane(anchorPane);

        // Get the assigned hero image
        Hero = ((ImageView) (mouseEvent.getSource())).getImage();

        // Set the hero image in the maze display
        if (mazeDisplay != null) {
            mazeDisplay.setHero(Hero);
            mVModel.generateMaze(rows, cols);
            MainApplication.mazeDisplay_stage.show();
            MainApplication.characterChoose_stage.hide();
            logger.info("Player has chosen a new hero!");
        }
    }

    /**
     * Handles the action when the Start Game button is clicked.
     *
     * @param event The ActionEvent object representing the button click event.
     */
    public void startGame(ActionEvent event) {
        try {
            // Parse the maze rows and columns from the text fields
            rows = Integer.parseInt(textField_mazeRows.getText());
            cols = Integer.parseInt(textField_mazeColumns.getText());
        } catch (NumberFormatException e) {
            // Display an error message for invalid maze sizes
            Alert prob = new Alert(Alert.AlertType.ERROR);
            prob.setContentText("Please Enter Valid Maze Sizes");
            prob.showAndWait();
            return;
        }

        // Get the button that triggered the event
        Button button = (Button) event.getSource();

        // Add the "Btn_pressed" style class and remove the "Btn_start" style class
        button.getStyleClass().add("Btn_pressed");
        button.getStyleClass().remove("Btn_start");

        // Create a pause transition to simulate a button press effect
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> {
            // Remove the "Btn_pressed" style class and add the "Btn_start" style class
            button.getStyleClass().remove("Btn_pressed");
            button.getStyleClass().add("Btn_start");

            // Show the character choose stage
            MainApplication.characterChoose_stage.show();

            // Hide the main stage
            MainApplication.mainStage.hide();
        });

        // Start the pause transition
        pause.play();
    }

    /**
     * Opens a file chooser dialog to select a file from the user.
     *
     * @param title The title of the file chooser dialog.
     * @param save  A boolean indicating whether it's a save dialog or open dialog.
     * @return The selected file.
     */
    private File getFileFromUser(String title, boolean save) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fileChooser.setInitialDirectory(new File(getResourcesFolderPath()));
        File selectedFile;

        if (save)
            selectedFile = fileChooser.showSaveDialog(MainApplication.mainStage);
        else {
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

    /**
     * Returns the path of the resources' folder.
     *
     * @return The path of the resources' folder.
     */
    private String getResourcesFolderPath() {
        File file = new File("resources");
        String resourcesFolderPath = file.getAbsolutePath() + "/savedMazes";
        return resourcesFolderPath;
    }
}