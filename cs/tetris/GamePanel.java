/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

// TODO: MUSIC http://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html

package cs.tetris;

import java.awt.*;
import java.awt.event.*;

import cs.tetris.geom.Point;
import cs.util.RandomAccessFileInputStream;
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
    public static final int NEXT_LABEL_X = 20;
    public static final int NEXT_LABEL_Y = 40;
    public static final int NEXT_X = Board.PIECE_SIZE * 3 + NEXT_LABEL_X;
    public static final int NEXT_Y = Board.PIECE_SIZE * 3 + NEXT_LABEL_Y;
    public static final int HOLD_LABEL_X = 20;
    public static final int HOLD_LABEL_Y = NEXT_LABEL_Y + Board.PIECE_SIZE * 8;
    public static final int HOLD_X = Board.PIECE_SIZE * 3 + HOLD_LABEL_X;
    public static final int HOLD_Y = Board.PIECE_SIZE * 3 + HOLD_LABEL_Y;
    public static final int BOARD_RT_X = BOARD_X + BOARD_PIXEL_WIDTH;
    public static final int SCORE_PANEL_X = BOARD_RT_X + 20;
    public static final int SCORE_PANEL_Y = 40;

    private Color backgroundColor;
    public static final Color BACKGROUNDS[] = {
        new Color(96, 96, 96), new Color(102, 71, 71), new Color(71, 71, 102),
        new Color(71, 102, 71)
    };
    public static final Color EMPTY_COLOR = new Color(128, 128, 128);
    public static final Color TEXT_COLOR = new Color(255, 255, 255);

    private Board board;
    private Piece piece, next, hold, ghost;
    private boolean canHold;

    private Timer timer;
    private long lastMovement = 0;
    // used so that pieces will not drop until the player stops moving it

    private int score;
    private int level;
    private int linesCleared;
    private int linesLevel;
    public boolean pause = false;

    public static final int KEEP_ALIVE_NS = 800000000;

    private Music bg;

    public GamePanel() {
        this(new Board());
    }

    public GamePanel(Board b) {
        board = b;
        timer = new Timer(1000, this);
        timer.start();
        generateNext();
        replace();
        hold = null;
        canHold = true;
        score = linesCleared = linesLevel = 0;
        level = 1;
        try {
            bg = Music.createWithAmp(new RandomAccessFileInputStream("bg.wav"));
            bg.setAmplitude(0.1);
            bg.play_with_amplitude();
        } catch (Exception e) {
            bg = null;
        }
        setTimer(level);
        setColor(level);
    }

    protected void drawPiece(Graphics g, Piece p, int x, int y) {
        drawPiece(g, p, new Point(x, y));
    }

    protected void drawPiece(Graphics g, Piece p, Point origin) {
        if (p == null) return;
        g.setColor(Piece.piece_colors[p.index]);
        for (Point x : p.coords) {
            int px = origin.x + (p.position.x + x.x) * Board.PIECE_SIZE;
            int py = origin.y + (p.position.y + x.y) * Board.PIECE_SIZE;
            g.fillRect(px, py, Board.PIECE_SIZE - 1, Board.PIECE_SIZE - 1);
        }
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(backgroundColor);
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
        g.setColor(EMPTY_COLOR);
        g.fillRect(NEXT_X - Board.PIECE_SIZE * 2, NEXT_Y - Board.PIECE_SIZE * 2, Board.PIECE_SIZE * 5, Board.PIECE_SIZE * 5);
        g.fillRect(HOLD_X - Board.PIECE_SIZE * 2, HOLD_Y - Board.PIECE_SIZE * 2, Board.PIECE_SIZE * 5, Board.PIECE_SIZE * 5);
        drawPiece(g, piece, BOARD_X, BOARD_Y);
        drawPiece(g, next, NEXT_X, NEXT_Y);
        drawPiece(g, hold, HOLD_X, HOLD_Y);
        g.setColor(TEXT_COLOR);
        g.setFont(GameFrame.PLAY_BODY);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.drawString("NEXT PIECE", NEXT_LABEL_X, NEXT_LABEL_Y);
        g.drawString("HELD PIECE", HOLD_LABEL_X, HOLD_LABEL_Y);
        // TODO: score stuff on right
        g.drawString("SCORE: " + getScore(), SCORE_PANEL_X, SCORE_PANEL_Y);
        g.drawString("LEVEL: " + level, SCORE_PANEL_X, SCORE_PANEL_Y + 30);
        g.setFont(GameFrame.PLAY_SMALL);
        g.drawString("LINES CLEARED: " + linesCleared, SCORE_PANEL_X, SCORE_PANEL_Y + 60);
        g.drawString("LINES TO NEXT LEVEL: " + (10 - linesLevel), SCORE_PANEL_X, SCORE_PANEL_Y + 90);
        ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        drawPiece(g, ghost, BOARD_X, BOARD_Y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
                pause = !pause;
                if(pause)
                    timer.stop();
                else
                    timer.start();
            }
        if(!pause) {
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
                lastMovement = System.nanoTime();
                dropGhost();
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
                lastMovement = System.nanoTime();
                dropGhost();
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                movePieceDown();
            }
            if (e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_X) {
                if (e.getKeyCode() == KeyEvent.VK_Z)
                    piece.rotateCounterClockwise();
                else
                    piece.rotateClockwise();
                lastMovement = System.nanoTime();
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
                        if (board.get(tx, ty) > -1 || ty >= Board.BOARD_HEIGHT) {
                            move = true;
                            piece.position.y--;
                            break;
                        }
                    }
                    if (!move)
                        break;
                }
                dropGhost();
            }
            if (e.getKeyCode() == KeyEvent.VK_C) {
                if (canHold) {
                    // hold
                    if (hold == null) {
                        hold = next;
                        generateNext();
                    }
                    Piece temp = piece;
                    replace(hold);
                    hold = temp;
                    hold.position.set(0, 0);
                }
                canHold = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) {
                piece.position.set(ghost.position.x, ghost.position.y);
                lastMovement -= KEEP_ALIVE_NS;
                movePieceDown();
            }
        }
        repaint();
    }

    @Override
    public void removed() {
        timer.stop();
        if (bg != null) {
            bg.fadeOut(1.0);
        }
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
            if (System.nanoTime() - lastMovement > KEEP_ALIVE_NS) {
                for (Point x : piece.coords) {
                    int tx = x.x + piece.position.x;
                    int ty = x.y + piece.position.y;
                    if (ty < 0) {
                        // GAME OVER
                        System.out.println("YOU LOSE SUCKER!");
                        GameFrame.get().transition(new GameOver(getScore()));
                        return;
                    }
                    board.set(tx, ty, piece.index);
                }
                // replace piece
                replace();
                // clear lines
                int n = board.clearLines();
                clearedLines(n);
                dropGhost();
            }
        } else {
            piece.position.y++;
        }
    }

    protected void generateNext() {
        next = new Piece((int)(Math.random() * Piece.PIECE_NUM));
        int nrots = (int)(Math.random() * 4);
        for (int i = 0; i < nrots; i++)
            next.rotateClockwise();
    }

    protected void replace() {
        replace(next);
        generateNext();
        canHold = true;
    }

    protected void replace(Piece with) {
        piece = with;
        piece.position.set(Board.BOARD_WIDTH / 2, 0);
        ghost = (Piece)piece.clone();
        dropGhost();
    }

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    private void dropGhost() {
        ghost.position.set(piece.position.x, piece.position.y);
        while (true) {
            for (Point x : ghost.coords) {
                int tx = x.x + ghost.position.x;
                int ty = x.y + ghost.position.y;
                if (ty >= Board.BOARD_HEIGHT) {
                    ghost.position.y--;
                    return;
                }
                if (board.get(tx, ty) >= 0) {
                    ghost.position.y--;
                    return;
                }
            }
            ghost.position.y++;
        }
    }

    private void clearedLines(int n) {
        score += n * (n + 1) * 50;
        if (n > 0 && n < 4) {
            Sounds.Sound.AWW_YEAH.play();
        }
        if (n == 4) {
            Sounds.Sound.TERIS.play();
            Sounds.Sound.WOOO.play();
        }
        linesCleared += n;
        linesLevel += n;
        if (linesLevel >= 10 && level < 20) {
            linesLevel -= 10;
            level++;
            setTimer(level);
            if (level == 20) {
                Sounds.Sound.YOU_WIN.play();
            }
            //
        }
    }

    private void setTimer(int level) {
        timer.setDelay((int)((22750 - 1150 * level) / 27.0));
    }

    private void setColor(int level) {
        backgroundColor = BACKGROUNDS[level % BACKGROUNDS.length];
    }
}
