package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.IOException;
import java.util.Observer;

public interface IModel{
    public void generateMaze(int row, int col);
    public Maze getMaze();
    public void updateCharacterLocation(KeyCode direction);
    public void assignObserver(Observer o);
    public void solveMaze();
    public void solve();
    Solution getMazeSolution();
    void saveMaze(File file);
    void loadMaze(File file);
    int getPlayerRow();
    int getPlayerCol();

    void endGame();
    void restartMaze();

    void setMazeSolvingAlgorithm(String algo) throws IOException;

    void setMazeGeneratingAlgorithmAlgorithm(String algo) throws IOException;
}
