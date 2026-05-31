package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.Unit;

/**
 * Грызун — пассивный юнит-цель
 */
public  class Rodent extends Unit {
    @Override
    public boolean canBelongTo(Cell cell) {
        // грызун может занять только пустую ячейку
        return cell != null
                && cell.isEmpty(Rock.class)
                && cell.isEmpty(SnakeSegment.class);
    }

    @Override
    protected void onDeactivate() {
        if (owner() != null) owner().extractUnit(this);
    }
}