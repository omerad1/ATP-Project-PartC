package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observable;
import java.util.Observer;
import Server.*;
import Client.*;
import IO.MyDecompressorInputStream;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import  org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;


public class MyModel extends Observable implements IModel{
    private Maze maze;
    private int playerRow;
    private int playerCol;
    private Solution mazeSol;
    private final Server mazeGenerationServer;
    private final Server mazeSolverServer;
    private boolean reachedEnd;
    private Logger logger = LogManager.getLogger();


    public MyModel()
    {
        mazeGenerationServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        mazeSolverServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGenerationServer.start();
        mazeSolverServer.start();
        reachedEnd = false;
        Configurator.setRootLevel(Level.DEBUG);
    }
    @Override
    public Maze getMaze() {
        return this.maze;
    }

    @Override
    public int getPlayerRow() {
        return this.playerRow;
    }

    @Override
    public int getPlayerCol() {
        return this.playerCol;
    }

    @Override
    public Solution getMazeSolution() {
        return this.mazeSol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void generateMaze(int row, int col) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row, col};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed withMyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[row*col+24]; //allocating byte[] for the decompressed maze
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        // initialize needed components
                        maze = new Maze(decompressedMaze);
                        logger.info("User has created a new maze of sizes : " + row + " " + col);
                        playerRow = maze.getStartPosition().getRow_index();
                        playerCol = maze.getStartPosition().getColumn_index();
                        reachedEnd = false;
                        setChanged();
                        solveMaze();
                        notifyObservers("UpdateMaze, UpdatePlayerPosition, UpdateSolution");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.error("Couldn't handle client at port 5400");
        }
    }
    @Override
    public void solveMaze() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        mazeSol = (Solution) fromServer.readObject();
                        logger.info("a maze was solved of sizes : " + maze.getRows() + " " + maze.getColumns());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.error("Couldn't handle client at port 5401");
        }
    }

    @Override
    public void updateCharacterLocation(KeyCode direction) {
        if(maze == null || reachedEnd)
            return;
        int tempRow = this.playerRow;
        int tempCol = this.playerCol;
        switch (direction) {
            case NUMPAD1, END -> moveDiagonal(1, -1);
            case NUMPAD2, DOWN -> moveChar(1, 0);
            case NUMPAD3, PAGE_DOWN -> moveDiagonal(1, 1);
            case NUMPAD4, LEFT -> moveChar(0, -1);
            case NUMPAD6, RIGHT -> moveChar(0, 1);
            case NUMPAD7, HOME -> moveDiagonal(-1, -1);
            case NUMPAD8, UP -> moveChar(-1, 0);
            case NUMPAD9, PAGE_UP -> moveDiagonal(-1, 1);
        }
        if (tempRow != this.playerRow || tempCol != this.playerCol){
            setChanged();
            if(playerRow == maze.getGoalPosition().getRow_index() && playerCol == maze.getGoalPosition().getColumn_index()) {
                reachedEnd = true;
                logger.info("Player found the maze solution");
                notifyObservers("UpdatePlayerPosition, FoundGoal");
            }
            else
            {
                logger.info("Player moved to: " + tempRow + "," + tempCol);
                notifyObservers("UpdatePlayerPosition");
            }
        }
    }

    public void moveDiagonal(int rowMove, int colMove)
    {
        int [][] mazeMat = maze.getMaze();
        if(mazeMat[playerRow + rowMove][playerCol + colMove] == 0)
            if(mazeMat[playerRow+rowMove][playerCol] == 0 || mazeMat[playerRow][playerCol+colMove] == 0)
                moveChar(rowMove,colMove);

    }

    public void moveChar(int rowMove, int colMove)
    {
        int tempRow = playerRow + rowMove;
        int tempCol = playerCol + colMove;
        if(tempRow < maze.getRows() && tempRow >= 0 && tempCol < maze.getColumns() && tempCol >= 0 && maze.getMaze()[tempRow][tempCol] == 0) {
            playerRow = tempRow;
            playerCol = tempCol;
        }

    }

    @Override
    public void saveMaze(File file) {
        if (file == null) return;
        boolean success;
        try (ObjectOutputStream mazeSaver = new ObjectOutputStream(new FileOutputStream(file))) {
            MazeData mazeData = new MazeData(maze, playerRow, playerCol, mazeSol);
            mazeSaver.writeObject(mazeData);
            success = true;
            logger.info("Player saved the maze");
        } catch (IOException e) {
            success = false;
            logger.error("Player was unable to save the maze");
        }
        showAlert(success ? "Game Saved!" : "Could not save game :(\nPlease try again!", success);
    }

    @Override
    public void loadMaze(File file) {
        String msg = "";
        if (file == null) return;
        if (maze == null)
            msg = "loadFromStart";
        try (ObjectInputStream gameLoader = new ObjectInputStream(new FileInputStream(file))) {
            MazeData mazeData = (MazeData) gameLoader.readObject();
            maze = mazeData.getMaze();
            playerRow = mazeData.getPlayerRow();
            playerCol = mazeData.getPlayerCol();
            mazeSol = mazeData.getSolution();
            setChanged();
            logger.info("Player loaded a maze of sizes: " + maze.getRows() + "," + maze.getColumns());
            notifyObservers("UpdateMaze, UpdatePlayerPosition, UpdateSolution" + ", "+ msg);
        } catch (Exception e) {
            logger.error("Player couldn't load the maze");
            showAlert("Could not load game :(\nPlease try again!", false);
        }
    }

    private void showAlert(String message, boolean success) {
        Alert status = new Alert(success ? Alert.AlertType.CONFIRMATION : Alert.AlertType.ERROR);
        status.setContentText(message);
        status.showAndWait();
    }

    @Override
    public void endGame() {
        logger.info("Player has ended the game");
        mazeSolverServer.stop();
        mazeGenerationServer.stop();
    }

    @Override
    public void restartMaze() {
        if (maze==null)
            return;

        playerRow = maze.getStartPosition().getRow_index();
        playerCol = maze.getStartPosition().getColumn_index();
        reachedEnd = false;

        setChanged();
        notifyObservers("UpdatePlayerPosition");
    }

}
