package View;

import Model.gamefield.Cell;
import Model.units.Rock;
import Model.units.Rodent;
import Model.units.SnakeSegment;

import java.awt.*;

/**
 * Отрисовка одной ячейки.
 */
public class CellView {

    // Цвета в стиле ретро-терминала
    static final Color COLOR_BG        = new Color(15, 20, 30);
    static final Color COLOR_GRID      = new Color(30, 40, 55);
    static final Color COLOR_ROCK      = new Color(80, 90, 110);
    static final Color COLOR_ROCK_HL   = new Color(120, 130, 150);
    static final Color COLOR_RODENT    = new Color(220, 180, 60);
    static final Color COLOR_RODENT_HL = new Color(255, 220, 80);
    static final Color COLOR_SNAKE_H   = new Color(60, 220, 120);  // голова
    static final Color COLOR_SNAKE_B   = new Color(40, 160, 90);   // тело
    static final Color COLOR_SNAKE_HL  = new Color(100, 255, 160);

    private final Cell cell;
    private final int x;
    private final int y;
    private final int size;

    public CellView(Cell cell, int x, int y, int size) {
        this.cell = cell;
        this.x    = x;
        this.y    = y;
        this.size = size;
    }

    public void draw(Graphics2D g, boolean isHead) {
        // фон ячейки
        g.setColor(COLOR_BG);
        g.fillRect(x, y, size, size);

        // сетка
        g.setColor(COLOR_GRID);
        g.drawRect(x, y, size, size);

        if (!cell.getUnits(Rock.class).isEmpty())         drawRock(g);
        else if (!cell.getUnits(Rodent.class).isEmpty())  drawRodent(g);
        else if (!cell.getUnits(SnakeSegment.class).isEmpty()) drawSegment(g, isHead);
    }

    private void drawRock(Graphics2D g) {
        int pad = size / 6;
        g.setColor(COLOR_ROCK);
        g.fillRect(x + pad, y + pad, size - pad*2, size - pad*2); // Просто квадрат без бликов
    }

    private void drawRodent(Graphics2D g) {
        int pad = size / 5;
        // тело
        g.setColor(COLOR_RODENT);
        g.fillOval(x + pad, y + pad + size/8, size - pad*2, size - pad*2 - size/8);
        // ушки
        g.fillOval(x + pad + 2, y + pad - size/6, size/5, size/5);
        g.fillOval(x + size - pad - size/5 - 2, y + pad - size/6, size/5, size/5);
        // блик
        g.setColor(COLOR_RODENT_HL);
        g.fillOval(x + pad + 3, y + pad + size/8 + 2, size/6, size/6);
    }

    private void drawSegment(Graphics2D g, boolean isHead) {
        int pad = size / 10;
        g.setColor(isHead ? COLOR_SNAKE_H : COLOR_SNAKE_B);
        g.fillRect(x + pad, y + pad, size - pad*2, size - pad*2); // Плоские сегменты

        // глаза у головы
        if (isHead) {
            g.setColor(COLOR_BG);
            int ey = y + size/3;
            g.fillOval(x + size/3 - 2, ey, 4, 4);
            g.fillOval(x + size*2/3 - 2, ey, 4, 4);
        }
    }
}