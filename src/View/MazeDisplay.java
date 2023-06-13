package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.awt.*;

public class MazeDisplay extends Canvas {

    MyMazeGenerator myMaze = new MyMazeGenerator();
    Maze maze = myMaze.generate(10,10);
    int[][] board = maze.getMaze();
    int rows = maze.getRows();
    int cols = maze.getColumns();



    public void draw(){
        double canvasHeight = getHeight();
        double canvasWidth =getWidth();
        double cellHeight = canvasHeight/rows;
        double cellWidth = canvasWidth/cols;
        GraphicsContext graphicsContext = getGraphicsContext2D(); //todo: tommi !!!!!!
        graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
        graphicsContext.setFill(Color.color(10,101,312));
        double w,h;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(board[i][j] == 1){
                    h = i*cellHeight;
                    w = j*cellWidth;
                    graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                }

            }

        }

    }
}
