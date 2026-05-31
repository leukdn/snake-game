package Model.gamefield;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    @Test void north_opposite_isSouth() { assertEquals(Direction.SOUTH, Direction.NORTH.opposite()); }
    @Test void south_opposite_isNorth() { assertEquals(Direction.NORTH, Direction.SOUTH.opposite()); }
    @Test void east_opposite_isWest()   { assertEquals(Direction.WEST,  Direction.EAST.opposite()); }
    @Test void west_opposite_isEast()   { assertEquals(Direction.EAST,  Direction.WEST.opposite()); }
    @Test void opposite_isSymmetric() {
        for (Direction d : Direction.values())
            assertEquals(d, d.opposite().opposite());
    }

    // left()
    @Test void north_left_isWest()  { assertEquals(Direction.WEST,  Direction.NORTH.left()); }
    @Test void south_left_isEast()  { assertEquals(Direction.EAST,  Direction.SOUTH.left()); }
    @Test void east_left_isNorth()  { assertEquals(Direction.NORTH, Direction.EAST.left()); }
    @Test void west_left_isSouth()  { assertEquals(Direction.SOUTH, Direction.WEST.left()); }

    //  right()
    @Test void north_right_isEast() { assertEquals(Direction.EAST,  Direction.NORTH.right()); }
    @Test void south_right_isWest() { assertEquals(Direction.WEST,  Direction.SOUTH.right()); }
    @Test void east_right_isSouth() { assertEquals(Direction.SOUTH, Direction.EAST.right()); }
    @Test void west_right_isNorth() { assertEquals(Direction.NORTH, Direction.WEST.right()); }

    // left и right симметричны
    @Test void left_right_areInverse() {
        for (Direction d : Direction.values())
            assertEquals(d, d.left().right());
    }
}