package Model.Spawners;

import Model.gamefield.*;
import Model.units.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SimpleSpawnerTest {

    private GameField field;
    private SimpleSpawner spawner;

    @BeforeEach
    void setUp() {
        field   = new GameField(new Dimension2D(10, 10));
        spawner = new SimpleSpawner(field); // NormalSnakeFactory по умолчанию
        spawner.start();
    }

    // Базовая расстановка

    @Test void equipUnits_creates5Rocks() {
        assertEquals(5, field.getAllUnits(Rock.class).size());
    }

    @Test void equipUnits_creates3Rodents() {
        assertEquals(3, field.getAllUnits(Rodent.class).size());
    }

    @Test void equipUnits_creates3SnakeSegments() {
        assertEquals(3, field.getAllUnits(SnakeSegment.class).size());
    }

    @Test void equipUnits_segments_haveOwners() {
        field.getAllUnits(SnakeSegment.class)
                .forEach(s -> assertNotNull(s.owner()));
    }

    @Test void equipUnits_segments_areActive() {
        field.getAllUnits(SnakeSegment.class)
                .forEach(s -> assertTrue(s.isActive()));
    }

    @Test void equipUnits_rocks_areActive() {
        field.getAllUnits(Rock.class)
                .forEach(r -> assertTrue(r.isActive()));
    }

    @Test void equipUnits_rodents_areActive() {
        field.getAllUnits(Rodent.class)
                .forEach(r -> assertTrue(r.isActive()));
    }

    // Змея — тип и состояние

    @Test void equipUnits_snakeIsNotNull() {

        assertNotNull(field.getSnake());
    }

    @Test void equipUnits_defaultFactory_createsNormalSnake() {
        assertInstanceOf(NormalSnake.class, field.getSnake());
    }

    @Test void equipUnits_snakeIsAbstractSnake() {
        assertInstanceOf(AbstractSnake.class, field.getSnake());
    }

    @Test void equipUnits_snakeIsAlive() {
        assertTrue(field.getSnake().isAlive());
    }

    @Test void equipUnits_snakeIsActive() {
        assertTrue(field.getSnake().isActive());
    }

    @Test void equipUnits_snakeHas3Segments() {
        assertEquals(3, field.getSnake().getSegments().size());
    }

    @Test void equipUnits_snakeDirectionIsEast() {
        assertEquals(Direction.EAST, field.getSnake().getDirection());
    }

    @Test void equipUnits_snakeSegmentsAreConnected() {
        // Все сегменты змеи стоят в ячейках
        field.getSnake().getSegments()
                .forEach(seg -> assertNotNull(seg.owner()));
    }

    // Фабрика

    @Test void zigzagFactory_createsZigZagSnake() {
        GameField f = new GameField(new Dimension2D(10, 10));
        new SimpleSpawner(f, new ZigZagSnakeFactory()).start();
        assertInstanceOf(ZigZagSnake.class, f.getSnake());
    }

    @Test void zigzagFactory_snakeIsAlive() {
        GameField f = new GameField(new Dimension2D(10, 10));
        new SimpleSpawner(f, new ZigZagSnakeFactory()).start();
        assertTrue(f.getSnake().isAlive());
    }

    @Test void zigzagFactory_snakeHas3Segments() {
        GameField f = new GameField(new Dimension2D(10, 10));
        new SimpleSpawner(f, new ZigZagSnakeFactory()).start();
        assertEquals(3, f.getSnake().getSegments().size());
    }

    @Test void normalFactory_explicit_createsNormalSnake() {
        GameField f = new GameField(new Dimension2D(10, 10));
        new SimpleSpawner(f, new NormalSnakeFactory()).start();
        assertInstanceOf(NormalSnake.class, f.getSnake());
    }

    // spawnRodent

    @Test void spawnRodent_addsOneRodent() {
        spawner.spawnRodent();
        assertEquals(4, field.getAllUnits(Rodent.class).size());
    }

    @Test void spawnRodent_newRodentIsActive() {
        spawner.spawnRodent();
        field.getAllUnits(Rodent.class)
                .forEach(r -> assertTrue(r.isActive()));
    }

    @Test void spawnRodent_newRodentHasOwner() {
        spawner.spawnRodent();
        field.getAllUnits(Rodent.class)
                .forEach(r -> assertNotNull(r.owner()));
    }

    @Test void spawnRodent_twice_addsTwo() {
        spawner.spawnRodent();
        spawner.spawnRodent();
        assertEquals(5, field.getAllUnits(Rodent.class).size());
    }

    //  placeSnake отдельно

    @Test void placeSnake_setsSnakeInField() {
        GameField f = new GameField(new Dimension2D(10, 10));
        SimpleSpawner s = new SimpleSpawner(f);
        s.placeSnake();
        assertNotNull(f.getSnake());
    }

    @Test void placeSnake_zigzag_setsZigzagInField() {
        GameField f = new GameField(new Dimension2D(10, 10));
        SimpleSpawner s = new SimpleSpawner(f, new ZigZagSnakeFactory());
        s.placeSnake();
        assertInstanceOf(ZigZagSnake.class, f.getSnake());
    }
    /**  центр поля занят берём любые свободные */
    @Test void placeSnake_fallbackToAnyCell_whenCenterFull() {

        GameField f = new GameField(new Dimension2D(5, 5));

        GameField tiny = new GameField(new Dimension2D(6, 6));

        for (int y = 3; y < 3; y++)
            for (int x = 3; x < 3; x++) {
                Cell c = tiny.getCell(x, y);
                if (c != null) c.putUnit(new Rock());
            }
        SimpleSpawner s = new SimpleSpawner(tiny);
        s.placeSnake();

        assertNotNull(tiny.getSnake());
    }

    /** когда поле полностью занято  */
    @Test void spawnRodent_fieldFull_doesNotThrow() {
        // Заполняем всё поле камнями
        GameField f = new GameField(new Dimension2D(4, 4));
        for (Cell c : f) c.putUnit(new Rock());
        SimpleSpawner s = new SimpleSpawner(f);
        assertDoesNotThrow(() -> s.spawnRodent());
    }

    /** placeSnake когда нет трёх свободных клеток подряд  */
    @Test void placeSnake_noThreeInLine_returnsThis() {
        // Заполняем поле так что нет трёх подряд свободных
        GameField f = new GameField(new Dimension2D(4, 4));

        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x += 2)
                f.getCell(x, y).putUnit(new Rock());
        SimpleSpawner s = new SimpleSpawner(f);

        assertDoesNotThrow(() -> s.placeSnake());
    }
}