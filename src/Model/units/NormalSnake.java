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
     * Строит список целей: голова → nextCell,
     * каждый следующий сегмент на место предыдущего.
     */
    @Override
    protected void shiftSegments(Cell nextCell) {
        List<SnakeSegment> segments = getSegments();

        List<Cell> targets = new ArrayList<>();
        targets.add(nextCell);
        for (int i = 0; i < segments.size() - 1; i++) {
            targets.add(segments.get(i).owner());
        }

        applyTargets(targets);
    }
}