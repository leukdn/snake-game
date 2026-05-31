package Model.gamefield;

import Model.GameListener;
import Model.UnitListener;
import Model.units.AbstractSnake;
import Model.units.SnakeListener;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final GameField gameField;
    private final Spawner   spawner;
    private AbstractSnake   snake; // ← AbstractSnake
    private boolean isGameOver = false;
    private int score = 0;

    private final List<GameListener> viewListeners = new ArrayList<>();

    private final SnakeListener snakeListener = new SnakeListener() {
        @Override public void rodentEaten() {
            score++;
            spawner.spawnRodent();
            notifyScore();
            notifyLives();
        }
        @Override public void snakeDied() { finishGame(); }
    };

    private final UnitListener internalListener = unit ->
            viewListeners.forEach(l -> l.activateChanged(unit));

    public Game(GameField gameField, Spawner spawner) {
        this.gameField = gameField;
        this.spawner   = spawner;
        this.spawner.setGameListener(internalListener);
    }

    public void start() {
        score      = 0;
        isGameOver = false;
        gameField.deactivate();
        spawner.start();

        snake = gameField.getSnake();
        if (snake != null) {
            snake.addUnitListener(internalListener);
            snake.setSnakeListener(snakeListener);
            notifyLives();
        }
    }

    public void update() {
        if (isGameOver || snake == null) return;
        snake.move();
        viewListeners.forEach(GameListener::fieldChanged);
        notifySteps();
    }

    public void setDirection(Direction d) {
        if (snake != null) snake.setDirection(d);
    }

    private void finishGame() {
        if (isGameOver) return;
        isGameOver = true;
        gameField.deactivate();
        viewListeners.forEach(l -> l.gameIsOver(false));
    }

    private void notifyScore() { viewListeners.forEach(l -> l.scoreChanged(score)); }
    private void notifyLives() {
        if (snake != null)
            viewListeners.forEach(l -> l.livesChanged(snake.getLives()));
    }
    private void notifySteps() {
        if (snake != null)
            viewListeners.forEach(l -> l.stepsChanged(
                    snake.getStepsAfterEat(), snake.getK()));
    }

    public void addViewListener(GameListener l) { viewListeners.add(l); }
    public boolean isOver()                     { return isGameOver; }
    public GameField getGameField()             { return gameField; }
    public int getScore()                       { return score; }
}