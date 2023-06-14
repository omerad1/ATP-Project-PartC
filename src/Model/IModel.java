package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel{
    public void generateMaze(int row, int col);
    public Maze getMaze();
    public void updateCharacterLocation(int direction);
    public void assignObserver(Observer o);
    public void solveMaze();
    Solution getMazeSolution();
    void saveMaze();
    void loadMaze();
    int getPlayerRow();
    int getPlayerCol();
}
