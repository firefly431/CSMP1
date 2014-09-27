/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author s506571
 */
public class MainMenu extends StatePanel {
    public static final int HALF_X = GameFrame.WINDOW_WIDTH / 2;
    public static final int HALF_Y = GameFrame.WINDOW_HEIGHT / 2;
    public static final int TITLE_Y = HALF_Y - 80;
    public static final int ARW_TOP = HALF_Y + 12;
    public static final int ARW_BOT = HALF_Y + 22;
    public static final int ARW_MID = (ARW_BOT + ARW_TOP) / 2;
    public static final int ARW_D = 26;
    public static final int ARW_S = ARW_BOT - ARW_TOP;
    public static final int ARW_XS = ARW_S / 2;
    public static final int ARW_Y[] = {ARW_TOP, ARW_MID, ARW_BOT};
    public static final int ARW1_X[] = {HALF_X - ARW_D, HALF_X - ARW_D - ARW_XS, HALF_X - ARW_D};
    public static final int ARW2_X[] = {HALF_X + ARW_D, HALF_X + ARW_D + ARW_XS, HALF_X + ARW_D};

    private int level = 1;
    private int selected = 0;
    
    public MainMenu() {
        super();
    }

    public static void init() {
        // nothing really
    }

    @Override
    public void paint(Graphics g) {
        // anti-alias text
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        // draw background
        g.setColor(GamePanel.BACKGROUNDS[0]);
        g.fillRect(0, 0, GameFrame.WINDOW_WIDTH, GameFrame.WINDOW_HEIGHT);
        g.setFont(GameFrame.PLAY_TITLE);
        FontMetrics mt = g.getFontMetrics();
        g.setColor(GamePanel.TEXT_COLOR);
        drawCenter(g, "TETRIS", mt, TITLE_Y);
        g.setFont(GameFrame.PLAY_BODY);
        mt = g.getFontMetrics();
        g.setColor(selected == 0 ? GamePanel.TEXT_COLOR : GamePanel.DISABLED_COLOR);
        drawCenter(g, "PLAY LEVEL", mt, HALF_Y);
        drawCenter(g, "" + level, mt, HALF_Y + 25);
        g.fillPolygon(ARW1_X, ARW_Y, 3);
        g.fillPolygon(ARW2_X, ARW_Y, 3);
        g.setColor(selected == 1 ? GamePanel.TEXT_COLOR : GamePanel.DISABLED_COLOR);
        drawCenter(g, "CONTROLS", mt, HALF_Y + 70);
        g.setFont(GameFrame.PLAY_SMALL);
        mt = g.getFontMetrics();
        g.setColor(GamePanel.DISABLED_COLOR);
        drawCenter(g, "UP/DOWN TO MOVE CURSOR", mt, GameFrame.WINDOW_HEIGHT - 48);
        drawCenter(g, "LEFT/RIGHT TO CHANGE VALUE", mt, GameFrame.WINDOW_HEIGHT - 32);
        drawCenter(g, "ENTER TO SELECT", mt, GameFrame.WINDOW_HEIGHT - 16);
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (selected == 0)
                    level = (level + 18) % 20 + 1;
                break;
            case KeyEvent.VK_RIGHT:
                if (selected == 0)
                    level = level % 20 + 1;
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                // these two do the same in mod 2
                selected = 1 - selected;
                break;
            case KeyEvent.VK_ENTER:
                if (selected == 0) {
                    Sounds.Sound.WOOO.play();
                    GameFrame.get().transition(new GamePanel(level));
                }
                if (selected == 1) {
                    GameFrame.get().transition(new ControlsScreen());
                }
                break;
        }
        repaint();
    }

    protected void drawCenter(Graphics g, String s, FontMetrics mt, int y) {
        g.drawString(s, HALF_X - mt.stringWidth(s) / 2, y);
    }
}
