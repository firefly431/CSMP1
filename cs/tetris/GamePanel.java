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
public class GamePanel extends StatePanel {
    public static int BOARD_PIXEL_WIDTH = Board.BOARD_WIDTH * Board.PIECE_SIZE;
    public static int BOARD_PIXEL_HEIGHT = Board.BOARD_HEIGHT * Board.PIECE_SIZE;
    public static int BOARD_X = (GameFrame.WINDOW_WIDTH - BOARD_PIXEL_WIDTH) / 2;
    public static int BOARD_Y = GameFrame.WINDOW_HEIGHT - BOARD_PIXEL_HEIGHT;

    private Board board;

    public GamePanel() {
        board = new Board();
    }

    public GamePanel(Board b) {
        board = b;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, GameFrame.WINDOW_WIDTH, GameFrame.WINDOW_HEIGHT);
        for (int y = 0; y < Board.BOARD_HEIGHT; y++) {
            for (int x = 0; x < Board.BOARD_WIDTH; x++) {
                int px = x * Board.PIECE_SIZE + BOARD_X;
                int py = y * Board.PIECE_SIZE + BOARD_Y;
                int p = board.get(x, y);
                if (p == -1)
                    g.setColor(Color.BLACK);
                else
                    g.setColor(Piece.piece_colors[p]);
                g.fillRect(px, py, Board.PIECE_SIZE - 1, Board.PIECE_SIZE - 1);
            }
        }
    }
}
