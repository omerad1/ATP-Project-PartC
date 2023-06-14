package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.util.Observable;

import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private final IModel model;
    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
    }
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public void solveMaze(){
        model.solve();
    }

    public Solution getMazeSolution() {
        return model.getMazeSolution();
    }

    public void generateMaze(int rows, int cols) {
        model.generateMaze(rows, cols);
    }

    public void saveMaze() {
        model.saveMaze();
    }

    public void loadMaze() {
        model.loadMaze();
    }

    public int getPlayerRow() {
        return model.getPlayerRow();
    }

    public int getPlayerCol() {
        return model.getPlayerCol();
    }

    public void movePlayer(KeyCode keyEvent) {
        model.updateCharacterLocation(keyEvent);
    }
}
