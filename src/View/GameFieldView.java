package View;

import Model.gamefield.Cell;
import Model.gamefield.GameField;
import Model.units.AbstractSnake;
import Model.units.SnakeSegment;

import javax.swing.*;
import java.awt.*;

public class GameFieldView extends JPanel {

    private static final int CELL_SIZE = 32;
    private final GameField gameField;

    public GameFieldView(GameField gameField) {
        this.gameField = gameField;
        setPreferredSize(new Dimension(
                gameField.getWidth()  * CELL_SIZE,
                gameField.getHeight() * CELL_SIZE));
        setBackground(CellView.COLOR_BG);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Cell headCell = findHeadCell();

        int x = 0, y = 0;
        for (Cell cell : gameField) {
            boolean isHead = (cell == headCell);
            new CellView(cell, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE)
                    .draw(g2, isHead);
            x++;
            if (x >= gameField.getWidth()) { x = 0; y++; }
        }
    }

    private Cell findHeadCell() {
        AbstractSnake snake = gameField.getSnake(); // ← AbstractSnake
        if (snake == null || snake.getSegments().isEmpty()) return null;
        return snake.getSegments().get(0).owner();
    }
}