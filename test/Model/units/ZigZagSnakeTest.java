package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.Dimension2D;
import Model.gamefield.Direction;
import Model.gamefield.GameField;
import Model.Spawners.SimpleSpawner;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для ZigZagSnake.
 */
class ZigZagSnakeTest {

    private GameField field;
    private static final int LIVES = 10;
    private static final int K = 20;

    @BeforeEach
    void setUp() {
        field = new GameField(new Dimension2D(10, 10));
    }

    // Фабрики
    @Test void normalFactory_createsNormalSnake() {
        AbstractSnake s = new NormalSnakeFactory().createSnake(LIVES, K);
        assertInstanceOf(NormalSnake.class, s);
    }

    @Test void normalFactory_setsCorrectLivesAndK() {
        AbstractSnake s = new NormalSnakeFactory().createSnake(5, 15);
        assertEquals(5,  s.getLives());
        assertEquals(15, s.getK());
    }

    @Test void zigzagFactory_createsZigZagSnake() {
        AbstractSnake s = new ZigZagSnakeFactory().createSnake(LIVES, K);
        assertInstanceOf(ZigZagSnake.class, s);
    }

    @Test void zigzagFactory_setsCorrectLivesAndK() {
        AbstractSnake s = new ZigZagSnakeFactory().createSnake(8, 12);
        assertEquals(8,  s.getLives());
        assertEquals(12, s.getK());
    }

    @Test void both_factories_implement_SnakeFactory() {
        assertInstanceOf(SnakeFactory.class, new NormalSnakeFactory());
        assertInstanceOf(SnakeFactory.class, new ZigZagSnakeFactory());
    }

    //  Интеграция со спавнером

    @Test void spawnerDefault_usesNormalSnake() {
        new SimpleSpawner(field).placeSnake();
        assertInstanceOf(NormalSnake.class, field.getSnake());
    }

    @Test void spawnerWithZigzag_createsZigzagSnake() {
        new SimpleSpawner(field, new ZigZagSnakeFactory()).placeSnake();
        assertInstanceOf(ZigZagSnake.class, field.getSnake());
    }

    @Test void spawner_placesSnakeWith3Segments() {
        new SimpleSpawner(field, new ZigZagSnakeFactory()).placeSnake();
        assertEquals(3, field.getSnake().getSegments().size());
    }

    @Test void spawner_activatesSnake() {
        new SimpleSpawner(field, new ZigZagSnakeFactory()).placeSnake();
        assertTrue(field.getSnake().isActive());
    }

    @Test void spawner_setsDirectionEast() {
        new SimpleSpawner(field, new ZigZagSnakeFactory()).placeSnake();
        assertEquals(Direction.EAST, field.getSnake().getDirection());
    }

    // Базовое состояние

    @Test void zigzag_isInstanceOfAbstractSnake() {
        assertInstanceOf(AbstractSnake.class, new ZigZagSnake(3, 10));
    }

    @Test void zigzag_isAlive_afterActivate() {
        ZigZagSnake s = new ZigZagSnake(3, 10);
        s.activate();
        assertTrue(s.isAlive());
    }

    @Test void zigzag_initialLivesAndK() {
        ZigZagSnake s = new ZigZagSnake(7, 15);
        assertEquals(7,  s.getLives());
        assertEquals(15, s.getK());
    }

    // Движение головы

    @Test void head_movesToNextCell() {
        ZigZagSnake s = buildSnake3(field.getCell(5,5), field.getCell(5,6),
                field.getCell(5,7));
        s.setDirection(Direction.NORTH);
        s.move();
        assertEquals(field.getCell(5,4), s.getSegments().get(0).owner());
    }

    @Test void head_twoSteps() {
        ZigZagSnake s = buildSnake3(field.getCell(5,5), field.getCell(5,6),
                field.getCell(5,7));
        s.setDirection(Direction.NORTH);
        s.move(); s.move();
        assertEquals(field.getCell(5,3), s.getSegments().get(0).owner());
    }




    @Test void seg2_zigzagsLeft_when_available() {

        ZigZagSnake s = buildSnake4(
                field.getCell(5,4), field.getCell(5,5),
                field.getCell(5,6), field.getCell(5,7));
        s.setDirection(Direction.NORTH);
        s.move();

        assertEquals(field.getCell(4,5), s.getSegments().get(2).owner());
    }

