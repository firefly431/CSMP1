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
    public static final int TITLE_Y = 80;
    
    public MainMenu() {
        super();
    }

    public static void init() {
        // nothing really
    }

    @Override
    public void paint(Graphics g) {
        g.setFont(GameFrame.PLAY_48);
        FontMetrics m48 = g.getFontMetrics();
        g.drawString("TETRIS", HALF_X - m48.stringWidth("TETRIS"), TITLE_Y);
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            GameFrame.get().transition(new GamePanel());
            Sounds.Sound.WOOO.play();
        }
        if (e.getKeyCode() == KeyEvent.VK_C) {
            GameFrame.get().transition(new ControlsScreen());
            Sounds.Sound.WOOO.play();
        }
    }
}
