package Model.units;
import Model.gamefield.Cell;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SnakeSegmentTest extends UnitContractTest<SnakeSegment> {
    protected SnakeSegment createUnit() { return new SnakeSegment(); }
    @Test void canBelongTo_emptyCell_true()  { assertTrue(createUnit().canBelongTo(new Cell())); }
    @Test void canBelongTo_null_false()      { assertFalse(createUnit().canBelongTo(null)); }
    @Test void canBelongTo_cellWithSegment_false() {
        Cell c = new Cell(); c.putUnit(new SnakeSegment());
        assertFalse(createUnit().canBelongTo(c));
    }
    @Test void onDeactivate_extractsFromCell() {
        Cell c = new Cell(); SnakeSegment s = createUnit(); c.putUnit(s);
        s.deactivate(); assertTrue(c.isEmpty()); assertNull(s.owner());
    }
}