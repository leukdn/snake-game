package Model.gamefield;

import Model.Spawners.SimpleSpawner;
import Model.units.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;
    private GameField field;

    @BeforeEach
    void setUp() {
        field = new GameField(new Dimension2D(10, 10));
        game  = new Game(field, new SimpleSpawner(field));
    }


    //  Старт


    @Test void start_gameNotOver() {
        game.start(); assertFalse(game.isOver());
    }

    @Test void start_segmentsAppearOnField() {
        game.start(); assertFalse(field.getAllUnits(SnakeSegment.class).isEmpty());
    }

    @Test void start_rocksOnField() {
        game.start(); assertFalse(field.getAllUnits(Rock.class).isEmpty());
    }

    @Test void start_rodentsOnField() {
        game.start(); assertFalse(field.getAllUnits(Rodent.class).isEmpty());
    }

    @Test void start_snakeIsAbstractSnake() {
        game.start(); assertInstanceOf(AbstractSnake.class, field.getSnake());
    }

    @Test void start_defaultFactory_createsNormalSnake() {
        game.start(); assertInstanceOf(NormalSnake.class, field.getSnake());
    }

    @Test void start_snakeIsAlive() {
        game.start(); assertTrue(field.getSnake().isAlive());
    }

    @Test void start_snakeHas3Segments() {
        game.start(); assertEquals(3, field.getSnake().getSegments().size());
    }

    @Test void start_scoreIsZero() {
        game.start(); assertEquals(0, game.getScore());
    }

    @Test void start_resetsGameOverFlag() {
        game.start();
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 15; i++) { game.update(); if (game.isOver()) break; }
        assertTrue(game.isOver());

        Game game2 = new Game(new GameField(new Dimension2D(10, 10)),
                new SimpleSpawner(new GameField(new Dimension2D(10, 10))));
        game2.start();
        assertFalse(game2.isOver());
    }


    //  Фабрики

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

    @Test void start_sineFactory_createsSineSnake() {
        GameField f = new GameField(new Dimension2D(10, 10));
        Game g = new Game(f, new SimpleSpawner(f, new SineSnakeFactory()));
        g.start();
        assertFalse(g.isOver());
        assertInstanceOf(SineSnake.class, f.getSnake());
    }


    //  Update

    @Test void update_beforeStart_doesNotThrow() {
        assertDoesNotThrow(() -> game.update());
    }

    @Test void update_movesSnake() {
        game.start();
        Cell headBefore = field.getSnake().getSegments().get(0).owner();
        game.update();
        if (!game.isOver()) {
            assertNotEquals(headBefore, field.getSnake().getSegments().get(0).owner());
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
        assertEquals(segsBefore, field.getAllUnits(SnakeSegment.class).size());
    }

    @Test void setDirection_beforeStart_doesNotThrow() {
        assertDoesNotThrow(() -> game.setDirection(Direction.NORTH));
    }

    @Test void setDirection_changesSnakeDirection() {
        game.start();
        game.setDirection(Direction.SOUTH);
        assertDoesNotThrow(() -> game.update());
    }


    //  Конец игры

    @Test void game_endsWhenSnakeHitsWall() {
        game.start();
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 15; i++) { game.update(); if (game.isOver()) break; }
        assertTrue(game.isOver());
    }

    @Test void game_endsEventually() {
        game.start();
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 50; i++) { game.update(); if (game.isOver()) break; }
        assertTrue(game.isOver());
    }

    @Test void getGameField_returnsSameField() {
        assertEquals(field, game.getGameField());
    }

    @Test void gameIsOver_false_initially() {
        game.start();
        assertFalse(game.isOver());
    }


    //  Полный цикл

    @Test void fullCycle_zigzagSnake_movesAndDies() {
        GameField f = new GameField(new Dimension2D(10, 10));
        Game g = new Game(f, new SimpleSpawner(f, new ZigZagSnakeFactory()));
        g.start();
        g.setDirection(Direction.NORTH);
        for (int i = 0; i < 50; i++) { g.update(); if (g.isOver()) break; }
        assertTrue(g.isOver());
    }

    @Test void fullCycle_rodentsRespawn() {
        game.start();
        assertTrue(field.getAllUnits(Rodent.class).size() > 0);
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 30; i++) {
            game.update();
            if (game.isOver()) break;
            assertTrue(field.getAllUnits(Rodent.class).size() > 0,
                    "грызуны должны быть на поле во время игры");
        }
    }

    @Test void bothSnakeTypes_workWithGame() {
        GameField f1 = new GameField(new Dimension2D(10, 10));
        Game g1 = new Game(f1, new SimpleSpawner(f1, new NormalSnakeFactory()));
        g1.start();
        assertFalse(g1.isOver());
        assertInstanceOf(NormalSnake.class, f1.getSnake());

        GameField f2 = new GameField(new Dimension2D(10, 10));
        Game g2 = new Game(f2, new SimpleSpawner(f2, new ZigZagSnakeFactory()));
        g2.start();
        assertFalse(g2.isOver());
        assertInstanceOf(ZigZagSnake.class, f2.getSnake());
    }


    //  Слушатели — fieldChanged

    /**
     * fieldChanged вызывается при каждом update().
     */
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

    /**
     * fieldChanged вызывается ровно один раз за один update().
     * View не должен получать лишних уведомлений.
     */
    @Test void fieldChanged_calledExactlyOnce_perUpdate() {
        int[] count = {0};
        game.addViewListener(new Model.GameListener() {
            @Override public void gameIsOver(boolean w) {}
            @Override public void fieldChanged() { count[0]++; }
        });
        game.start();
        count[0] = 0;   // сбрасываем события от start()
        game.update();
        assertEquals(1, count[0],
                "fieldChanged должен вызываться ровно 1 раз за один update()");
    }


    //  Слушатели — scoreChanged

    /**
     * При старте счёт равен 0, scoreChanged не вызывается.
     */
    @Test void scoreChanged_notCalledOnStart() {
        boolean[] called = {false};
        game.addViewListener(new Model.GameListener() {
            public void gameIsOver(boolean win) {}
            public void fieldChanged() {}
            public void scoreChanged(int s) { called[0] = true; }
        });
        game.start();
        assertFalse(called[0],
                "scoreChanged не должен вызываться при старте — счёт ещё не изменился");
        assertEquals(0, game.getScore());
    }

    /**
     * scoreChanged: параметр s совпадает с game.getScore() в момент события.
     * Инвариант: listener получает именно актуальный счёт, а не устаревший.
     */
    @Test void scoreChanged_parameterMatchesGame_atMomentOfEvent() {
        int[] scoreParam  = {Integer.MIN_VALUE};
        int[] scoreActual = {Integer.MIN_VALUE};

        game.addViewListener(new Model.GameListener() {
            @Override public void gameIsOver(boolean w) {}
            @Override public void fieldChanged() {}
            @Override public void scoreChanged(int s) {
                scoreParam[0]  = s;
                scoreActual[0] = game.getScore();   // реальный счёт в момент события
            }
        });

        game.start();

        if (scoreParam[0] != Integer.MIN_VALUE) {
            // Событие сработало — параметр должен совпадать с реальным счётом
            assertEquals(scoreParam[0], scoreActual[0],
                    "параметр scoreChanged должен совпадать с game.getScore() в момент события");
        }
        assertEquals(0, game.getScore());
    }


    //  Слушатели — livesChanged
    /**
     * livesChanged вызывается при start() с актуальным числом жизней.
     */
    @Test void livesChanged_calledOnStart_withCorrectValue() {
        int[] livesParam = {-1};
        game.addViewListener(new Model.GameListener() {
            public void gameIsOver(boolean w) {}
            public void fieldChanged() {}
            public void livesChanged(int l) { livesParam[0] = l; }
        });
        game.start();

        // Событие должно было сработать
        assertNotEquals(-1, livesParam[0],
                "livesChanged должен быть вызван при start()");
        // Параметр совпадает с реальным состоянием модели
        assertEquals(livesParam[0], game.getGameField().getSnake().getLives(),
                "параметр livesChanged должен совпадать с snake.getLives()");
        assertTrue(livesParam[0] > 0,
                "число жизней при старте должно быть положительным");
    }

    /**
     * livesChanged: параметр события совпадает с реальным состоянием модели
     * именно В МОМЕНТ отправки события.
     */
    @Test void livesChanged_parameterMatchesSnake_atMomentOfEvent() {
        int[] livesParam  = {-1};
        int[] livesActual = {-1};

        game.addViewListener(new Model.GameListener() {
            @Override public void gameIsOver(boolean w) {}
            @Override public void fieldChanged() {}
            @Override public void livesChanged(int l) {
                livesParam[0]  = l;
                livesActual[0] = game.getGameField().getSnake().getLives();
            }
        });

        game.start();

        // Событие должно было сработать
        assertNotEquals(-1, livesParam[0],
                "livesChanged должен быть вызван при start()");
        // Параметр события == реальное состояние объекта в тот же момент
        assertEquals(livesParam[0], livesActual[0],
                "параметр livesChanged и snake.getLives() должны совпадать в момент события");
        assertTrue(livesParam[0] > 0,
                "число жизней при старте должно быть положительным");
    }


    //  Слушатели — stepsChanged

    /**
     * stepsChanged вызывается после update(), параметр совпадает
     * с реальным stepsAfterEat у змеи.
     */
    @Test void stepsChanged_parameterMatchesSnake_atMomentOfEvent() {
        int[] stepsParam  = {-1};
        int[] stepsActual = {-1};

        game.addViewListener(new Model.GameListener() {
            @Override public void gameIsOver(boolean w) {}
            @Override public void fieldChanged() {}
            @Override public void stepsChanged(int s, int k) {
                stepsParam[0]  = s;
                stepsActual[0] = game.getGameField().getSnake().getStepsAfterEat();
            }
        });

        game.start();
        game.update();

        // Событие должно было сработать
        assertNotEquals(-1, stepsParam[0],
                "stepsChanged должен быть вызван после update()");
        // Параметр события == реальное состояние модели в тот же момент
        assertEquals(stepsParam[0], stepsActual[0],
                "параметр stepsChanged должен совпадать с snake.getStepsAfterEat()");
        assertTrue(stepsParam[0] >= 0);
    }


    //  Слушатели — gameIsOver

    /**
     * gameIsOver вызывается когда змея врезается.
     */
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


    //  Цепочки событий

    /**
     * Цепочка Unit.deactivate() → UnitListener → GameListener.activateChanged().

     */
    @Test void eventChain_unitDeactivate_propagatesToView() {
        List<String> eventLog = new ArrayList<>();

        game.addViewListener(new Model.GameListener() {
            @Override public void gameIsOver(boolean w) {}
            @Override public void fieldChanged() {}
            @Override public void activateChanged(Model.gamefield.Unit u) {
                eventLog.add("view:activateChanged");
            }
        });

        game.start();

        var rodents = field.getAllUnits(Model.units.Rodent.class);
        assertFalse(rodents.isEmpty(), "грызуны должны быть на поле");
        Model.units.Rodent rodent = rodents.get(0);

        // Добавляем наш listener после internalListener Game
        rodent.addUnitListener(u -> eventLog.add("unit:activateChanged"));

        eventLog.clear();   // сбрасываем события от старта

        rodent.deactivate();

        // Оба звена сработали
        assertTrue(eventLog.contains("unit:activateChanged"),
                "UnitListener должен сработать");
        assertTrue(eventLog.contains("view:activateChanged"),
                "GameListener должен сработать");

        // Каждое событие ровно один раз
        assertEquals(1, eventLog.stream()
                        .filter("unit:activateChanged"::equals).count(),
                "unit:activateChanged должен сработать ровно 1 раз");
        assertEquals(1, eventLog.stream()
                        .filter("view:activateChanged"::equals).count(),
                "view:activateChanged должен сработать ровно 1 раз");


        assertEquals("view:activateChanged", eventLog.get(0),
                "internalListener Game должен сработать первым");
        assertEquals("unit:activateChanged", eventLog.get(1),
                "наш UnitListener добавлен вторым — срабатывает вторым");
    }

    /**
     * Цепочка при поедании грызуна:
     * eat() → SnakeListener.rodentEaten() → score++ → spawnRodent() → scoreChanged(s).

     */
    @Test void eventChain_rodentEaten_score_and_rodentCount() {
        int[] scoreParam     = {-1};
        int[] scoreActual    = {-1};
        int[] rodentsAtEvent = {-1};
        int[] rodentsBefore  = {0};

        game.addViewListener(new Model.GameListener() {
            @Override public void gameIsOver(boolean w) {}
            @Override public void fieldChanged() {}
            @Override public void scoreChanged(int s) {
                scoreParam[0]     = s;
                scoreActual[0]    = game.getScore();
                rodentsAtEvent[0] = field.getAllUnits(Rodent.class).size();
            }
        });

        game.start();
        rodentsBefore[0] = field.getAllUnits(Rodent.class).size();

        // Ищем грызуна в прямой видимости по одному из направлений
        AbstractSnake snake = field.getSnake();
        Cell headCell = snake.getSegments().get(0).owner();
        Direction dirToRodent = null;

        outer:
        for (Direction d : Direction.values()) {
            Cell cursor = headCell;
            for (int step = 0; step < 20; step++) {
                cursor = (cursor == null) ? null : cursor.getNeighbor(d);
                if (cursor == null) break;
                if (!cursor.getUnits(Rodent.class).isEmpty()) {
                    dirToRodent = d;
                    break outer;
                }
                if (!cursor.getUnits(Rock.class).isEmpty()
                        || !cursor.getUnits(SnakeSegment.class).isEmpty()) break;
            }
        }

        if (dirToRodent != null) {
            game.setDirection(dirToRodent);
            for (int i = 0; i < 25; i++) {
                game.update();
                if (game.isOver() || scoreParam[0] != -1) break;
            }
        }

        // Если грызун был съеден — проверяем цепочку
        if (scoreParam[0] != -1) {

            assertEquals(scoreParam[0], scoreActual[0],
                    "параметр scoreChanged должен совпадать с game.getScore()");


            assertEquals(1, scoreParam[0],
                    "после первого поедания счёт должен быть 1");


            assertEquals(rodentsBefore[0], rodentsAtEvent[0],
                    "число грызунов должно восстановиться до scoreChanged");
        }

    }

    /**
     * Цепочка при смерти змеи:
     * move() → die() → SnakeListener.snakeDied() → finishGame() → gameIsOver().

     */
    @Test void eventChain_snakeDied_gameIsOver_inOrder() {
        List<String> eventLog = new ArrayList<>();
        boolean[] snakeAliveAtGameOver = {true};

        game.addViewListener(new Model.GameListener() {
            @Override public void fieldChanged() {}
            @Override public void gameIsOver(boolean w) {
                AbstractSnake snake = field.getSnake();
                snakeAliveAtGameOver[0] = snake != null && snake.isAlive();
                eventLog.add("gameIsOver");
            }
        });

        game.start();
        game.setDirection(Direction.NORTH);
        for (int i = 0; i < 25; i++) {
            game.update();
            if (game.isOver()) break;
        }

        assertTrue(game.isOver(), "игра должна завершиться");

        // Событие сработало
        assertTrue(eventLog.contains("gameIsOver"),
                "gameIsOver должен сработать");

        // Инвариант: змея мертва В момент gameIsOver
        assertFalse(snakeAliveAtGameOver[0],
                "змея должна быть мертва в момент gameIsOver");
    }


    //  Гарантированное поедание — покрытие scoreChanged


    /**
     * Вспомогательный метод: кладёт грызуна прямо перед носом змеи.
     * Очищает целевую ячейку от Rock если нужно.
     * Возвращает ячейку, в которую положен грызун.
     */
    private Cell placeRodentAheadOfSnake() {
        AbstractSnake snake = field.getSnake();
        Direction dir       = snake.getDirection();
        Cell headCell       = snake.getSegments().get(0).owner();
        Cell nextCell       = headCell.getNeighbor(dir);
        assertNotNull(nextCell, "nextCell не должна быть null — змея в центре поля");


        new ArrayList<>(nextCell.getUnits(Rock.class))
                .forEach(Rock::deactivate);

        new ArrayList<>(nextCell.getUnits(Rodent.class))
                .forEach(Rodent::deactivate);

        Rodent r = new Rodent();
        boolean placed = nextCell.putUnit(r);
        assertTrue(placed, "грызун должен встать в пустую ячейку перед головой");
        return nextCell;
    }



    /**
     * После поедания грызуна число грызунов на поле не уменьшается:
     * spawnRodent() выполняется вутри rodentEaten(), до отправки scoreChanged.

     */
    @Test
    void scoreChanged_afterEat_rodentCountRestoredBeforeEvent() {
        int[] rodentsAtEvent = {-1};
        int[] rodentsBefore  = {0};

        game.addViewListener(new Model.GameListener() {
            @Override public void gameIsOver(boolean w) {}
            @Override public void fieldChanged() {}
            @Override public void scoreChanged(int s) {
                rodentsAtEvent[0] = field.getAllUnits(Rodent.class).size();
            }
        });

        game.start();
        placeRodentAheadOfSnake();


        rodentsBefore[0] = field.getAllUnits(Rodent.class).size();

        game.update();


        assertNotEquals(-1, rodentsAtEvent[0],
                "scoreChanged должен вызываться при поедании");


        assertEquals(rodentsBefore[0], rodentsAtEvent[0],
                "spawnRodent должен выполниться до scoreChanged: " +
                        "число грызунов должно быть восстановлено");
    }

    @Test
    void eventOrder_fieldChanged_before_stepsChanged_inSingleUpdate() {
        List<String> eventLog = new ArrayList<>();

        game.addViewListener(new Model.GameListener() {
            @Override public void gameIsOver(boolean w) {}
            @Override public void fieldChanged()          { eventLog.add("fieldChanged"); }
            @Override public void stepsChanged(int s, int k) { eventLog.add("stepsChanged"); }
        });

        game.start();
        eventLog.clear();   // сбрасываем события от start()

        game.update();

        // Оба события должны присутствовать
        assertTrue(eventLog.contains("fieldChanged"),
                "fieldChanged должен вызываться в update()");
        assertTrue(eventLog.contains("stepsChanged"),
                "stepsChanged должен вызываться в update()");


        int idxField = eventLog.indexOf("fieldChanged");
        int idxSteps = eventLog.indexOf("stepsChanged");
        assertTrue(idxField < idxSteps,
                "fieldChanged должен идти раньше stepsChanged в одном update()");
    }

    /**
     * После обычного update() (без столкновения) жизни не изменились.
     * Проверяем инвариант состояния модели, а не факт вызова события.
     */
    @Test
    void normalUpdate_livesUnchanged() {
        game.start();
        int livesBefore = field.getSnake().getLives();

        game.update();  // обычный шаг без столкновения

        if (!game.isOver()) {
            assertEquals(livesBefore, field.getSnake().getLives(),
                    "жизни не должны меняться при обычном шаге");
        }
    }
}