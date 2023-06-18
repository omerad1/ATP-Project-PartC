package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplay extends Canvas {

    int playerRow = 0;
    int playerCol = 0;
    Maze maze;
    Solution solution;
    Image hero;

    public MazeDisplay() {
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public void draw() {
        if (maze != null) {
            int[][] board = maze.getMaze();
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int nRows = maze.getRows();
            int nCols = maze.getColumns();
            double cellHeight = canvasHeight / nRows;
            double cellWidth = canvasWidth / nCols;
            double w, h;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            Image wallImage = null;
            try {
                wallImage = new Image(new FileInputStream("C:\\Users\\omera\\OneDrive\\שולחן העבודה\\לימודים\\נושאים מתקדמים בתכנות\\ATP-Project-PartC\\ATP-Project-PartC\\src\\imgs\\wall.jpg"));
            } catch (FileNotFoundException e) {
                System.out.println();
            }

            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < nCols; j++) {
                    if (board[i][j] == 1) {
                        h = i * cellHeight;
                        w = j * cellWidth;
                        if (wallImage == null) {
                            graphicsContext.fillRect(w, h, cellWidth, cellHeight);
                        } else {
                            graphicsContext.drawImage(wallImage, w, h, cellWidth, cellHeight);
                        }
                    }
                }
            }

            double hPlayer = playerRow * cellHeight;
            double wPlayer = playerCol * cellWidth;
            graphicsContext.drawImage(hero, wPlayer, hPlayer, cellWidth, cellHeight);
        }
    }
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        draw();
    }
    public void setHero(Image hero) {
        this.hero = hero;
    }

    public void setPlayerPos(int playerRow, int playerCol) {
        this.playerRow = playerRow;
        this.playerCol = playerCol;
    }

    public void setMazeSolution(Solution sol) {
        this.solution = sol;
    }
}
