package Model.gamefield;

import Model.UnitListener;

import java.util.ArrayList;
import java.util.List;


public abstract class Spawner {
    private static final int RODENT_COUNT = 3;

    protected final GameField gameField;
    protected UnitListener gameListener;

    public Spawner(GameField gameField) {
        this.gameField = gameField;
    }

    public void setGameListener(UnitListener listener) {
        this.gameListener = listener;
    }


    public void start() {
        placeRocks().placeRodents(RODENT_COUNT).placeSnake();
    }

    protected abstract Spawner placeRocks();
    protected abstract Spawner placeRodents(int count);
    public abstract Spawner placeSnake();

    /**
     * Спавн одного грызуна (после съедания)
     */
    protected abstract void spawnRodent();

    protected List<Cell> getFreeCells() {
        List<Cell> result = new ArrayList<>();
        for (Cell c : gameField) if (c.isEmpty()) result.add(c);
        return result;
    }

}