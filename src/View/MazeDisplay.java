package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;

public class MazeDisplay extends Canvas {

    int playerRow;
    int playerCol;
    Maze maze;
    Solution solution;

    public MazeDisplay()
    {
        playerRow=0;
        playerCol=0;
        solution = null;
    }

    public void setMaze(Maze maze)
    {
        this.maze = maze;
    }

    public GraphicsContext draw(){
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int[][] board = maze.getMaze();
            int nRows = maze.getRows();
            int nCols = maze.getColumns();
            double cellHeight = canvasHeight / nRows;
            double cellWidth = canvasWidth / nCols;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            double w, h;
            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < nCols; j++) {
                    //todo : choose pics for maze walls and non walls
                    if (board[i][j] == 1) {
                        h = i * cellHeight;
                        w = j * cellWidth;
                        graphicsContext.fillRect(w, h, cellWidth, cellHeight);
                    } else {
                        // todo : draw something
                    }
                }
            }
            // todo : draw player?
            return graphicsContext;
        }
        return null;
    }

    public void setPlayerPos(int playerRow, int playerCol)
    {
        this.playerRow = playerRow;
        this.playerCol = playerCol;
    }

    public void setMazeSolution(Solution sol)
    {
        this.solution = sol;
    }

}
