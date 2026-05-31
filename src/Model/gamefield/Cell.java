package Model.gamefield;

import Model.CellListener;
import java.util.*;

public class Cell {

    private final Map<Direction, Cell> neighbors = new EnumMap<>(Direction.class);
    private final List<Unit> units = new ArrayList<>();
    private final List<CellListener> listeners = new ArrayList<>();

    public void addCellListener(CellListener l) { listeners.add(l); }


    public void setNeighbor(Direction d, Cell n) {
        if (d == null || n == null) return;

        // Разорвать старую связь
        Cell oldNeighbor = neighbors.get(d);
        if (oldNeighbor != null) {
            oldNeighbor.neighbors.remove(d.opposite()); // c2 забывает c1
        }

        // Разорвать старую связь  в обратном направлении
        Cell oldOpposite = n.neighbors.get(d.opposite());
        if (oldOpposite != null) {
            oldOpposite.neighbors.remove(d);
        }

        // Установить новую двустороннюю связь
        neighbors.put(d, n);
        n.neighbors.put(d.opposite(), this);
    }

    public boolean putUnit(Unit unit) {
        if (unit == null) return false;
        if (unit.assignToCell(this)) {
            units.add(unit);
            unit.activate();
            listeners.forEach(l -> l.unitPlaced(unit));
            return true;
        }
        return false;
    }

    public boolean extractUnit(Unit unit) {
        if (units.remove(unit)) {
            unit.removeFromCell(this);
            listeners.forEach(l -> l.unitExtracted(unit));
            return true;
        }
        return false;
    }

    public void deactivate() {
        for (Unit u : new ArrayList<>(units)) u.deactivate();
    }

    public <T extends Unit> List<T> getUnits(Class<T> cls) {
        List<T> result = new ArrayList<>();
        for (Unit u : units)
            if (cls.isInstance(u)) result.add(cls.cast(u));
        return result;
    }

    public boolean isEmpty()                          { return units.isEmpty(); }
    public boolean isEmpty(Class<? extends Unit> cls) { return getUnits(cls).isEmpty(); }

    public Cell getNeighbor(Direction d)              { return neighbors.get(d); }
    public Map<Direction, Cell> getNeighbors()        { return Collections.unmodifiableMap(neighbors); }
    public boolean isNeighbor(Cell other)             { return neighbors.containsValue(other); }
}