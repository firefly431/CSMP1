package cs.tetris;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;

/**
 * Main menu screen
 */
public class MainMenu extends StatePanel implements ActionListener {
    // useful constants
    public static final int HALF_X = GameFrame.WINDOW_WIDTH / 2;
    public static final int HALF_Y = GameFrame.WINDOW_HEIGHT / 2;
    public static final int TITLE_Y = HALF_Y - 80;
    // arrow positioning
    public static final int ARW_TOP = HALF_Y + 12;
    public static final int ARW_BOT = HALF_Y + 22;
    public static final int ARW_MID = (ARW_BOT + ARW_TOP) / 2;
    public static final int ARW_D = 26;
    public static final int ARW_S = ARW_BOT - ARW_TOP;
    public static final int ARW_XS = ARW_S / 2;
    public static final int ARW_Y[] = {ARW_TOP, ARW_MID, ARW_BOT};
    public static final int ARW1_X[] = {HALF_X - ARW_D, HALF_X - ARW_D - ARW_XS, HALF_X - ARW_D};
    public static final int ARW2_X[] = {HALF_X + ARW_D, HALF_X + ARW_D + ARW_XS, HALF_X + ARW_D};

    // delay for fall
    public static final int FALL_DELAY_MS = 100;
    // frames until new piece
    public static final int FRAMES_NEW = 5;
    public static final int MAX_OFFSET_X = (int)((double)HALF_X / Board.PIECE_SIZE + 0.5);

    // selected level
    private int level = 1;
    // selected choice
    private int selected = 0;

    // falling pieces
    private LinkedList<Piece> falling;
    private Timer fallTimer;
    private int new_frame;

    // is this the controls screen
    private boolean controlsS = false;
    
    public MainMenu() {
        super();
        falling = new LinkedList<Piece>();
        fallTimer = new Timer(FALL_DELAY_MS, this);
        fallTimer.setInitialDelay(0);
        fallTimer.start();
        new_frame = 0;
    }

    // timer event
    public void actionPerformed(ActionEvent e) {
        if (++new_frame >= FRAMES_NEW) {
            // new piece
            Piece p = new Piece((int)(Math.random() * Piece.PIECE_NUM));
            int nr = (int)(Math.random() * 4);
            for (int i = 0; i < nr; i++)
                p.rotateClockwise();
            p.position.set((int)(Math.random() * MAX_OFFSET_X * 2 - MAX_OFFSET_X), -3);
            falling.addFirst(p);
            new_frame -= FRAMES_NEW;
        }
        // move the pieces down
        ListIterator<Piece> it = falling.listIterator();
        while (it.hasNext()) {
            Piece p = it.next();
            p.position.y++;
            if ((-3 + p.position.y) * Board.PIECE_SIZE > GameFrame.WINDOW_HEIGHT) {
                it.remove();
            }
        }
        repaint();
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
        // draw falling pieces
        for (Piece p : falling) {
            GamePanel.drawPiece(g, p, HALF_X, 0);
        }
        if (controlsS) {
            g.setFont(GameFrame.PLAY_TITLE);
            g.setColor(GamePanel.TEXT_COLOR);
            FontMetrics mt = g.getFontMetrics();
            drawCenter(g, "CONTROLS", mt, 80);
            drawControls(g);
            return;
        }
        // draw text
        g.setFont(GameFrame.PLAY_TITLE);
        FontMetrics mt = g.getFontMetrics();
        g.setColor(GamePanel.TEXT_COLOR);
        drawCenter(g, "FALLING BLOCKS", mt, TITLE_Y);
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
        drawCenter(g, "UP/DOWN TO MOVE CURSOR", mt, GameFrame.WINDOW_HEIGHT - 74);
        drawCenter(g, "LEFT/RIGHT TO CHANGE VALUE", mt, GameFrame.WINDOW_HEIGHT - 58);
        drawCenter(g, "ENTER TO SELECT", mt, GameFrame.WINDOW_HEIGHT - 42);
        drawCenter(g, "COPYRIGHT \u00A9 2014 JAMES DONG AND THIENSON HO", mt, GameFrame.WINDOW_HEIGHT - 16);
    }

    public void keyPressed(KeyEvent e) {
        if (controlsS) {
            controlsS = false;
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (selected == 0)
                    level = (level + 18) % 20 + 1;
                // simplification of ((level - 1) + 19) % 20 + 1
                // we subtract and add 1 because it's 1-based
                // we add 19 instead of subtracting 1
                // so that we don't go negative
                break;
            case KeyEvent.VK_RIGHT:
                if (selected == 0)
                    level = level % 20 + 1;
                // similar to LEFT
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
                    controlsS = true;
                }
                break;
        }
        repaint();
    }

    // utility
    public static void drawCenter(Graphics g, String s, FontMetrics mt, int y) {
        g.drawString(s, HALF_X - mt.stringWidth(s) / 2, y);
    }

    // controls strings
    public static final String controls[][] = {
        {"Arrow Keys", "Move Piece"},
        {"Space", "Hard Drop"},
        {"Z", "Rotate Clockwise"},
        {"X", "Rotate Counterclockwise"},
        {"C", "Hold Piece"},
        {"P", "Pause"},
    };

    // draw some controls
    public static void drawControls(Graphics g) {
        g.setFont(GameFrame.PLAY_BODY);
        g.setColor(GamePanel.TEXT_COLOR);
        int y = 180;
        FontMetrics fm = g.getFontMetrics();
        for (String[] table : controls) {
            g.drawString(table[0], 40, y);
            g.drawString(table[1], GameFrame.WINDOW_WIDTH - 40 - fm.stringWidth(table[1]), y);
            y += 30;
        }
    }
}
