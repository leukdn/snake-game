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


    @Override
    protected void shiftSegments(Cell nextCell) {
        List<SnakeSegment> segments = getSegments();
        Direction dir = getDirection();


        Cell wiggledNext = wiggleNext(nextCell, dir);

        // каждый сег на место предыдущего
        List<Cell> targets = new ArrayList<>();
        targets.add(wiggledNext);
        for (int i = 0; i < segments.size() - 1; i++) {
            targets.add(segments.get(i).owner());
        }

        // Снять все сегменты
        for (SnakeSegment seg : segments) {
            Cell c = seg.owner();
            if (c != null) c.extractUnit(seg);
        }

        // Расставить по целям
        for (int i = 0; i < segments.size(); i++) {
            Cell target = targets.get(i);
            if (target != null) segments.get(i).moveTo(target);
        }

        phase = 1 - phase;
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