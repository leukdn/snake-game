package Model.gamefield;

import Model.UnitListener;


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

    public abstract Spawner placeRocks();
    public abstract Spawner placeRodents(int count);
    public abstract Spawner placeSnake();

    /**
     * Спавн одного грызуна (после съедания)
     */
    protected abstract void spawnRodent();


}