package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private final IModel model;

    /**
     * Constructs a MyViewModel object with the specified model.
     * Registers itself as an observer to the model.
     *
     * @param model The underlying model.
     */
    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    /**
     * Retrieves the maze from the model.
     *
     * @return The maze.
     */
    public Maze getMaze() {
        return model.getMaze();
    }

    /**
     * Retrieves the solution for the maze from the model.
     *
     * @return The solution.
     */
    public Solution getMazeSolution() {
        return model.getMazeSolution();
    }

    /**
     * Generates a new maze with the specified number of rows and columns.
     *
     * @param rows The number of rows for the maze.
     * @param cols The number of columns for the maze.
     */
    public void generateMaze(int rows, int cols) {
        model.generateMaze(rows, cols);
    }

    /**
     * Saves the current maze to a file.
     *
     * @param file The file to save the maze to.
     */
    public void saveMaze(File file) {
        model.saveMaze(file);
    }

    /**
     * Loads a maze from a file.
     *
     * @param file The file to load the maze from.
     */
    public void loadMaze(File file) {
        model.loadMaze(file);
    }

    /**
     * Retrieves the current row position of the player in the maze.
     *
     * @return The player's row position.
     */
    public int getPlayerRow() {
        return model.getPlayerRow();
    }

    /**
     * Retrieves the current column position of the player in the maze.
     *
     * @return The player's column position.
     */
    public int getPlayerCol() {
        return model.getPlayerCol();
    }

    /**
     * Updates the position of the player in the maze based on the specified key event.
     *
     * @param keyEvent The key event representing the movement direction.
     */
    public void movePlayer(KeyCode keyEvent) {
        model.updateCharacterLocation(keyEvent);
    }

    /**
     * Signals the end of the game and performs any necessary cleanup.
     */
    public void endGame() {
        model.endGame();
    }
}
