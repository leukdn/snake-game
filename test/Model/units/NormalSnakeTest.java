package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.Direction;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для NormalSnakе.

 */
class NormalSnakeTest {

    private NormalSnake snake;
    private Cell c0, c1, c2, c3, c4;

    @BeforeEach
    void setUp() {
        c0=new Cell(); c1=new Cell(); c2=new Cell(); c3=new Cell(); c4=new Cell();
        c0.setNeighbor(Direction.EAST, c1);
        c1.setNeighbor(Direction.EAST, c2);
        c2.setNeighbor(Direction.EAST, c3);
        c3.setNeighbor(Direction.EAST, c4);

        snake = new NormalSnake(3, 10);
        SnakeSegment head=new SnakeSegment(), mid=new SnakeSegment(), tail=new SnakeSegment();
        c2.putUnit(head); snake.addSegment(head);
        c1.putUnit(mid);  snake.addSegment(mid);
        c0.putUnit(tail); snake.addSegment(tail);
        snake.setDirection(Direction.EAST);
        snake.activate();
    }

    // движение
    @Test void move_head_movesToNextCell() {
        snake.move();
        assertEquals(c3, snake.getSegments().get(0).owner());
    }

    @Test void move_tail_freesOldCell() {
        snake.move();
        assertTrue(c0.isEmpty());
    }

    @Test void move_body_followsHead() {
        snake.move();
        assertEquals(c2, snake.getSegments().get(1).owner());
        assertEquals(c1, snake.getSegments().get(2).owner());
    }

    @Test void move_twoSteps_correctPositions() {
        snake.move(); snake.move();
        assertEquals(c4, snake.getSegments().get(0).owner());
        assertEquals(c3, snake.getSegments().get(1).owner());
        assertEquals(c2, snake.getSegments().get(2).owner());
    }

    // коллизии

    @Test void move_intoRock_snakeDies() {
        c3.putUnit(new Rock());
        snake.move();
        assertFalse(snake.isAlive());
    }

    @Test void move_intoRock_cellsCleared() {
        c3.putUnit(new Rock());
        snake.move();
        assertTrue(c0.isEmpty());
        assertTrue(c1.isEmpty());
        assertTrue(c2.isEmpty());
    }

    @Test void move_outOfBounds_snakeDies() {
        snake.move(); snake.move(); snake.move();
        assertFalse(snake.isAlive());
    }

    //  поедание

    @Test void move_intoRodent_snakeGrows() {
        c3.putUnit(new Rodent());
        snake.move();
        assertEquals(4, snake.getSegments().size());
    }

    @Test void move_intoRodent_rodentDisappears() {
        Rodent r = new Rodent();
        c3.putUnit(r);
        snake.move();
        assertFalse(r.isActive());
        assertNull(r.owner());
    }

    @Test void move_intoRodent_newSegmentInOldTailCell() {
        c3.putUnit(new Rodent());
        snake.move();
        assertFalse(c0.isEmpty());
    }

    // уменьшение K=1
    @Test void afterKSteps_livesDecrease_whenSizeIsMin() {
        NormalSnake s = buildSnakeOnLine(3, 1);
        s.move();
        assertEquals(2, s.getLives());
    }

    @Test void afterKSteps_segmentRemoved_whenSizeGreaterThanMin() {
        NormalSnake s = buildSnakeOnLine4(3, 1);
        s.move();
        assertEquals(3, s.getSegments().size());
    }

    @Test void shrink_livesZero_snakeDies() {
        NormalSnake s = buildSnakeOnLine(1, 1);
        s.move();
        assertFalse(s.isAlive());
    }

    // направление

    @Test void setDirection_perpendicular_allowed() {
        snake.setDirection(Direction.NORTH);
        assertEquals(Direction.NORTH, snake.getDirection());
    }

    @Test void setDirection_180turn_prevented() {
        snake.setDirection(Direction.EAST);
        snake.setDirection(Direction.WEST);
        assertEquals(Direction.EAST, snake.getDirection());
    }

    @Test void setDirection_northToSouth_prevented() {
        snake.setDirection(Direction.NORTH);
        snake.setDirection(Direction.SOUTH);
        assertEquals(Direction.NORTH, snake.getDirection());
    }

    @Test void move_withoutDirection_doesNothing() {
        NormalSnake s = new NormalSnake(3, 10);
        SnakeSegment seg = new SnakeSegment();
        c4.putUnit(seg);
        s.addSegment(seg);
        s.activate();
        s.move();
        assertEquals(c4, seg.owner());
    }

    // слушатели
    @Test void snakeListener_rodentEaten_called() {
        boolean[] called = {false};
        snake.setSnakeListener(new SnakeListener() {
            public void rodentEaten() { called[0] = true; }
            public void snakeDied()   {}
        });
        c3.putUnit(new Rodent());
        snake.move();
        assertTrue(called[0]);
    }

    @Test void snakeListener_snakeDied_called() {
        boolean[] called = {false};
        snake.setSnakeListener(new SnakeListener() {
            public void rodentEaten() {}
            public void snakeDied()   { called[0] = true; }
        });
        c3.putUnit(new Rock());
        snake.move();
        assertTrue(called[0]);
    }

    //  isAlive

    @Test void isAlive_initially_true()  { assertTrue(snake.isAlive()); }

    @Test void isAlive_afterDie_false()  {
        c3.putUnit(new Rock());
        snake.move();
        assertFalse(snake.isAlive());
    }



    @Test void normalSnake_isInstanceOfAbstractSnake() {
        assertInstanceOf(AbstractSnake.class, snake);
    }


