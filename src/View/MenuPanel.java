package View;

import Model.units.SnakeFactory;
import Model.units.NormalSnakeFactory;
import Model.units.ZigZagSnakeFactory;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Экран меню с выбором типа змеи.

 */
public class MenuPanel extends JPanel {

    private static final Color BG     = new Color(10, 14, 22);
    private static final Color ACCENT = new Color(60, 220, 120);
    private static final Color DIM    = new Color(80, 100, 120);
    private static final Color RED    = new Color(220, 60, 80);
    private static final Color GOLD   = new Color(220, 180, 60);
    private static final Color SEL_BG = new Color(20, 35, 50);
    private static final Color SEL_ON = new Color(60, 220, 120);

    /** onStart получает выбранную фабрику змеи. */
    private final Consumer<SnakeFactory> onStart;

    private final JLabel   titleLabel;
    private final JLabel   line1Label;
    private final JLabel   line2Label;
    private final JLabel   recordLabel;
    private final JButton  startBtn;

    // Выбор типа змеи
    private final JToggleButton btnNormal;
    private final JToggleButton btnZigzag;
    private final ButtonGroup   snakeTypeGroup = new ButtonGroup();

    public MenuPanel(Consumer<SnakeFactory> onStart) {
        this.onStart = onStart;
        setBackground(BG);
        setLayout(new GridBagLayout());

        titleLabel  = makeLabel("SNAKE",  48, Font.BOLD,  ACCENT);
        line1Label  = makeLabel("",       13, Font.PLAIN, DIM);
        line2Label  = makeLabel("",       13, Font.PLAIN, DIM);
        recordLabel = makeLabel("",       17, Font.BOLD,  GOLD);
        startBtn    = createActionButton("▶  НАЧАТЬ ИГРУ");

        btnNormal = createTypeButton("  Обычная",   "Тело повторяет траекторию головы");
        btnZigzag = createTypeButton("  Зигзаг",    "Тело движется зигзагообразно");
        btnNormal.setSelected(true); // по умолчанию обычная

        snakeTypeGroup.add(btnNormal);
        snakeTypeGroup.add(btnZigzag);

        startBtn.addActionListener(e -> onStart.accept(selectedFactory()));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill   = GridBagConstraints.NONE;

        c.gridy = 0; c.insets = new Insets(0,  0, 12, 0); add(titleLabel,  c);
        c.gridy = 1; c.insets = new Insets(0,  0,  4, 0); add(line1Label,  c);
        c.gridy = 2; c.insets = new Insets(0,  0, 16, 0); add(line2Label,  c);
        c.gridy = 3; c.insets = new Insets(0,  0,  6, 0); add(recordLabel, c);

        // Выбор типа змеи — два кнопки рядом
        c.gridy = 4; c.gridwidth = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 12, 6);  c.gridx = 0; add(btnNormal, c);
        c.insets = new Insets(0, 6, 12, 0);  c.gridx = 1; add(btnZigzag, c);

        c.gridy = 5; c.gridx = 0; c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 0, 0, 0); add(startBtn, c);

        showMenu();
    }

    public void showGameOver(int score, int best) {
        titleLabel.setForeground(RED);
        titleLabel.setText("GAME OVER");
        line1Label.setText("SCORE:  " + String.format("%04d", score));
        line1Label.setForeground(ACCENT);
        line2Label.setText("BEST:   " + String.format("%04d", best));
        line2Label.setForeground(DIM);
        recordLabel.setText(score > 0 && score >= best ? " НОВЫЙ РЕКОРД! " : " ");
        startBtn.setText("   ИГРАТЬ СНОВА");
    }

    public void showMenu() {
        titleLabel.setForeground(ACCENT);
        titleLabel.setText("SNAKE");
//        line1Label.setText("Управление: WASD или стрелки");
//        line1Label.setForeground(DIM);
//        line2Label.setText("Ешь грызунов. Избегай камней. Выживи.");
//        line2Label.setForeground(DIM);
        recordLabel.setText(" ");
        startBtn.setText(" НАЧАТЬ ИГРУ");
    }

    /** Вернуть фабрику соответственно выбранной кнопке. */
    private SnakeFactory selectedFactory() {
        return btnZigzag.isSelected()
                ? new ZigZagSnakeFactory()
                : new NormalSnakeFactory();
    }



    private JLabel makeLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Monospaced", style, size));
        lbl.setForeground(color);
        lbl.setOpaque(false);
        return lbl;
    }

    /** Кнопка выбора типа змеи с описанием при наведении. */
    private JToggleButton createTypeButton(String text, String tooltip) {
        JToggleButton btn = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = isSelected();
                g2.setColor(sel ? SEL_ON : SEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(sel ? new Color(10, 14, 22) : DIM);
                g2.setFont(new Font("Monospaced", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                // рамка
                g2.setColor(sel ? SEL_ON : new Color(40, 60, 80));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            }
        };
        btn.setPreferredSize(new Dimension(160, 44));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setToolTipText(tooltip);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if      (getModel().isPressed())   g2.setColor(new Color(40, 160, 90));
                else if (getModel().isRollover())  g2.setColor(new Color(50, 200, 110));
                else                               g2.setColor(ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(10, 14, 22));
                g2.setFont(new Font("Monospaced", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setPreferredSize(new Dimension(340, 46));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}