    @Test void seg2_fallback_when_side_blocked_by_rock() {
        ZigZagSnake s = buildSnake4(
                field.getCell(5,4), field.getCell(5,5),
                field.getCell(5,6), field.getCell(5,7));
        field.getCell(4,5).putUnit(new Rock());
        s.setDirection(Direction.NORTH);
        s.move();

        assertEquals(field.getCell(5,5), s.getSegments().get(2).owner());
    }

    @Test void seg2_fallback_when_side_null_boundary() {

        ZigZagSnake s = buildSnake4(
                field.getCell(0,4), field.getCell(0,5),
                field.getCell(0,6), field.getCell(0,7));
        s.setDirection(Direction.NORTH);
        s.move();

        assertEquals(field.getCell(0,5), s.getSegments().get(2).owner());
    }

    @Test void seg2_fallback_when_side_has_own_segment() {
        ZigZagSnake s = buildSnake4(
                field.getCell(5,4), field.getCell(5,5),
                field.getCell(5,6), field.getCell(5,7));

        SnakeSegment extra = new SnakeSegment();
        field.getCell(4,5).putUnit(extra);
        s.addSegment(extra);
        s.setDirection(Direction.NORTH);
        s.move();
        assertEquals(field.getCell(5,5), s.getSegments().get(2).owner());
    }

    @Test void seg2_not_blocked_by_rodent() {
        // Грызун не является препятствием для зигзага
        ZigZagSnake s = buildSnake4(
                field.getCell(5,4), field.getCell(5,5),
                field.getCell(5,6), field.getCell(5,7));
        field.getCell(4,5).putUnit(new Rodent());
        s.setDirection(Direction.NORTH);
        s.move();
        assertEquals(field.getCell(4,5), s.getSegments().get(2).owner());
    }



    @Test void oldTailCell_freed_after_move() {
        ZigZagSnake s = buildSnake3(field.getCell(5,5), field.getCell(5,6),
                field.getCell(5,7));
        s.setDirection(Direction.NORTH);
        s.move();
        assertTrue(field.getCell(5,7).isEmpty(SnakeSegment.class));
    }

    @Test void noTwoSegmentsOnSameCell() {
        ZigZagSnake s = buildSnake4(
                field.getCell(5,4), field.getCell(5,5),
                field.getCell(5,6), field.getCell(5,7));
        s.setDirection(Direction.NORTH);
        s.move();
        assertUniquePositions(s);
    }

    // Коллизии

    @Test void move_intoRock_dies() {
        ZigZagSnake s = buildSnake3(field.getCell(5,5), field.getCell(5,6),
                field.getCell(5,7));
        field.getCell(5,4).putUnit(new Rock());
        s.setDirection(Direction.NORTH);
        s.move();
        assertFalse(s.isAlive());
    }

    @Test void move_outOfBounds_dies() {
        ZigZagSnake s = buildSnake3(field.getCell(5,1), field.getCell(5,2),
                field.getCell(5,3));
        s.setDirection(Direction.NORTH);
        s.move(); s.move(); // голова в (5,0)
        s.move(); // выход за границу → die
        assertFalse(s.isAlive());
    }

    //  Поедание

    @Test void eating_rodent_grows() {
        ZigZagSnake s = buildSnake3(field.getCell(5,5), field.getCell(5,6),
                field.getCell(5,7));
        field.getCell(5,4).putUnit(new Rodent());
        s.setDirection(Direction.NORTH);
        s.move();
        assertEquals(4, s.getSegments().size());
    }

    @Test void eating_rodentEaten_listener_called() {
        ZigZagSnake s = buildSnake3(field.getCell(5,5), field.getCell(5,6),
                field.getCell(5,7));
        boolean[] called = {false};
        s.setSnakeListener(new SnakeListener() {
            public void rodentEaten() { called[0] = true; }
            public void snakeDied()   {}
        });
        field.getCell(5,4).putUnit(new Rodent());
        s.setDirection(Direction.NORTH);
        s.move();
        assertTrue(called[0]);
    }

