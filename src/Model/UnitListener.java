package Model;

import Model.gamefield.Unit;

public interface UnitListener {
    void activateChanged(Unit unit);
}