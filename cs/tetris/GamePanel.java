/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author s506571
 */
public class GamePanel extends JPanel implements KeyListener {
    public static int BOARD_PIXEL_WIDTH = Board.BOARD_WIDTH * Board.PIECE_SIZE;
    public static int BOARD_PIXEL_HEIGHT = Board.BOARD_HEIGHT * Board.PIECE_SIZE;
    public static int BOARD_X = (GameFrame.WINDOW_WIDTH - BOARD_PIXEL_WIDTH) / 2;
    public static int BOARD_Y = (GameFrame.WINDOW_HEIGHT - BOARD_PIXEL_HEIGHT) / 2;

    public void paint(Graphics g) {
        for (int y = 0; y < Board.BOARD_HEIGHT; y++) {
            //
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        // here's where we would handle key events
    }

    public void keyReleased(KeyEvent e) {
    }
}
