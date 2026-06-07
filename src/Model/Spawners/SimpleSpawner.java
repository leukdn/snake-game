package Model.Spawners;

import Model.gamefield.*;
import Model.units.*;

import java.util.*;

public class SimpleSpawner extends Spawner {

    private static final int ROCK_COUNT = 5;
    private static final int K          = 20;

    private final Random       random  = new Random();
    private final SnakeFactory factory;

    public SimpleSpawner(GameField gameField) {
        this(gameField, new NormalSnakeFactory());
    }

    public SimpleSpawner(GameField gameField, SnakeFactory factory) {
        super(gameField);
        this.factory = factory;
    }

    @Override protected Spawner placeRocks() {
        List<Cell> free = getFreeCells();
        Collections.shuffle(free, random);
        for (int i = 0; i < ROCK_COUNT && i < free.size(); i++)
            free.get(i).putUnit(new Rock());
        return this;
    }

    @Override protected Spawner placeRodents(int count) {
        for (int i = 0; i < count; i++) spawnRodent();
        return this;
    }

    @Override public void spawnRodent() {
        List<Cell> free = getFreeCells();
        if (free.isEmpty()) return;
        Cell target = free.get(random.nextInt(free.size()));
        Rodent r = new Rodent();
        if (gameListener != null) r.addUnitListener(gameListener);
        target.putUnit(r);
    }

    @Override public Spawner placeSnake() {
        List<Cell> safe = getSafeCells(3);
        Collections.shuffle(safe, random);
        for (Cell start : safe) {
            List<Cell> three = findThreeInLine(start);
            if (three != null) {
                AbstractSnake snake = createSnake(); // ← AbstractSnake
                placeSegments(snake, three);
                snake.setDirection(Direction.EAST);
                snake.activate();
                gameField.setSnake(snake);
                return this;
            }
        }
        return this;
    }

    private AbstractSnake createSnake() {
        int n = gameField.getWidth(), m = gameField.getHeight();
        int sum = n + m;
        int livesMin = sum / 2;
        int livesMax = (int)(sum / 1.5);
        int lives = livesMin + (livesMax > livesMin
                ? random.nextInt(livesMax - livesMin + 1) : 0);
        AbstractSnake snake = factory.createSnake(lives, K);
        if (gameListener != null) snake.addUnitListener(gameListener);
        return snake;
    }

    private void placeSegments(AbstractSnake snake, List<Cell> cells) {
        for (Cell c : cells) {
            SnakeSegment seg = new SnakeSegment();
            c.putUnit(seg);
            snake.addSegment(seg);
        }
    }

    private List<Cell> getSafeCells(int margin) {
        List<Cell> safe = new ArrayList<>();
        int w = gameField.getWidth(), h = gameField.getHeight();
        for (int y = margin; y < h - margin; y++)
            for (int x = margin; x < w - margin; x++) {
                Cell c = gameField.getCell(x, y);
                if (c != null && c.isEmpty()) safe.add(c);
            }
        return safe.isEmpty() ? getFreeCells() : safe;
    }

    private List<Cell> findThreeInLine(Cell start) {
        for (Direction d : Direction.values()) {
            List<Cell> line = new ArrayList<>();
            Cell cur = start;
            for (int i = 0; i < 3; i++) {
                if (cur == null || !cur.isEmpty()) break;
                line.add(cur); cur = cur.getNeighbor(d);
            }
            if (line.size() == 3) return line;
        }
        return null;
    }


}