    //  Уменьшение

    @Test void shrink_livesDecrease_whenSizeMin() {
        ZigZagSnake s = buildSnakeOnLine3(3, 1);
        s.move();
        assertEquals(2, s.getLives());
    }

    @Test void shrink_tailRemoved_whenSizeGreaterThanMin() {
        ZigZagSnake s = buildSnakeOnLine4(3, 1);
        s.move();
        assertEquals(3, s.getSegments().size());
    }

    @Test void shrink_livesZero_dies() {
        ZigZagSnake s = buildSnakeOnLine3(1, 1);
        s.move();
        assertFalse(s.isAlive());
    }



    @Test void setDirection_180_prevented() {
        ZigZagSnake s = new ZigZagSnake(3, 10);
        s.setDirection(Direction.EAST);
        s.setDirection(Direction.WEST);
        assertEquals(Direction.EAST, s.getDirection());
    }

    @Test void snakeDied_listener_called() {
        ZigZagSnake s = buildSnake3(field.getCell(5,5), field.getCell(5,6),
                field.getCell(5,7));
        boolean[] called = {false};
        s.setSnakeListener(new SnakeListener() {
            public void rodentEaten() {}
            public void snakeDied()   { called[0] = true; }
        });
        field.getCell(5,4).putUnit(new Rock());
        s.setDirection(Direction.NORTH);
        s.move();
        assertTrue(called[0]);
    }


    /** Змея 3 сег */
    private ZigZagSnake buildSnake3(Cell head, Cell b, Cell tail) {
        ZigZagSnake s = new ZigZagSnake(LIVES, K);
        SnakeSegment s1=new SnakeSegment(), s2=new SnakeSegment(), s3=new SnakeSegment();
        head.putUnit(s1); s.addSegment(s1);
        b.putUnit(s2);    s.addSegment(s2);
        tail.putUnit(s3); s.addSegment(s3);
        s.activate();
        return s;
    }

    /** Змея 4 сег */
    private ZigZagSnake buildSnake4(Cell head, Cell b, Cell c, Cell tail) {
        ZigZagSnake s = new ZigZagSnake(LIVES, K);
        SnakeSegment s1=new SnakeSegment(), s2=new SnakeSegment(),
                s3=new SnakeSegment(), s4=new SnakeSegment();
        head.putUnit(s1); s.addSegment(s1);
        b.putUnit(s2);    s.addSegment(s2);
        c.putUnit(s3);    s.addSegment(s3);
        tail.putUnit(s4); s.addSegment(s4);
        s.activate();
        return s;
    }

    private ZigZagSnake buildSnakeOnLine3(int lives, int k) {
        Cell a=new Cell(), b=new Cell(), c=new Cell(), d=new Cell();
        a.setNeighbor(Direction.EAST, b);
        b.setNeighbor(Direction.EAST, c);
        c.setNeighbor(Direction.EAST, d);
        ZigZagSnake s = new ZigZagSnake(lives, k);
        SnakeSegment s1=new SnakeSegment(), s2=new SnakeSegment(), s3=new SnakeSegment();
        c.putUnit(s1); s.addSegment(s1);
        b.putUnit(s2); s.addSegment(s2);
        a.putUnit(s3); s.addSegment(s3);
        s.setDirection(Direction.EAST);
        s.activate();
        return s;
    }

    private ZigZagSnake buildSnakeOnLine4(int lives, int k) {
        Cell a=new Cell(), b=new Cell(), c=new Cell(), d=new Cell(), e=new Cell();
        a.setNeighbor(Direction.EAST, b); b.setNeighbor(Direction.EAST, c);
        c.setNeighbor(Direction.EAST, d); d.setNeighbor(Direction.EAST, e);
        ZigZagSnake s = new ZigZagSnake(lives, k);
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

    private void assertUniquePositions(ZigZagSnake s) {
        long unique = s.getSegments().stream()
                .map(SnakeSegment::owner)
                .filter(cell -> cell != null)
                .distinct().count();
        long nonNull = s.getSegments().stream()
                .map(SnakeSegment::owner)
                .filter(cell -> cell != null)
                .count();
        assertEquals(nonNull, unique, "Два сегмента на одной ячейке");
    }
}