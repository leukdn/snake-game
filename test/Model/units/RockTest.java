package Model.units;
import Model.gamefield.Cell;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RockTest extends UnitContractTest<Rock> {
    protected Rock createUnit() { return new Rock(); }
    @Test void canBelongTo_emptyCell_true()  { assertTrue(createUnit().canBelongTo(new Cell())); }
    @Test void canBelongTo_null_false()      { assertFalse(createUnit().canBelongTo(null)); }
    @Test void canBelongTo_occupiedCell_false() {
        Cell c = new Cell(); c.putUnit(new Rodent());
        assertFalse(createUnit().canBelongTo(c));
    }
}