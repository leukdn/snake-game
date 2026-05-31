package Model.units;

import Model.gamefield.Cell;
import java.util.ArrayList;
import java.util.List;

/**
 * Обычная змея.

 */
public class NormalSnake extends AbstractSnake {

    public NormalSnake(int lives, int k) {
        super(lives, k);
    }

    /**
     * Стандартный сдвиг
     */
    @Override
    protected void shiftSegments(Cell nextCell) {
        List<SnakeSegment> segments = getSegments();

        // Запомнить целевые ячейки
        List<Cell> targets = new ArrayList<>();
        targets.add(nextCell);
        for (int i = 0; i < segments.size() - 1; i++) {
            targets.add(segments.get(i).owner());
        }

        // Снять все сегменты
        for (SnakeSegment seg : segments) {
            Cell c = seg.owner();
            if (c != null) c.extractUnit(seg);
        }

        // Расставить по целям
        for (int i = 0; i < segments.size(); i++) {
            Cell target = targets.get(i);
            if (target != null) segments.get(i).moveTo(target);
        }
    }
}