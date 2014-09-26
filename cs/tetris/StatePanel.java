/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author s506571
 */
public class StatePanel extends JPanel implements KeyListener, MouseListener {
    public StatePanel() {
        addMouseListener(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GameFrame.WINDOW_WIDTH, GameFrame.WINDOW_HEIGHT);
    }

    @Override
    public void paint(Graphics g) {}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}

    public void removed() {
        removeMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}
