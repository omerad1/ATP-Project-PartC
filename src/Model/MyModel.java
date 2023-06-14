package Model;

import View.TestView;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import Server.*;
import Client.*;
import IO.MyDecompressorInputStream;
import javafx.scene.control.Alert;
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


    public MyModel()
    {
        mazeGenerationServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        mazeSolverServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGenerationServer.start();
        mazeSolverServer.start();
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
                                setChanged();
                                notifyObservers("UpdateSolution");
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
    public void updateCharacterLocation(int direction) {

    }


    @Override
    public void saveMaze() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Game");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Game Files", "*.maze"));
        fc.setInitialDirectory(new File(System.getProperty("user.dir")));

        File file = fc.showSaveDialog(TestView.myStage);
        if (file != null) {
            boolean success = false;
            try (ObjectOutputStream mazeSaver = new ObjectOutputStream(new FileOutputStream(file))) {
                mazeSaver.writeObject(maze);
                success = true;
            } catch (IOException e) {
                success = false;
            } finally {
                showSaveStatus(success);
            }
        }
    }

    private void showSaveStatus(boolean success) {
        Alert status = new Alert(success ? Alert.AlertType.CONFIRMATION : Alert.AlertType.ERROR);
        status.setContentText(success ? "Game Saved!" : "Could not save game :(\nPlease try again!");
        status.showAndWait();
    }

    @Override
    public void loadMaze() {

    }


}
