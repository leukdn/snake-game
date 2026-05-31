package Model;
import Model.gamefield.Unit;

public interface GameListener {

    void gameIsOver(boolean win);
    void fieldChanged();
    default void activateChanged(Unit unit) {}

    default void scoreChanged(int score) {}      // счёт изменился
    default void livesChanged(int lives) {}      // жизни изменились
    default void stepsChanged(int steps, int k) {} // шагов после еды изменилось
}