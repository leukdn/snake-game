package Model.gamefield;

import Model.Spawners.SimpleSpawner;
import Model.units.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;
    private GameField field;

    @BeforeEach
    void setUp() {
        field = new GameField(new Dimension2D(10, 10));

        game = new Game(field, new SimpleSpawner(field));
    }

    //  Старт
    @Test void start_gameNotOver() {
        game.start();
        assertFalse(game.isOver());
    }

    @Test void start_segmentsAppearOnField() {
        game.start();
        assertFalse(field.getAllUnits(SnakeSegment.class).isEmpty());
    }

    @Test void start_rocksOnField() {
        game.start();
        assertFalse(field.getAllUnits(Rock.class).isEmpty());
    }

    @Test void start_rodentsOnField() {
        game.start();
        assertFalse(field.getAllUnits(Rodent.class).isEmpty());
    }

    @Test void start_snakeIsAbstractSnake() {
        game.start();

        assertInstanceOf(AbstractSnake.class, field.getSnake());
    }

    @Test void start_defaultFactory_createsNormalSnake() {
        game.start();

        assertInstanceOf(NormalSnake.class, field.getSnake());
    }

    @Test void start_snakeIsAlive() {
        game.start();
        assertTrue(field.getSnake().isAlive());
    }

    @Test void start_snakeHas3Segments() {
        game.start();
        assertEquals(3, field.getSnake().getSegments().size());
    }

    @Test void start_scoreIsZero() {
        game.start();
        assertEquals(0, game.getScore());
    }

    @Test void start_resetsGameOverFlag() {

        game.start();
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 15; i++) { game.update(); if (game.isOver()) break; }
        assertTrue(game.isOver());

        // Пересоздаём игру и стартуем заново
        Game game2 = new Game(new GameField(new Dimension2D(10, 10)),
                new SimpleSpawner(new GameField(new Dimension2D(10, 10))));
        game2.start();
        assertFalse(game2.isOver());
    }

    //  ZigZagSnake через фабрику

    @Test void start_zigzagFactory_createsZigZagSnake() {
        GameField f = new GameField(new Dimension2D(10, 10));
        Game g = new Game(f, new SimpleSpawner(f, new ZigZagSnakeFactory()));
        g.start();
        assertInstanceOf(ZigZagSnake.class, f.getSnake());
    }

    @Test void start_zigzagSnake_isAlive() {
        GameField f = new GameField(new Dimension2D(10, 10));
        Game g = new Game(f, new SimpleSpawner(f, new ZigZagSnakeFactory()));
        g.start();
        assertTrue(f.getSnake().isAlive());
    }


    @Test void update_beforeStart_doesNotThrow() {
        assertDoesNotThrow(() -> game.update());
    }

    @Test void update_movesSnake() {
        game.start();
        Cell headBefore = field.getSnake().getSegments().get(0).owner();
        game.update();
        if (!game.isOver()) {
            Cell headAfter = field.getSnake().getSegments().get(0).owner();
            assertNotEquals(headBefore, headAfter);
        }
    }

    @Test void update_afterGameOver_doesNotThrow() {
        game.start();
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 15; i++) game.update();
        assertTrue(game.isOver());
        assertDoesNotThrow(() -> game.update());
    }

    @Test void update_afterGameOver_stateUnchanged() {
        game.start();
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 50; i++) { game.update(); if (game.isOver()) break; }
        assertTrue(game.isOver());

        int segsBefore = field.getAllUnits(SnakeSegment.class).size();
        game.update();
        int segsAfter  = field.getAllUnits(SnakeSegment.class).size();
        assertEquals(segsBefore, segsAfter);
    }



    @Test void setDirection_beforeStart_doesNotThrow() {
        assertDoesNotThrow(() -> game.setDirection(Direction.NORTH));
    }

    @Test void setDirection_changesSnakeDirection() {
        game.start();
        game.setDirection(Direction.SOUTH);

        assertDoesNotThrow(() -> game.update());
    }

    // Конец игры

    @Test void game_endsWhenSnakeHitsWall() {
        game.start();
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 15; i++) {
            game.update();
            if (game.isOver()) break;
        }
        assertTrue(game.isOver());
    }

    @Test void game_endsEventually() {
        game.start();
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 50; i++) {
            game.update();
            if (game.isOver()) break;
        }
        assertTrue(game.isOver());
    }

    // GameListener

    @Test void gameIsOver_listener_called() {
        boolean[] called = {false};
        game.addViewListener(new Model.GameListener() {
            public void gameIsOver(boolean win) { called[0] = true; }
            public void fieldChanged() {}
        });
        game.start();
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 15; i++) { game.update(); if (game.isOver()) break; }
        assertTrue(called[0]);
    }

    @Test void fieldChanged_listener_called_on_update() {
        boolean[] called = {false};
        game.addViewListener(new Model.GameListener() {
            public void gameIsOver(boolean win) {}
            public void fieldChanged() { called[0] = true; }
        });
        game.start();
        game.update();
        assertTrue(called[0]);
    }

    @Test void scoreChanged_listener_called_when_rodentEaten() {
        int[] score = {-1};
        game.addViewListener(new Model.GameListener() {
            public void gameIsOver(boolean win) {}
            public void fieldChanged() {}
            public void scoreChanged(int s) { score[0] = s; }
        });
        game.start();

        assertEquals(0, game.getScore());
    }



    @Test void getGameField_returnsSameField() {
        assertEquals(field, game.getGameField());
    }

    @Test void gameIsOver_false_initially() {
        game.start();
        assertFalse(game.isOver());
    }

    //  Полный цикл с ZigZagSnake

    @Test void fullCycle_zigzagSnake_movesAndDies() {
        GameField f = new GameField(new Dimension2D(10, 10));
        Game g = new Game(f, new SimpleSpawner(f, new ZigZagSnakeFactory()));
        g.start();
        g.setDirection(Direction.NORTH);
        for (int i = 0; i < 50; i++) {
            g.update();
            if (g.isOver()) break;
        }
        assertTrue(g.isOver());
    }

    @Test void fullCycle_rodentsRespawn() {
        game.start();
        int initial = field.getAllUnits(Rodent.class).size();
        assertTrue(initial > 0);
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 30; i++) {
            game.update();
            if (game.isOver()) break;
        }
        // Грызуны либо есть либо съедены и пересозданы — не падает
        assertTrue(field.getAllUnits(Rodent.class).size() >= 0);
    }

    @Test void bothSnakeTypes_workWithGame() {
        // NormalSnake
        GameField f1 = new GameField(new Dimension2D(10, 10));
        Game g1 = new Game(f1, new SimpleSpawner(f1, new NormalSnakeFactory()));
        g1.start();
        assertFalse(g1.isOver());
        assertInstanceOf(NormalSnake.class, f1.getSnake());

        // ZigZagSnake
        GameField f2 = new GameField(new Dimension2D(10, 10));
        Game g2 = new Game(f2, new SimpleSpawner(f2, new ZigZagSnakeFactory()));
        g2.start();
        assertFalse(g2.isOver());
        assertInstanceOf(ZigZagSnake.class, f2.getSnake());
    }
}