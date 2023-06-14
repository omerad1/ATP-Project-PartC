package Model;

import java.io.Serializable;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

public class MazeData implements Serializable {
    private final Maze maze;
    private final int playerRow;
    private final int playerCol;
    private final Solution solution;

    public MazeData(Maze maze, int playerRow, int playerCol, Solution solution) {
        this.maze = maze;
        this.playerRow = playerRow;
        this.playerCol = playerCol;
        this.solution = solution;
    }

    public Maze getMaze() {
        return maze;
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public Solution getSolution() {
        return solution;
    }
}
