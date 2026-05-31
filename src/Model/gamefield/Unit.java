package Model.gamefield;

import Model.UnitListener;

import java.util.ArrayList;
import java.util.List;

public abstract class Unit {

    private Cell owner; // в какой находимся
    private boolean active;

    protected final List<UnitListener> listeners = new ArrayList<>();

    public abstract boolean canBelongTo(Cell cell); // проверка можно ли ставть


    public void addUnitListener(UnitListener l) {
        listeners.add(l);
    }

    public final void activate() {
        if (!active) {
            active = true;
            onActivate();
            listeners.forEach(l -> l.activateChanged(this));
        }
    }

    //деактивирует все юниты
    public final void deactivate() {
        if (active) {
            active = false;
            onDeactivate();
            listeners.forEach(l -> l.activateChanged(this));
        }
    }

    protected void onActivate() {}
    protected void onDeactivate() {}

    //назначить ячейку
    final boolean assignToCell(Cell cell) {
        if (owner == null && canBelongTo(cell)) {
            owner = cell;
            return true;
        }
        return false;
    }

    //убрать связь с ячейкой
    final boolean removeFromCell(Cell cell) {
        if (owner == cell) {
            owner = null;
            return true;
        }
        return false;
    }

    public Cell owner() { return owner; }
    public boolean isActive() { return active; }
}