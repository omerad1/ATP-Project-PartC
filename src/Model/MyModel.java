package Model;

import View.TestView;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observable;
import java.util.Observer;
import Server.*;
import Client.*;
import IO.MyDecompressorInputStream;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class MyModel extends Observable implements IModel{
    private Maze maze;
    private int playerRow;
    private int playerCol;
    private Solution mazeSol;
    private Server mazeGenerationServer;
    private Server mazeSolverServer;
    private boolean reachedEnd;


    public MyModel()
    {
        mazeGenerationServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        mazeSolverServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGenerationServer.start();
        mazeSolverServer.start();
        reachedEnd = false;
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
                        Maze maze = new Maze(decompressedMaze);
                        playerRow = maze.getStartPosition().getRow_index();
                        playerCol = maze.getStartPosition().getColumn_index();
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
        }
    }

    public void solve()
    {
        if(maze == null);
            //todo: throw some error?
        else {
            solveMaze();
            setChanged();
            notifyObservers("UpdateSolution");
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCharacterLocation(KeyCode direction) {
        if(maze == null)
            return;
        int tempRow = this.playerRow;
        int tempCol = this.playerCol;
        switch (direction) {
            case NUMPAD1 -> moveChar(1, -1);
            case NUMPAD2 -> moveChar(1, 0);
            case NUMPAD3 -> moveChar(1, 1);
            case NUMPAD4 -> moveChar(0, -1);
            case NUMPAD6 -> moveChar(0, 1);
            case NUMPAD7 -> moveChar(-1, -1);
            case NUMPAD8 -> moveChar(-1, 0);
            case NUMPAD9 -> moveChar(-1, 1);
        }
        if (tempRow != this.playerRow || tempCol != this.playerCol){
            setChanged();
            if(playerRow == maze.getGoalPosition().getRow_index() && playerCol == maze.getGoalPosition().getColumn_index())
                notifyObservers("UpdatePlayerPosition, FoundGoal");
            else notifyObservers("UpdatePlayerPosition");
        }
    }

    public void moveChar(int rowMove, int colMove)
    {
        int tempRow = playerRow + rowMove;
        int tempCol = playerCol + colMove;
        if(tempRow < maze.getRows() && tempRow > 0 && tempCol < maze.getColumns() && tempCol > 0 && maze.getMaze()[tempRow][tempCol] == 0) {
            playerRow = tempRow;
            playerCol = tempCol;
        }
    }


    @Override
    public void saveMaze() {
        File file = getFileFromUser("Save Game");
        if (file == null) return;
        boolean success;
        try (ObjectOutputStream mazeSaver = new ObjectOutputStream(new FileOutputStream(file))) {
            MazeData mazeData = new MazeData(maze, playerRow, playerCol, mazeSol);
            mazeSaver.writeObject(mazeData);
            success = true;
        } catch (IOException e) {
            success = false;
        }
        showAlert(success ? "Game Saved!" : "Could not save game :(\nPlease try again!", success);
    }

    @Override
    public void loadMaze() {
        File file = getFileFromUser("Load Game");
        if (file == null) return;
        try (ObjectInputStream gameLoader = new ObjectInputStream(new FileInputStream(file))) {
            MazeData mazeData = (MazeData) gameLoader.readObject();
            maze = mazeData.getMaze();
            playerRow = mazeData.getPlayerRow();
            playerCol = mazeData.getPlayerCol();
            mazeSol = mazeData.getSolution();
            setChanged();
            notifyObservers("mazeDisplay, solutionDisplay, playerDisplay");
        } catch (Exception e) {
            showAlert("Could not load game :(\nPlease try again!", false);
        }
    }

    private File getFileFromUser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fileChooser.setInitialDirectory(new File(System.getProperty("./resources")));
        return fileChooser.showSaveDialog(TestView.myStage);
    }

    private void showAlert(String message, boolean success) {
        Alert status = new Alert(success ? Alert.AlertType.CONFIRMATION : Alert.AlertType.ERROR);
        status.setContentText(message);
        status.showAndWait();
    }

    @Override
    public void endGame() {
        mazeSolverServer.stop();
        mazeGenerationServer.stop();
    }
}
