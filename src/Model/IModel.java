package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.util.Observer;

public interface IModel{
    public void generateMaze(int row, int col);
    public Maze getMaze();
    public void updateCharacterLocation(KeyCode direction);
    public void assignObserver(Observer o);
    public void solveMaze();
    public void solve();
    Solution getMazeSolution();
    void saveMaze();
    void loadMaze();
    int getPlayerRow();
    int getPlayerCol();
    void endGame();
}
