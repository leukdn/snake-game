package Model.gamefield;

import Model.CellListener;
import Model.units.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {
    private Cell cell;

    @BeforeEach
    void setUp() { cell = new Cell(); }

    @Test void putUnit_returnsTrue_andUnitBecomesActive() {
        Rodent r = new Rodent();
        assertTrue(cell.putUnit(r));
        assertTrue(r.isActive());
    }
    @Test void putUnit_setsOwner() {
        Rodent r = new Rodent(); cell.putUnit(r);
        assertEquals(cell, r.owner());
    }
    @Test void putUnit_cellBecomesNotEmpty() {
        cell.putUnit(new Rodent()); assertFalse(cell.isEmpty());
    }
    @Test void putUnit_null_returnsFalse() {
        assertFalse(cell.putUnit(null));
    }
    @Test void putUnit_sameUnitTwice_returnsFalse() {
        Rodent r = new Rodent(); cell.putUnit(r);
        assertFalse(new Cell().putUnit(r));
    }
    @Test void putUnit_whenCannotBelong_returnsFalse() {
        cell.putUnit(new Rock());
        Rodent r = new Rodent();
        assertFalse(cell.putUnit(r));
        assertNull(r.owner());
    }
    @Test void extractUnit_returnsTrue_andCellBecomesEmpty() {
        Rock rock = new Rock(); cell.putUnit(rock);
        assertTrue(cell.extractUnit(rock)); assertTrue(cell.isEmpty());
    }
    @Test void extractUnit_clearsOwner() {
        Rock rock = new Rock(); cell.putUnit(rock);
        cell.extractUnit(rock); assertNull(rock.owner());
    }
    @Test void extractUnit_unitNotInCell_returnsFalse() {
        assertFalse(cell.extractUnit(new Rock()));
    }
    @Test void extractUnit_null_returnsFalse() {
        assertFalse(cell.extractUnit(null));
    }
    @Test void isEmpty_newCell_isTrue() { assertTrue(cell.isEmpty()); }
    @Test void isEmpty_byClass() {
        cell.putUnit(new Rock());
        assertFalse(cell.isEmpty(Rock.class));
        assertTrue(cell.isEmpty(Rodent.class));
    }
    @Test void getUnits_returnsCorrectType() {
        cell.putUnit(new Rock());
        assertEquals(1, cell.getUnits(Rock.class).size());
        assertEquals(0, cell.getUnits(Rodent.class).size());
    }
    @Test void setNeighbor_bidirectional() {
        Cell north = new Cell(); cell.setNeighbor(Direction.NORTH, north);
        assertEquals(north, cell.getNeighbor(Direction.NORTH));
        assertEquals(cell, north.getNeighbor(Direction.SOUTH));
    }
    @Test void getNeighbor_noNeighbor_returnsNull() {
        assertNull(cell.getNeighbor(Direction.EAST));
    }
    @Test void isNeighbor_true_afterSetNeighbor() {
        Cell other = new Cell(); cell.setNeighbor(Direction.EAST, other);
        assertTrue(cell.isNeighbor(other));
        assertTrue(other.isNeighbor(cell));
    }
    @Test void isNeighbor_false_whenNotConnected() {
        assertFalse(cell.isNeighbor(new Cell()));
    }

    @Test
    void isEmpty_byType_trueWhenNoSuchUnit() {
        Cell cell = new Cell();
        cell.putUnit(new Rock());

        assertTrue(cell.isEmpty(SnakeSegment.class));
    }

    @Test
    void isEmpty_byType_falseWhenUnitExists() {
        Cell cell = new Cell();
        cell.putUnit(new SnakeSegment());

        assertFalse(cell.isEmpty(SnakeSegment.class));
    }

    /**  деактивируем все юниты в ячейке */
    @Test void deactivate_deactivatesAllUnits() {
        Rock rock = new Rock();
        cell.putUnit(rock);
        assertTrue(rock.isActive());
        cell.deactivate();
        assertFalse(rock.isActive());
        assertTrue(cell.isEmpty());
    }

    /** deactivate() на пустой ячейке  */
    @Test void deactivate_emptyCell_doesNotThrow() {
        assertDoesNotThrow(() -> cell.deactivate());
    }

    /** addCellListener — срабатывает при putUnit */
    @Test void addCellListener_triggersOnPutUnit() {
        boolean[] placed = {false};
        cell.addCellListener(new CellListener() {
            public void unitPlaced(Unit u)    { placed[0] = true; }
            public void unitExtracted(Unit u) {}
        });
        cell.putUnit(new Rodent());
        assertTrue(placed[0]);
    }

    /** addCellListener — срабатывает при extractUnit */
    @Test void addCellListener_triggersOnExtractUnit() {
        boolean[] extracted = {false};
        Rock rock = new Rock();
        cell.putUnit(rock);
        cell.addCellListener(new CellListener() {
            public void unitPlaced(Unit u)    {}
            public void unitExtracted(Unit u) { extracted[0] = true; }
        });
        cell.extractUnit(rock);
        assertTrue(extracted[0]);
    }

    /** setNeighbor — разрывает старую связь c1 ↔ c2 при переустановке */
    @Test void setNeighbor_breaksOldLink() {
        Cell c1 = new Cell(), c2 = new Cell(), c3 = new Cell();
        c1.setNeighbor(Direction.EAST, c2); // c1 ↔ c2
        c1.setNeighbor(Direction.EAST, c3); // c1 ↔ c3, c2 должна забыть c1
        assertEquals(c3, c1.getNeighbor(Direction.EAST));
        assertNull(c2.getNeighbor(Direction.WEST)); // c2 забыла c1
        assertEquals(c1, c3.getNeighbor(Direction.WEST));
    }

    /** setNeighbor — разрывает обратную связь n */
    @Test void setNeighbor_breaksReverseLink() {
        Cell c1 = new Cell(), c2 = new Cell(), c3 = new Cell();
        c2.setNeighbor(Direction.WEST, c1); // c2 ↔ c1
        c3.setNeighbor(Direction.EAST, c2); // c3 ↔ c2; c1 должна забыть c2
        assertNull(c1.getNeighbor(Direction.EAST)); // c1 забыла c2
        assertEquals(c2, c3.getNeighbor(Direction.EAST));
        assertEquals(c3, c2.getNeighbor(Direction.WEST));
    }

    /** setNeighbor null — ничего не делает */
    @Test void setNeighbor_null_doesNothing() {
        assertDoesNotThrow(() -> cell.setNeighbor(null, new Cell()));
        assertDoesNotThrow(() -> cell.setNeighbor(Direction.EAST, null));
    }

    /** возвращает немодифицируемую карту */
    @Test void getNeighbors_returnsUnmodifiable() {
        Cell north = new Cell();
        cell.setNeighbor(Direction.NORTH, north);
        var neighbors = cell.getNeighbors();
        assertEquals(1, neighbors.size());
        assertThrows(UnsupportedOperationException.class,
                () -> neighbors.put(Direction.EAST, new Cell()));
    }



}


