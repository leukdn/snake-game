package View;

import Model.gamefield.Direction;
import Model.gamefield.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Контроллер — слушает клавиши
 */
public class Controller extends KeyAdapter {

    private final Game game;

    public Controller(Game game) {
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Direction d = toDirection(e.getKeyCode());
        if (d != null) game.setDirection(d);
    }

    private Direction toDirection(int key) {
        return switch (key) {
            case KeyEvent.VK_UP,    KeyEvent.VK_W -> Direction.NORTH;
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> Direction.SOUTH;
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> Direction.WEST;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> Direction.EAST;
            default -> null;
        };
    }
}