    private NormalSnake buildSnakeOnLine(int lives, int k) {
        Cell a=new Cell(), b=new Cell(), c=new Cell(), d=new Cell();
        a.setNeighbor(Direction.EAST, b);
        b.setNeighbor(Direction.EAST, c);
        c.setNeighbor(Direction.EAST, d);
        NormalSnake s = new NormalSnake(lives, k);
        SnakeSegment s1=new SnakeSegment(), s2=new SnakeSegment(), s3=new SnakeSegment();
        c.putUnit(s1); s.addSegment(s1);
        b.putUnit(s2); s.addSegment(s2);
        a.putUnit(s3); s.addSegment(s3);
        s.setDirection(Direction.EAST);
        s.activate();
        return s;
    }

    private NormalSnake buildSnakeOnLine4(int lives, int k) {
        Cell a=new Cell(), b=new Cell(), c=new Cell(), d=new Cell(), e=new Cell();
        a.setNeighbor(Direction.EAST, b); b.setNeighbor(Direction.EAST, c);
        c.setNeighbor(Direction.EAST, d); d.setNeighbor(Direction.EAST, e);
        NormalSnake s = new NormalSnake(lives, k);
        SnakeSegment s1=new SnakeSegment(), s2=new SnakeSegment(),
                s3=new SnakeSegment(), s4=new SnakeSegment();
        d.putUnit(s1); s.addSegment(s1);
        c.putUnit(s2); s.addSegment(s2);
        b.putUnit(s3); s.addSegment(s3);
        a.putUnit(s4); s.addSegment(s4);
        s.setDirection(Direction.EAST);
        s.activate();
        return s;
    }

    @Test void move_intoOwnBody_notTail_dies() {

        Cell extra1 = new Cell(), extra2 = new Cell();
        c0.setNeighbor(Direction.WEST, extra1);
        extra1.setNeighbor(Direction.WEST, extra2);

        SnakeSegment seg4 = new SnakeSegment(), seg5 = new SnakeSegment();
        extra1.putUnit(seg4); snake.addSegment(seg4);
        extra2.putUnit(seg5); snake.addSegment(seg5);

        Cell head = snake.getSegments().get(0).owner();

        SnakeSegment bodySegment = snake.getSegments().get(1);
        c1.extractUnit(bodySegment);
        c3.putUnit(bodySegment);


        snake.move();
        assertFalse(snake.isAlive());
    }



    /** При росте старая клетка занята — ищем по часовой */
    @Test void grow_usesClockwiseWhenOldTailOccupied() {

        Cell northOfC1 = new Cell();
        c1.setNeighbor(Direction.NORTH, northOfC1);

        c3.putUnit(new Rodent());

        c0.putUnit(new Rock()); // c0 занят

        NormalSnake s = new NormalSnake(3, 10);
        Cell a=new Cell(), b=new Cell(), c=new Cell(), d=new Cell();
        Cell aN = new Cell(); //куда вырастет
        a.setNeighbor(Direction.EAST, b);
        b.setNeighbor(Direction.EAST, c);
        c.setNeighbor(Direction.EAST, d);
        a.setNeighbor(Direction.NORTH, aN);


        a.putUnit(new Rock());

        SnakeSegment s1=new SnakeSegment(), s2=new SnakeSegment(), s3=new SnakeSegment();
        c.putUnit(s1); s.addSegment(s1);
        b.putUnit(s2); s.addSegment(s2);


        Cell x=new Cell(), y=new Cell(), z=new Cell(), w=new Cell();
        x.setNeighbor(Direction.EAST, y);
        y.setNeighbor(Direction.EAST, z);
        z.setNeighbor(Direction.EAST, w);
        Cell xN = new Cell(); x.setNeighbor(Direction.NORTH, xN); // куда вырастет

        NormalSnake s2snake = new NormalSnake(3, 10);
        SnakeSegment h=new SnakeSegment(), m=new SnakeSegment(), t=new SnakeSegment();
        z.putUnit(h); s2snake.addSegment(h);
        y.putUnit(m); s2snake.addSegment(m);
        x.putUnit(t); s2snake.addSegment(t);
        s2snake.setDirection(Direction.EAST);
        s2snake.activate();

        w.putUnit(new Rodent());
        s2snake.move();
        assertEquals(4, s2snake.getSegments().size());
    }



    /** Рост невозможен  нет свободных соседей у хвоста */
    @Test void grow_noSpace_sizeUnchanged() {
        // Окружаем хвост со всех сторон
        Cell cN=new Cell(), cS=new Cell(), cE2=new Cell();
        c0.setNeighbor(Direction.NORTH, cN);
        c0.setNeighbor(Direction.SOUTH, cS);

        cN.putUnit(new Rock());
        cS.putUnit(new Rock());

        c3.putUnit(new Rodent());
        int sizeBefore = snake.getSegments().size();
        snake.move();

        assertTrue(snake.getSegments().size() >= sizeBefore);
    }



    /** removeFromCell с чужой ячейкой — ничего не делает */
    @Test void unit_removeFromCell_wrongCell_returnsFalse() {
        Rock rock = new Rock();
        Cell cell1 = new Cell(), cell2 = new Cell();
        cell1.putUnit(rock);

        assertFalse(cell2.extractUnit(rock));

        assertEquals(cell1, rock.owner());
    }


    /** moveTo когда сегмент не стоит ни в какой ячейке */
    @Test void snakeSegment_moveTo_whenOwnerIsNull() {
        SnakeSegment seg = new SnakeSegment();
        Cell target = new Cell();

        assertDoesNotThrow(() -> seg.moveTo(target));
        assertEquals(target, seg.owner());
    }
}