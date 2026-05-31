package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.Direction;
import Model.gamefield.Unit;
import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактная змея — содержит всю общую логику.

 */
public abstract class AbstractSnake extends Unit {

    private static final Direction[] CLOCKWISE = {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    private final List<SnakeSegment> segments = new ArrayList<>();
    private Direction direction;
    private int lives;
    private int stepsAfterEat = 0;
    private final int K;
    private static final int MIN_LENGTH = 3;

    private SnakeListener snakeListener;
    public void setSnakeListener(SnakeListener l) { this.snakeListener = l; }

    public AbstractSnake(int lives, int k) {
        this.lives = Math.max(0, lives);
        this.K     = k;
    }

    @Override
    public boolean canBelongTo(Cell cell) {
        return cell != null;
    }

    public void addSegment(SnakeSegment seg) { segments.add(seg); }



    public void move() {
        if (direction == null) return;

        Cell nextCell = resolveNextCell();
        if (nextCell == null)           { die(); return; }
        if (checkCollision(nextCell))   { die(); return; }

        Cell   oldTailCell  = tailCell();
        Rodent rodentToEat  = rodentAt(nextCell);

        shiftSegments(nextCell);

        if (rodentToEat != null) eat(rodentToEat, oldTailCell);

        incrementSteps();
    }


    protected abstract void shiftSegments(Cell nextCell);



    private Cell resolveNextCell() {
        if (segments.isEmpty()) return null;
        Cell headCell = segments.get(0).owner();
        if (headCell == null) return null;
        return headCell.getNeighbor(direction);
    }

    protected Cell tailCell() {
        return segments.get(segments.size() - 1).owner();
    }

    private Rodent rodentAt(Cell cell) {
        List<Rodent> list = cell.getUnits(Rodent.class);
        return list.isEmpty() ? null : list.get(0);
    }

    private void incrementSteps() {
        stepsAfterEat = Math.min(stepsAfterEat + 1, K);
        if (stepsAfterEat >= K) shrinkStep();
    }

    private boolean checkCollision(Cell nextCell) {
        if (nextCell == null) return true;
        if (!nextCell.getUnits(Rock.class).isEmpty()) return true;
        List<SnakeSegment> body = segments.subList(0, segments.size() - 1);
        for (SnakeSegment seg : body) {
            if (seg.owner() == nextCell) return true;
        }
        return false;
    }

    private void eat(Rodent rodent, Cell oldTailCell) {
        rodent.deactivate();
        Cell growCell = findGrowCell(oldTailCell);
        if (growCell != null) {
            SnakeSegment newSeg = new SnakeSegment();
            growCell.putUnit(newSeg);
            segments.add(newSeg);
        }
        stepsAfterEat = 0;
        if (snakeListener != null) snakeListener.rodentEaten();
    }

    private Cell findGrowCell(Cell oldTailCell) {
        if (oldTailCell != null && oldTailCell.isEmpty()) return oldTailCell;
        Cell tail = tailCell();
        if (tail == null) return null;
        for (Direction d : CLOCKWISE) {
            Cell neighbor = tail.getNeighbor(d);
            if (neighbor != null && neighbor.isEmpty()) return neighbor;
        }
        return null;
    }

    private void shrinkStep() {
        stepsAfterEat = 0;
        if (segments.size() > MIN_LENGTH) removeTail();
        else decreaseLife();
    }

    private void removeTail() {
        SnakeSegment tail = segments.remove(segments.size() - 1);
        tail.deactivate();
    }

    private void decreaseLife() {
        lives = Math.max(0, lives - 1);
        if (lives == 0) die();
    }

    private void die() {
        new ArrayList<>(segments).forEach(SnakeSegment::deactivate);
        segments.clear();
        deactivate();
        if (snakeListener != null) snakeListener.snakeDied();
    }

    public void setDirection(Direction d) {
        if (direction != null && d == direction.opposite()) return;
        this.direction = d;
    }



    public boolean isAlive()                { return isActive() && lives > 0; }
    public int getLives()                   { return lives; }
    public List<SnakeSegment> getSegments() { return segments; }
    public Direction getDirection()         { return direction; }
    public int getStepsAfterEat()           { return stepsAfterEat; }
    public int getK()                       { return K; }
}