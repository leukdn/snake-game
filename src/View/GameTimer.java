package View;

import Model.gamefield.Game;
import javax.swing.Timer;

/**
 * Игровой таймер
 */
public class GameTimer {

    private final Timer timer;

    public GameTimer(Game game) {
        this.timer = new Timer(300, e -> game.update());
        this.timer.setCoalesce(true);
    }

    public void start() { timer.start(); }
    public void stop()  { timer.stop(); }

    public void setDelay(int ms) {
        timer.setDelay(ms);
    }
}