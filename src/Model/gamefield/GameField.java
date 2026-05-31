package Model.gamefield;

import Model.units.AbstractSnake;
import java.util.*;

public class GameField implements Iterable<Cell> {

    private final int width;
    private final int height;
    private Cell topLeft;
    private AbstractSnake snake; // ← AbstractSnake вместо Snake

    public GameField(Dimension2D size) {
        this.width  = size.getWidth();
        this.height = size.getHeight();
        linkNeighbors();
    }

    private void linkNeighbors() {
        Cell prevRowStart = null;
        for (int y = 0; y < height; y++) {
            Cell currentRowStart = null;
            Cell prevInRow = null;
            for (int x = 0; x < width; x++) {
                Cell current = new Cell();
                if (x == 0 && y == 0) topLeft = current;
                if (prevInRow != null)
                    prevInRow.setNeighbor(Direction.EAST, current);
                if (prevRowStart != null) {
                    prevRowStart.setNeighbor(Direction.SOUTH, current);
                    prevRowStart = prevRowStart.getNeighbor(Direction.EAST);
                }
                if (x == 0) currentRowStart = current;
                prevInRow = current;
            }
            prevRowStart = currentRowStart;
        }
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        Cell current = topLeft;
        for (int i = 0; i < x; i++) current = current.getNeighbor(Direction.EAST);
        for (int j = 0; j < y; j++) current = current.getNeighbor(Direction.SOUTH);
        return current;
    }

    public void setSnake(AbstractSnake s) { this.snake = s; }
    public AbstractSnake getSnake()       { return snake; }

    public void deactivate() {
        for (Cell c : this) c.deactivate();
    }

    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public <T extends Unit> List<T> getAllUnits(Class<T> cls) {
        List<T> result = new ArrayList<>();
        for (Cell c : this) result.addAll(c.getUnits(cls));
        return result;
    }

    @Override
    public Iterator<Cell> iterator() {
        return new Iterator<>() {
            private Cell rowStart = topLeft;
            private Cell current  = topLeft;

            public boolean hasNext() { return current != null; }

            public Cell next() {
                if (current == null) throw new NoSuchElementException();
                Cell res = current;
                current = current.getNeighbor(Direction.EAST);
                if (current == null && rowStart != null) {
                    rowStart = rowStart.getNeighbor(Direction.SOUTH);
                    current  = rowStart;
                }
                return res;
            }
        };
    }
}