/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

import java.awt.*;
import java.awt.event.*;

import cs.tetris.geom.Point;

/**
 *
 * @author s506571
 */
public class GamePanel extends StatePanel {
    public static int BOARD_PIXEL_WIDTH = Board.BOARD_WIDTH * Board.PIECE_SIZE;
    public static int BOARD_PIXEL_HEIGHT = Board.BOARD_HEIGHT * Board.PIECE_SIZE;
    public static int BOARD_X = (GameFrame.WINDOW_WIDTH - BOARD_PIXEL_WIDTH) / 2;
    public static int BOARD_Y = GameFrame.WINDOW_HEIGHT - BOARD_PIXEL_HEIGHT;

    public static Color BACKGROUND_COLOR = new Color(186, 186, 186);
    public static Color EMPTY_COLOR = new Color(128, 128, 128);

    private Board board;
    private Piece piece;

    public GamePanel() {
        board = new Board();
        piece = new Piece((int)(Math.random() * Piece.PIECE_NUM));
    }

    public GamePanel(Board b) {
        board = b;
    }

    protected void drawPiece(Graphics g, Piece p, int x, int y) {
        drawPiece(g, p, new Point(x, y));
    }

    protected void drawPiece(Graphics g, Piece p, Point origin) {
        g.setColor(Piece.piece_colors[p.index]);
        for (Point x : p.coords) {
            int px = origin.x + (p.position.x + x.x) * Board.PIECE_SIZE;
            int py = origin.y + (p.position.y + x.y) * Board.PIECE_SIZE;
            g.fillRect(px, py, Board.PIECE_SIZE - 1, Board.PIECE_SIZE - 1);
        }
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, GameFrame.WINDOW_WIDTH, GameFrame.WINDOW_HEIGHT);
        for (int y = 0; y < Board.BOARD_HEIGHT; y++) {
            for (int x = 0; x < Board.BOARD_WIDTH; x++) {
                int px = x * Board.PIECE_SIZE + BOARD_X;
                int py = y * Board.PIECE_SIZE + BOARD_Y;
                int p = board.get(x, y);
                if (p == -1)
                    g.setColor(EMPTY_COLOR);
                else
                    g.setColor(Piece.piece_colors[p]);
                g.fillRect(px, py, Board.PIECE_SIZE - 1, Board.PIECE_SIZE - 1);
            }
        }
        drawPiece(g, piece, BOARD_X, BOARD_Y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            piece.position.x--;
            int minx = 0;
            for (Point x : piece.coords) {
                if (x.x + piece.position.x < minx)
                    minx = x.x + piece.position.x;
            }
            piece.position.x -= minx;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            piece.position.x++;
            int maxx = Board.BOARD_WIDTH - 1;
            for (Point x : piece.coords) {
                if (x.x + piece.position.x > maxx)
                    maxx = x.x + piece.position.x;
            }
            piece.position.x -= (maxx - Board.BOARD_WIDTH + 1);
        }
        repaint();
    }
}
