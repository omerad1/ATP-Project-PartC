package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplay extends Canvas {

    int playerRow = 0;
    int playerCol = 0;
    Maze maze;
    Solution solution;
    Image hero;
    ToggleButton toggleButton;
    boolean showSol = false;
    ArrayList<AState> solPath;


    public MazeDisplay() {
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }
    public void Solve(){
        System.out.println("solve");
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




                //add pic
            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            // Draw frame
            graphicsContext.setStroke(Color.DARKGOLDENROD);
            graphicsContext.setLineWidth(4.0);
            graphicsContext.strokeRect(0, 0, canvasWidth, canvasHeight);

            Image wallImage = null;
            Image gate = null;
            Image monster = null;
            try {
                monster = new Image(new FileInputStream("C:\\Users\\omera\\OneDrive\\שולחן העבודה\\לימודים\\נושאים מתקדמים בתכנות\\ATP-Project-PartC\\ATP-Project-PartC\\src\\imgs\\monster.png"));

                gate = new Image(new FileInputStream("C:\\Users\\omera\\OneDrive\\שולחן העבודה\\לימודים\\נושאים מתקדמים בתכנות\\ATP-Project-PartC\\ATP-Project-PartC\\src\\imgs\\gate.jpg"));
                wallImage = new Image(new FileInputStream("C:\\Users\\omera\\OneDrive\\שולחן העבודה\\לימודים\\נושאים מתקדמים בתכנות\\ATP-Project-PartC\\ATP-Project-PartC\\src\\imgs\\wall.jpg"));
            } catch (FileNotFoundException e) {
                System.out.println();
            }


            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < nCols; j++) {
                    h = i * cellHeight;
                    w = j * cellWidth;
                    if(i==nRows-1 && j==nCols-1){
                        graphicsContext.drawImage(gate, w, h, cellWidth, cellHeight);
                        continue;
                    }
                    if (board[i][j] == 1) {
                        if (wallImage == null) {
                            graphicsContext.fillRect(w, h, cellWidth, cellHeight);
                        } else {
                            graphicsContext.drawImage(wallImage, w, h, cellWidth, cellHeight);
                        }
                    }
                    else if(showSol && !(i == playerRow  && j == playerCol)){
                        AState currState = new MazeState(i, j, 0);
                        if(solPath.contains(currState))
                            graphicsContext.drawImage(monster, w, h, cellWidth, cellHeight);
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
        solPath = sol.getSolutionPath();
    }
    public void setSol(boolean toSet){
        showSol = toSet;
    }
}
