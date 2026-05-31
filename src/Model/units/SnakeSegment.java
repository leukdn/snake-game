package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.Unit;

/**
 * Один сегмент тела змеи.

 */
public final class SnakeSegment extends Unit {

    @Override
    public boolean canBelongTo(Cell cell) {
        return cell != null && cell.isEmpty(SnakeSegment.class);
    }

    /**
     * Переместить сегмент в целевую ячейку.

     */
    void moveTo(Cell target) {
        if (target == null) return;
        Cell current = owner();
        if (current != null) current.extractUnit(this);
        if (target != null) target.putUnit(this);
    }

    @Override
    protected void onDeactivate() {
        if (owner() != null) owner().extractUnit(this);
    }
}