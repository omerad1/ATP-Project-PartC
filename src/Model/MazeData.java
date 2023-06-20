package Model;

import java.io.Serializable;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

/**
 * Represents the data associated with a maze, including the maze itself, player position, and solution.
 */
public class MazeData implements Serializable {
    private  Maze maze; // The maze object
    private  int playerRow; // The row index of the player's position
    private  int playerCol; // The column index of the player's position
    private  Solution solution; // The solution to the maze

    /**
     * Constructs a MazeData object with the specified maze, player position, and solution.
     *
     * @param maze       The maze object.
     * @param playerRow  The row index of the player's position.
     * @param playerCol  The column index of the player's position.
     * @param solution   The solution to the maze.
     */
    public MazeData(Maze maze, int playerRow, int playerCol, Solution solution) {
        this.maze = maze;
        this.playerRow = playerRow;
        this.playerCol = playerCol;
        this.solution = solution;
    }

    /**
     * Returns the maze object.
     *
     * @return The maze object.
     */
    public Maze getMaze() {
        return maze;
    }

    /**
     * Returns the row index of the player's position.
     *
     * @return The row index of the player's position.
     */
    public int getPlayerRow() {
        return playerRow;
    }

    /**
     * Returns the column index of the player's position.
     *
     * @return The column index of the player's position.
     */
    public int getPlayerCol() {
        return playerCol;
    }

    /**
     * Returns the solution to the maze.
     *
     * @return The solution to the maze.
     */
    public Solution getSolution() {
        return solution;
    }
}
