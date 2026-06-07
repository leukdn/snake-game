package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.Direction;
import java.util.ArrayList;
import java.util.List;

/**
 * Волнообразная змея.

 */
public class SineSnake extends AbstractSnake {

    private int phase = 0;

    public SineSnake(int lives, int k) {
        super(lives, k);
    }


    /**
     * Строит цели: голова с боковым смещением,
     * остальные — стандартно каждый на место предыдущего.
     */
    @Override
    protected void shiftSegments(Cell nextCell) {
        List<SnakeSegment> segments = getSegments();
        Direction dir = getDirection();

        // Голова идёт в виляющую ячейку
        Cell wiggledNext = wiggleNext(nextCell, dir);

        List<Cell> targets = new ArrayList<>();
        targets.add(wiggledNext);
        for (int i = 0; i < segments.size() - 1; i++) {
            targets.add(segments.get(i).owner());
        }

        applyTargets(targets);

        phase = 1 - phase; // переключаем сторону для следующего шага
    }


    private Cell wiggleNext(Cell nextCell, Direction dir) {
        if (nextCell == null || dir == null) return nextCell;

        Direction side = (phase == 0) ? dir.left() : dir.right();
        Cell sideCell  = nextCell.getNeighbor(side);

        if (isAvailable(sideCell)) {
            return sideCell;
        }
        return nextCell;
    }

    private boolean isAvailable(Cell cell) {
        if (cell == null) return false;
        if (!cell.isEmpty(Rock.class)) return false;
        if (!cell.isEmpty(SnakeSegment.class)) return false;
        return true;
    }
}