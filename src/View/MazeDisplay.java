package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.transform.NonInvertibleTransformException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class MazeDisplay extends Canvas {

    private int playerRow = 0;
    private int playerCol = 0;
    private Maze maze;
    private Solution solution;
    private Image hero;
    private ToggleButton toggleButton;
    private boolean showSol = false;
    private ArrayList<AState> solPath;
    private double zoomFactor = 1.0;
    private double zoomStep = 0.1;
    private double[] currLocation;
    private double cellWidth;
    private double cellHeight;

    private AnchorPane anchorPane;

    public MazeDisplay() {
        currLocation = new double[2];
        initialize();
    }

    private void initialize() {
        anchorPane = (AnchorPane) getParent();

        if (anchorPane != null) {
            anchorPane.widthProperty().addListener((obs, oldWidth, newWidth) -> draw());
            anchorPane.heightProperty().addListener((obs, oldHeight, newHeight) -> draw());
        }

        widthProperty().addListener((obs, oldWidth, newWidth) -> draw());
        heightProperty().addListener((obs, oldHeight, newHeight) -> draw());

        addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.isControlDown()) {
                    double mouseX = event.getX();
                    double mouseY = event.getY();
                    double deltaY = event.getDeltaY();

                    try {
                        if (deltaY > 0) {
                            zoomIn(mouseX, mouseY);
                        } else {
                            zoomOut(mouseX, mouseY);
                        }
                    } catch (NonInvertibleTransformException e) {
                        throw new RuntimeException(e);
                    }

                    event.consume();
                }
            }
        });
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public void Solve() {
        System.out.println("solve");
    }

    public void draw() {
        if (hero == null) {
            // default hero
            Image defHero = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/kosem-removebg-preview.png")));
            hero = defHero;
        }

        if (maze != null) {

            int[][] board = maze.getMaze();
            double canvasHeight = anchorPane.getHeight()*0.91;
            double canvasWidth = anchorPane.getWidth()*0.85;
            setHeight(canvasHeight);
            setWidth(canvasWidth);
            int nRows = maze.getRows();
            int nCols = maze.getColumns();
            cellHeight = canvasHeight / nRows;
            cellWidth = canvasWidth / nCols;
            double w, h;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, anchorPane.getHeight(), anchorPane.getWidth());

            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            // Apply zooming transformation
            graphicsContext.scale(zoomFactor, zoomFactor);

            // Draw frame
            graphicsContext.setStroke(Color.DARKSLATEGRAY);
            graphicsContext.setLineWidth(3.0 / zoomFactor);
            graphicsContext.strokeRect(0, 0, canvasWidth / zoomFactor, canvasHeight / zoomFactor);

            Image monster = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/monster.png")));
            Image gate = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/gate.jpg")));
            Image wallImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/wall.jpg")));

            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < nCols; j++) {
                    h = i * cellHeight;
                    w = j * cellWidth;
                    if (i == nRows - 1 && j == nCols - 1) {
                        graphicsContext.drawImage(gate, w, h, cellWidth, cellHeight);
                        continue;
                    }
                    if (board[i][j] == 1)
                        graphicsContext.drawImage(wallImage, w, h, cellWidth, cellHeight);
                     else if (showSol && !(i == playerRow && j == playerCol)) {
                        AState currState = new MazeState(i, j, 0);
                        if (solPath != null && solPath.contains(currState))
                            graphicsContext.drawImage(monster, w, h, cellWidth, cellHeight);
                    }
                }
            }

            double hPlayer = playerRow * cellHeight;
            double wPlayer = playerCol * cellWidth;
            currLocation[0] = hPlayer;
            currLocation[1] = wPlayer;
            graphicsContext.drawImage(hero, wPlayer, hPlayer, cellWidth, cellHeight);

            // Reset zooming transformation
            graphicsContext.setTransform(1, 0, 0, 1, 0, 0);
        }
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

    public void setSol(boolean toSet) {
        showSol = toSet;
    }

    private void zoomIn(double mouseX, double mouseY) throws NonInvertibleTransformException {
        if (zoomFactor < 3.0) {
            zoomFactor += zoomStep;
            // Adjust the transformation origin based on the mouse position
            GraphicsContext graphicsContext = getGraphicsContext2D();
            Point2D canvasPoint = graphicsContext.getTransform().inverseTransform(mouseX, mouseY);
            graphicsContext.translate(canvasPoint.getX(), canvasPoint.getY());
            graphicsContext.scale(zoomFactor, zoomFactor);
            graphicsContext.translate(-canvasPoint.getX(), -canvasPoint.getY());
            this.setFocused(false);
            draw();
        }
    }

    private void zoomOut(double mouseX, double mouseY) throws NonInvertibleTransformException {
        if (zoomFactor > 1.0) {
            zoomFactor -= zoomStep;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            Point2D canvasPoint = graphicsContext.getTransform().inverseTransform(mouseX, mouseY);
            graphicsContext.translate(canvasPoint.getX(), canvasPoint.getY());
            graphicsContext.scale(zoomFactor, zoomFactor);
            graphicsContext.translate(-canvasPoint.getX(), -canvasPoint.getY());
            this.setFocused(false);

            draw();
        }
    }

    public void setAnchorPane(AnchorPane anchor) {
        this.anchorPane = anchor;
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        draw();
    }

    public double[] getHeroLocation() {
        return currLocation;
    }

    public double getPropX() {
        return cellWidth * zoomFactor;
    }

    public double getPropY() {
        return cellHeight * zoomFactor;
    }
}
