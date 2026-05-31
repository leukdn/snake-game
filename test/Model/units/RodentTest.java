package Model.units;
import Model.gamefield.Cell;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RodentTest extends UnitContractTest<Rodent> {
    protected Rodent createUnit() { return new Rodent(); }
    @Test void canBelongTo_emptyCell_true()  { assertTrue(createUnit().canBelongTo(new Cell())); }
    @Test void canBelongTo_null_false()      { assertFalse(createUnit().canBelongTo(null)); }
    @Test void canBelongTo_cellWithRock_false() {
        Cell c = new Cell(); c.putUnit(new Rock()); assertFalse(createUnit().canBelongTo(c));
    }
    @Test void canBelongTo_cellWithSnakeSegment_false() {
        Cell c = new Cell(); c.putUnit(new SnakeSegment()); assertFalse(createUnit().canBelongTo(c));
    }
    @Test void onDeactivate_extractsFromCell() {
        Cell c = new Cell(); Rodent r = createUnit(); c.putUnit(r);
        r.deactivate(); assertTrue(c.isEmpty()); assertNull(r.owner());
    }
}