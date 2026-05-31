package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.Unit;

public  class Rock extends Unit {
    @Override
    public boolean canBelongTo(Cell cell) {
        // расстановка камней происходит первой
        return cell != null && cell.isEmpty();
    }

    @Override
    protected void onDeactivate() {
        if (owner() != null) owner().extractUnit(this);
    }

}