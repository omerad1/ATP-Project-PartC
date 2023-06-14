package View;

import ViewModel.MyViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;


import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements IView, Observer {

    private MyViewModel mVModel;
    @FXML
    private TextField textField_mazeRows;
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

    public void generateMaze() {
        try {
            int rows = Integer.parseInt(textField_mazeRows.getText());
            int cols = Integer.parseInt(textField_mazeColumns.getText());
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

    public void PropertiesAction(ActionEvent actionEvent)
    {
        // todo : die
    }

    public void ExitAction(ActionEvent actionEvent)
    {
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

    public void HelpAction(ActionEvent actionEvent)
    {
        // todo : move to help screen or open a data box on screen
    }

    public void AboutAction(ActionEvent actionEvent){
        // todo: what is the difference from help?
    }

    public void keyPressed(KeyEvent keyEvent) {
        mVModel.movePlayer(keyEvent.getCode());
        keyEvent.consume();
    }

}
