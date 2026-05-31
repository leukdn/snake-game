package View;

import javax.swing.*;
import java.awt.*;

public class HudPanel extends JPanel {

    private static final Color BG        = new Color(10, 14, 22);
    private static final Color ACCENT    = new Color(60, 220, 120);
    private static final Color DIM       = new Color(80, 100, 120);
    private static final Color HEART_ON  = new Color(220, 60, 80);
    private static final Color HEART_OFF = new Color(50, 50, 60);
    private static final Color GOLD      = new Color(220, 180, 60);

    private static final int HEART_SIZE  = 14;
    private static final int SECTION_GAP = 24;
    private static final int LABEL_OFF   = -13;

    private int lives    = 0;
    private int maxLives = 1;
    private int score    = 0;
    private int best     = 0;
    private int steps    = 0;
    private int k        = 20;

    public HudPanel() {
        setBackground(BG);
        setPreferredSize(new Dimension(0, 56));
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(30, 45, 60)));
    }

    public void setMaxLives(int max) {
        this.maxLives = Math.max(1, max);
        repaint();
    }

    public void updateStats(int score, int lives, int steps, int k) {
        this.score = score;
        if (score > best) best = score;
        this.lives = Math.max(0, lives);
        this.steps = Math.max(0, Math.min(steps, k));
        this.k     = Math.max(1, k);
        repaint();
    }

    public int getLives() { return lives; }
    public int getSteps() { return steps; }
    public int getK()     { return k; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cy = getHeight() / 2 + 5;
        int x  = 14;

        // жизни
        x = label(g2, "LIVES", x, cy);
        g2.setColor(lives > 0 ? HEART_ON : HEART_OFF);
        drawHeart(g2, x, cy - HEART_SIZE + 2, HEART_SIZE);
        x += HEART_SIZE + 5;
        g2.setColor(lives > 0 ? HEART_ON : DIM);
        g2.setFont(mono(16, Font.BOLD));
        String livesStr = String.valueOf(lives);
        g2.drawString(livesStr, x, cy + 1);
        x += g2.getFontMetrics().stringWidth(livesStr) + SECTION_GAP;


        x = label(g2, "SCORE", x, cy);
        g2.setColor(Color.WHITE);
        g2.setFont(mono(16, Font.BOLD));
        String sc = String.format("%04d", score);
        g2.drawString(sc, x, cy + 1);
        x += g2.getFontMetrics().stringWidth(sc) + SECTION_GAP;

        //счет лчший
        x = label(g2, "BEST", x, cy);
        g2.setColor(GOLD);
        g2.setFont(mono(16, Font.BOLD));
        String bs = String.format("%04d", best);
        g2.drawString(bs, x, cy + 1);
        x += g2.getFontMetrics().stringWidth(bs) + SECTION_GAP;

        // отсчет шаги
        x = label(g2, "DIGEST", x, cy);
        int remaining = k - steps;
        g2.setColor(remaining <= 3 ? HEART_ON : ACCENT);
        g2.setFont(mono(16, Font.BOLD));
        g2.drawString(String.valueOf(remaining), x, cy + 1);
    }

    private int label(Graphics2D g2, String text, int x, int cy) {
        g2.setColor(DIM);
        g2.setFont(mono(9, Font.PLAIN));
        g2.drawString(text, x, cy + LABEL_OFF);
        return x;
    }

    private void drawHeart(Graphics2D g2, int x, int y, int size) {
        int half = size / 2;
        g2.fillOval(x, y, half + 1, half + 1);
        g2.fillOval(x + half, y, half + 1, half + 1);
        int[] px = {x, x + size + 1, x + size / 2};
        int[] py = {y + half, y + half, y + size};
        g2.fillPolygon(px, py, 3);
    }

    private Font mono(int size, int style) {
        return new Font("Monospaced", style, size);
    }
}