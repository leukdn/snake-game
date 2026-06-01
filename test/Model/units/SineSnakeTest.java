package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.Direction;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для SineSnake.
 */
class SineSnakeTest {

    private SineSnake snake;
    private Cell c0, c1, c2, c3, c4, c5, c6, c7, c8, c9;
    private Cell n5, n6, s5, s6;

    @BeforeEach
    void setUp() {
        c0=new Cell(); c1=new Cell(); c2=new Cell(); c3=new Cell();
        c4=new Cell(); c5=new Cell(); c6=new Cell(); c7=new Cell();
        c8=new Cell(); c9=new Cell();
        c0.setNeighbor(Direction.EAST, c1); c1.setNeighbor(Direction.EAST, c2);
        c2.setNeighbor(Direction.EAST, c3); c3.setNeighbor(Direction.EAST, c4);
        c4.setNeighbor(Direction.EAST, c5); c5.setNeighbor(Direction.EAST, c6);
        c6.setNeighbor(Direction.EAST, c7); c7.setNeighbor(Direction.EAST, c8);
        c8.setNeighbor(Direction.EAST, c9);


        n5=new Cell(); c5.setNeighbor(Direction.NORTH, n5);
        n6=new Cell(); n5.setNeighbor(Direction.EAST, n6);
        s5=new Cell(); c5.setNeighbor(Direction.SOUTH, s5);
        s6=new Cell(); s5.setNeighbor(Direction.EAST, s6);

        snake = new SineSnake(3, 10);
        SnakeSegment head=new SnakeSegment(), mid=new SnakeSegment(), tail=new SnakeSegment();
        c4.putUnit(head); snake.addSegment(head);
        c3.putUnit(mid);  snake.addSegment(mid);
        c2.putUnit(tail); snake.addSegment(tail);
        snake.setDirection(Direction.EAST);
        snake.activate();
    }


    @Test void phase0_head_goes_left_when_available() {
        snake.move();
        assertTrue(snake.isAlive());
        assertEquals(n5, snake.getSegments().get(0).owner());
    }

    // голова двигается каждый шаг
    @Test void head_moves_every_step() {
        snake.move();
        assertTrue(snake.isAlive());
        Cell headAfterStep1 = snake.getSegments().get(0).owner();

        snake.move();
        assertTrue(snake.isAlive());
        Cell headAfterStep2 = snake.getSegments().get(0).owner();

        assertNotEquals(headAfterStep1, headAfterStep2);
    }


    @Test void fallback_to_straight_when_side_blocked_by_rock() {
        n5.putUnit(new Rock());
        snake.move();
        assertTrue(snake.isAlive());
        assertEquals(c5, snake.getSegments().get(0).owner());
    }

    // тело следует за головой — сегменты связаны
    @Test void body_follows_head_connected() {
        Cell oldHead = snake.getSegments().get(0).owner(); // c4
        Cell oldMid  = snake.getSegments().get(1).owner(); // c3
        snake.move();
        assertTrue(snake.isAlive());
        assertEquals(oldHead, snake.getSegments().get(1).owner());
        assertEquals(oldMid,  snake.getSegments().get(2).owner());
    }


    @Test void sineSnake_isInstanceOfAbstractSnake() {
        assertInstanceOf(AbstractSnake.class, snake);
    }
}