package View;

import Model.GameListener;
import Model.Spawners.SimpleSpawner;
import Model.gamefield.*;
import Model.units.AbstractSnake;
import Model.units.SnakeFactory;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private static final String CARD_MENU = "MENU";
    private static final String CARD_GAME = "GAME";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     root       = new JPanel(cardLayout);

    private Game          game;
    private GameField     field;
    private GameFieldView fieldView;
    private HudPanel      hudPanel;
    private GameTimer     gameTimer;
    private MenuPanel     menuPanel;
    private int           bestScore = 0;

    private final JPanel gamePanel = new JPanel(new BorderLayout());

    public GameFrame() {
        setTitle("SNAKE");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        hudPanel  = new HudPanel();
        menuPanel = new MenuPanel(this::startGame);

        Dimension2D size = new Dimension2D(20, 20);
        field     = new GameField(size);
        fieldView = new GameFieldView(field);

        gamePanel.setBackground(new Color(10, 14, 22));
        gamePanel.add(hudPanel,  BorderLayout.NORTH);
        gamePanel.add(fieldView, BorderLayout.CENTER);

        root.add(menuPanel, CARD_MENU);
        root.add(gamePanel, CARD_GAME);

        add(root);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        cardLayout.show(root, CARD_MENU);
    }

    private void startGame(SnakeFactory factory) {
        if (gameTimer != null) gameTimer.stop();

        Dimension2D size = new Dimension2D(20, 20);
        field = new GameField(size);
        game  = new Game(field, new SimpleSpawner(field, factory));

        gamePanel.remove(fieldView);
        fieldView = new GameFieldView(field);
        gamePanel.add(fieldView, BorderLayout.CENTER);
        gamePanel.revalidate();

        removeKeyListeners();
        addKeyListener(new Controller(game));

        int[] maxLives = {0};
        game.addViewListener(new GameListener() {
            @Override public void fieldChanged()        { fieldView.repaint(); }
            @Override public void gameIsOver(boolean w) {
                gameTimer.stop();
                SwingUtilities.invokeLater(() -> {
                    menuPanel.showGameOver(game.getScore(), bestScore);
                    cardLayout.show(root, CARD_MENU);
                });
            }
            @Override public void scoreChanged(int s) {
                if (s > bestScore) bestScore = s;
                updateHud();
            }
            @Override public void livesChanged(int l) {
                if (maxLives[0] == 0) maxLives[0] = l;
                hudPanel.setMaxLives(maxLives[0]);
                updateHud();
            }
            @Override public void stepsChanged(int s, int k) { updateHud(); }
        });

        game.start();
        gameTimer = new GameTimer(game);
        gameTimer.start();

        cardLayout.show(root, CARD_GAME);
        requestFocusInWindow();
    }

    private void updateHud() {
        AbstractSnake s = game.getGameField().getSnake();
        if (s != null)
            hudPanel.updateStats(game.getScore(), s.getLives(),
                    s.getStepsAfterEat(), s.getK());
    }

    private void removeKeyListeners() {
        for (var kl : getKeyListeners()) removeKeyListener(kl);
    }
}