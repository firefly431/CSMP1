/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 *
 * @author s506571
 */
public abstract class StatePanel extends JPanel implements KeyListener {
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GameFrame.WINDOW_WIDTH, GameFrame.WINDOW_HEIGHT);
    }

    @Override
    public void paint(Graphics g) {}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}
}
