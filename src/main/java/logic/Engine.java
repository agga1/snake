package logic;

import javafx.scene.input.KeyCode;
import levels.Levels;
import objects.IMapElement;
import objects.Map;
import objects.Obstacle;
import objects.Snake;
import utils.Direction;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class Engine implements MapObserver{
    private Snake snake;
    private Map map;
    private List<Observer> observers = new ArrayList<>();
    private boolean paused = false;
    private int progress = 0;
    private int currLvl = 1;
    private int speed = 30;

    private int width = 20;
    private int height = 20;

    public Engine(int width, int height) {
        map = new Map(width, height, this);
        snake = new Snake(map);
    }
    public void initialize(){
        paused = true;
        map.placeElement(snake);
        Obstacle obstacle = Levels.getInstance().getLevel(currLvl);
        map.placeElement(obstacle);
        map.onGrowApple();
        map.getRect().toVectors().forEach(v -> onTileUpdate(map.objectAt(v), v));
    }

    public void update(){
        if(!paused)
            snake.move();
    }

    public void onKeyPressed(KeyCode k){
        if(k.equals(KeyCode.P))
            paused = !paused;
        else {
            if(paused) paused = false;
            Direction direction = Direction.keyKodeToDir(k);
            if (direction != null)
                snake.changeDirection(direction);
        }
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void onKill() {
        currLvl = 1;
        progress = 0;
        onLevelUpdate(currLvl);
        onScoreUpdate(progress);
        map = new Map(width, height, this);
        snake = new Snake(map);
        initialize();

    }

    @Override
    public void onProgress() {
        progress++;
        onScoreUpdate(progress);
        if(progress > currLvl*currLvl + 1){
            currLvl ++;
            onNewLevel();
        }
    }

    public void onNewLevel(){
        onLevelUpdate(currLvl);
        map = new Map(width, height, this);
        snake.changeMap(map); // reset snake
        initialize();
    }

    @Override
    public void onTileUpdate(IMapElement mapElement, Vector2d position) {
        this.observers.forEach(ob -> ob.onTileUpdate(mapElement, position));
    }

    public void onScoreUpdate(int score) {
        this.observers.forEach(ob -> ob.onScoreUpdate(score));
    }

    public void onLevelUpdate(int lvl) {
        this.observers.forEach(ob -> ob.onLevelUpdate(lvl));
    }

    public int getSpeed() {
        return speed;
    }

}
