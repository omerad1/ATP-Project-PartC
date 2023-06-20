package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observer;

public interface IModel{
    /**
     * Generates a new maze with the specified dimensions.
     * @param row The number of rows for the maze.
     * @param col The number of columns for the maze.
     */
    public void generateMaze(int row, int col);

    /**
     * Retrieves the maze object.
     * @return The maze object.
     */
    public Maze getMaze();

    /**
     * Updates the character location based on the given direction.
     * @param direction The direction in which the character should move.
     */
    public void updateCharacterLocation(KeyCode direction);

    /**
     * Assigns an observer to the model.
     * @param o The observer to be assigned.
     */
    public void assignObserver(Observer o);

    /**
     * Solves the maze.
     */
    public void solveMaze();

    /**
     * Retrieves the solution of the maze.
     * @return The solution of the maze.
     */
    public Solution getMazeSolution();

    /**
     * Saves the current maze to a file.
     * @param file The file to save the maze to.
     */
    public void saveMaze(File file);

    /**
     * Loads a maze from a file.
     * @param file The file to load the maze from.
     */
    public void loadMaze(File file);

    /**
     * Retrieves the current row position of the player.
     * @return The row position of the player.
     */
    public int getPlayerRow();

    /**
     * Retrieves the current column position of the player.
     * @return The column position of the player.
     */
    public int getPlayerCol();

    /**
     * Ends the game and performs necessary cleanup.
     */
    public void endGame();

    /**
     * Restarts the current maze.
     */
    public void restartMaze();
}
