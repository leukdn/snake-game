package Model.gamefield;
import Model.units.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class GameFieldTest {
    @Test void cornerCell_hasTwoNeighbors() {
        GameField f = new GameField(new Dimension2D(3,3));
        long c = Arrays.stream(Direction.values()).filter(d -> f.getCell(0,0).getNeighbor(d)!=null).count();
        assertEquals(2, c);
    }
    @Test void edgeCell_hasThreeNeighbors() {
        GameField f = new GameField(new Dimension2D(3,3));
        long c = Arrays.stream(Direction.values()).filter(d -> f.getCell(1,0).getNeighbor(d)!=null).count();
        assertEquals(3, c);
    }
    @Test void centerCell_hasFourNeighbors() {
        GameField f = new GameField(new Dimension2D(3,3));
        long c = Arrays.stream(Direction.values()).filter(d -> f.getCell(1,1).getNeighbor(d)!=null).count();
        assertEquals(4, c);
    }
    @Test void iterator_visitsAllCells() {
        GameField f = new GameField(new Dimension2D(4,5));
        int count=0; for (Cell ignored : f) count++;
        assertEquals(20, count);
    }
    @Test void iterator_noDuplicates() {
        GameField f = new GameField(new Dimension2D(3,3));
        Set<Cell> seen = new HashSet<>();
        for (Cell c : f) assertTrue(seen.add(c));
        assertEquals(9, seen.size());
    }
    @Test void getAllUnits_findsCorrectType() {
        GameField f = new GameField(new Dimension2D(2,2));
        f.getCell(0,0).putUnit(new Rock()); f.getCell(1,1).putUnit(new Rodent());
        assertEquals(1, f.getAllUnits(Rock.class).size());
        assertEquals(1, f.getAllUnits(Rodent.class).size());
    }
    @Test void getAllUnits_emptyField_returnsEmpty() {
        assertTrue(new GameField(new Dimension2D(3,3)).getAllUnits(Rock.class).isEmpty());
    }
    @Test void dimension2D_invalidSize_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Dimension2D(0,5));
        assertThrows(IllegalArgumentException.class, () -> new Dimension2D(5,-1));
    }
}