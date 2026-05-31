package Model;

import Model.gamefield.Unit;

public interface CellListener {
    void unitPlaced(Unit unit);
    void unitExtracted(Unit unit);
}