package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

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
                //DoSomething?
            }
            mazeDisplay.draw();
        }
    }

    private void generateMaze() {
        try {
            int rows = Integer.parseInt(textField_mazeRows.getText());
            int cols = Integer.parseInt(textField_mazeColumns.getText());
            mVModel.generateMaze(rows, cols);
        } catch (NumberFormatException e) {
            // add a message to the user?
        }
    }

    public void NewAction(ActionEvent actionEvent) {
        // todo: checkBox with maze row / column ?
        generateMaze();
        actionEvent.consume();
    }

    public void SaveAction(ActionEvent actionEvent) {
        // todo : check null case and print something to user ?
        mVModel.saveMaze();
        // todo : print something to the user
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
        // todo : move to exit screen / close the app?
    }

    public void HelpAction(ActionEvent actionEvent)
    {
        // todo : move to help screen or open a data box on screen
    }

    public void AboutAction(ActionEvent actionEvent){
        // todo: what is the difference from help?
    }

}
