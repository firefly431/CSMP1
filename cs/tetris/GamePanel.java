/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

import java.awt.*;
import java.awt.event.*;

import cs.tetris.geom.Point;
import javax.swing.Timer;

/**
 *
 * @author s506571
 */
public class GamePanel extends StatePanel implements ActionListener {
    public static final int BOARD_PIXEL_WIDTH = Board.BOARD_WIDTH * Board.PIECE_SIZE;
    public static final int BOARD_PIXEL_HEIGHT = Board.BOARD_HEIGHT * Board.PIECE_SIZE;
    public static final int BOARD_X = (GameFrame.WINDOW_WIDTH - BOARD_PIXEL_WIDTH) / 2;
    public static final int BOARD_Y = GameFrame.WINDOW_HEIGHT - BOARD_PIXEL_HEIGHT;

    public static final Color BACKGROUND_COLOR = new Color(186, 186, 186);
    public static final Color EMPTY_COLOR = new Color(128, 128, 128);

    private Board board;
    private Piece piece, next;

    private boolean running = true;
    private Timer timer;

    public static final int DROP_DELAY = 300;

    public GamePanel() {
        board = new Board();
        timer = new Timer(DROP_DELAY, this);
        timer.start();
        generateNext();
        replace();
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
                if (minx >= 0)
                    if (board.get(x.x + piece.position.x, x.y + piece.position.y) > -1) {
                        piece.position.x++;
                        break;
                    }
            }
            piece.position.x -= minx;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            piece.position.x++;
            int maxx = Board.BOARD_WIDTH - 1;
            for (Point x : piece.coords) {
                if (x.x + piece.position.x > maxx)
                    maxx = x.x + piece.position.x;
                if (maxx <= Board.BOARD_WIDTH - 1)
                    if (board.get(x.x + piece.position.x, x.y + piece.position.y) > -1) {
                        piece.position.x--;
                        break;
                    }
            }
            piece.position.x -= (maxx - Board.BOARD_WIDTH + 1);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            movePieceDown();
        }
        if (e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_X) {
            if (e.getKeyCode() == KeyEvent.VK_Z)
                piece.rotateCounterClockwise();
            else
                piece.rotateClockwise();
            // keep within bounds
            int maxx = Board.BOARD_WIDTH - 1;
            int minx = 0;
            for (Point x : piece.coords) {
                if (x.x + piece.position.x > maxx)
                    maxx = x.x + piece.position.x;
                if (x.x + piece.position.x < minx)
                    minx = x.x + piece.position.x;
            }
            piece.position.x -= (maxx - Board.BOARD_WIDTH + 1) + minx;
            while (true) {
                boolean move = false;
                for (Point x : piece.coords) {
                    int tx = x.x + piece.position.x;
                    int ty = x.y + piece.position.y;
                    if (board.get(tx, ty) > -1) {
                        move = true;
                        piece.position.y--;
                        break;
                    }
                }
                if (!move)
                    break;
            }
        }
        repaint();
    }

    @Override
    public void removed() {
        running = false;
    }

    public void actionPerformed(ActionEvent e) {
        movePieceDown();
        repaint();
    }

    protected void movePieceDown() {
        boolean drop = false;
        for (Point x : piece.coords) {
            int tx = x.x + piece.position.x;
            int ty = x.y + piece.position.y + 1;
            if (ty >= Board.BOARD_HEIGHT || board.get(tx, ty) > -1) {
                drop = true;
                break;
            }
        }
        if (drop) {
            for (Point x : piece.coords) {
                int tx = x.x + piece.position.x;
                int ty = x.y + piece.position.y;
                if (ty < 0) {
                    // GAME OVER
                    System.out.println("YOU LOSE SUCKER!");
                    GameFrame.get().transition(new MainMenu());
                    return;
                }
                board.set(tx, ty, piece.index);
            }
            // replace piece
            replace();
        } else {
            piece.position.y++;
        }
    }

    protected void generateNext() {
        next = new Piece((int)(Math.random() * Piece.PIECE_NUM));
    }

    protected void replace() {
        piece = next;
        piece.position.set(Board.BOARD_WIDTH / 2, 0);
        generateNext();
    }
}
