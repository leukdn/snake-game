package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.Direction;
import java.util.ArrayList;
import java.util.List;

/**
 * Зигзагообразная змея.

 */
public class ZigZagSnake extends AbstractSnake {

    public ZigZagSnake(int lives, int k) {
        super(lives, k);
    }

    /**
     * Строит зигзаг-цели и передаёт в applyTargets()
     * Голова прямо. Сегменты тела смещаются группами по 2
     */
    @Override
    protected void shiftSegments(Cell nextCell) {
        List<Cell> targets = buildZigzagTargets(nextCell, getSegments(), getDirection());
        applyTargets(targets);
    }


    private List<Cell> buildZigzagTargets(
            Cell nextCell,
            List<SnakeSegment> segments,
            Direction dir) {

        List<Cell> targets = new ArrayList<>();
        targets.add(nextCell); // голова

        for (int i = 0; i < segments.size() - 1; i++) {
            Cell baseCell = segments.get(i).owner();

            if (baseCell == null || dir == null) {
                targets.add(baseCell);
                continue;
            }

            Cell target = baseCell;

            Direction side = ((i / 2) % 2 == 0) ? dir.left() : dir.right();
            Cell sideCell  = baseCell.getNeighbor(side);

            if (i > 0 && isAvailable(sideCell, segments)) {
                target = sideCell;
            }

            targets.add(target);
        }

        return targets;
    }

    private boolean isAvailable(Cell cell, List<SnakeSegment> segments) {
        if (cell == null) return false;
        if (!cell.isEmpty(Rock.class)) return false;
        for (SnakeSegment seg : segments) {
            if (seg.owner() == cell) return false;
        }
        return true;
    }
}