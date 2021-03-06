package cs.tetris;

import java.awt.*;
import java.awt.event.*;

import cs.tetris.geom.Point;
import cs.util.RandomAccessFileInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.Timer;

/**
 * Main Game Panel
 */
public class GamePanel extends StatePanel implements ActionListener {
    // these are self-explanatory
    public static final int BOARD_PIXEL_WIDTH = Board.BOARD_WIDTH * Board.PIECE_SIZE;
    public static final int BOARD_PIXEL_HEIGHT = Board.BOARD_HEIGHT * Board.PIECE_SIZE;
    public static final int BOARD_X = (GameFrame.WINDOW_WIDTH - BOARD_PIXEL_WIDTH) / 2;
    public static final int BOARD_Y = GameFrame.WINDOW_HEIGHT - BOARD_PIXEL_HEIGHT;
    public static final int HOLD_LABEL_X = 20;
    public static final int HOLD_LABEL_Y = 40;
    public static final int HOLD_X = Board.PIECE_SIZE * 3 + HOLD_LABEL_X;
    public static final int HOLD_Y = Board.PIECE_SIZE * 3 + HOLD_LABEL_Y;
    public static final int NEXT_LABEL_X = 20;
    public static final int NEXT_LABEL_Y = HOLD_LABEL_Y + Board.PIECE_SIZE * 8;
    public static final int NEXT_X = Board.PIECE_SIZE * 3 + NEXT_LABEL_X;
    public static final int NEXT_Y = Board.PIECE_SIZE * 3 + NEXT_LABEL_Y;
    public static final int BOARD_RT_X = BOARD_X + BOARD_PIXEL_WIDTH;
    public static final int SCORE_PANEL_X = BOARD_RT_X + 20;
    public static final int SCORE_PANEL_Y = 40;

    // color cycling
    private Color backgroundColor;
    public static final Color BACKGROUNDS[] = {
        new Color(96, 96, 96), new Color(102, 71, 71), new Color(71, 71, 102),
        new Color(71, 102, 71)
    };
    public static final Color EMPTY_COLOR = new Color(128, 128, 128);
    public static final Color TEXT_COLOR = new Color(255, 255, 255);
    public static final Color DISABLED_COLOR = new Color(128, 128, 128);

    private Board board;
    private Piece piece, hold, ghost;
    private LinkedList<Piece> next;
    private int canHold; // can we hold or have we already held

    private Timer timer;
    // used so that pieces will not drop until the player stops moving it

    private int score;
    private int level;
    private int linesCleared;
    private int linesLevel;
    public boolean pause = false;

    // key states
    private boolean leftK, rightK, upK, downK, zK, xK;
    // timers
    private Timer controlTimer;
    private Timer newPieceTimer;
    private Timer cementTimer;
    private Piece cementPiece;

    public static final int KEEP_ALIVE_MS = 800;
    public static final int PEEK_NUM = Piece.PIECE_NUM;
    public static final int PEEK_SIZE = 10;
    public static final int CONTROL_DELAY_MS = 120;
    public static final int NEW_PIECE_MS = 200;

    private Music bg;

    public GamePanel() {
        this(1);
    }

    public GamePanel(int level) {
        this(new Board(), level);
    }

    public GamePanel(Board b, int level) {
        board = b;
        timer = new Timer(1000, this);
        timer.setCoalesce(false); // disable buffering of timers
        timer.start();
        // basically, have a consistent key repeat
        controlTimer = new Timer(CONTROL_DELAY_MS, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (upK)
                    doKey(KeyEvent.VK_UP);
                if (downK)
                    doKey(KeyEvent.VK_DOWN);
                if (leftK)
                    doKey(KeyEvent.VK_LEFT);
                if (rightK)
                    doKey(KeyEvent.VK_RIGHT);
                if (zK)
                    doKey(KeyEvent.VK_Z);
                if (xK)
                    doKey(KeyEvent.VK_X);
                repaint();
            }
        });
        controlTimer.setCoalesce(false);
        // timer for new piece, self-explanatory
        newPieceTimer = new Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                replace(next.removeLast());
                generateNext();
                if (canHold != 1)
                    canHold--;
            }
        });
        // this timer doesn't repeat but performs an action after NEW_PIECE_MS
        newPieceTimer.setInitialDelay(NEW_PIECE_MS);
        newPieceTimer.setRepeats(false);
        // similar to this one, cements after some time
        cementTimer = new Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (cementPiece == piece)
                    cement();
            }
        });
        cementTimer.setInitialDelay(KEEP_ALIVE_MS);
        cementTimer.setRepeats(false);
        controlTimer.setInitialDelay(0);
        next = new LinkedList<Piece>();
        generateNext();
        replace();
        hold = null;
        canHold = 1;
        score = linesCleared = linesLevel = 0;
        this.level = level;
        try {
            bg = Music.createWithAmp(new RandomAccessFileInputStream("bg.wav"));
            bg.setAmplitude(0.1);
            bg.play_with_amplitude();
        } catch (Exception e) {
            bg = null;
        }
        setTimer(level);
        setColor(level);
        leftK = rightK = upK = downK = zK = xK = false;
    }
    
    /**
     * Draw a block
     * @param g the Graphics to use when drawing
     * @param p the Point at which to draw
     * @param c the Color to use
     * @param size the size of the block
     */
    public static void drawBlock(Graphics g, Point p, Color c, int size) {
        drawBlock(g, p.x, p.y, c, size);
    }

    /**
     * Draw a block
     * @param g the Graphics to use when drawing
     * @param px the x coordinate
     * @param py the y coordinate
     * @param c the Color to use
     * @param size the size of the block
     */
    public static void drawBlock(Graphics g, int px, int py, Color c, int size) {
        int bwidth = size / 8;
        g.setColor(c);
        g.fillRect(px, py, size - 1, size - 1);
        g.setColor(c.darker());
        g.fillRect(px + bwidth, py + bwidth, size - 1 - bwidth - bwidth, bwidth);
        g.fillRect(px + bwidth, py + size - 1 - bwidth - bwidth, size - 1 - bwidth - bwidth, bwidth);
        g.fillRect(px + bwidth, py + bwidth + bwidth, bwidth, size - 1 - bwidth - bwidth - bwidth - bwidth);
        g.fillRect(px + size - 1 - bwidth - bwidth, py + bwidth + bwidth, bwidth, size - 1 - bwidth - bwidth - bwidth - bwidth);
    }

    // overloads of drawPiece
    public static void drawPiece(Graphics g, Piece p, int x, int y) {
        drawPiece(g, p, new Point(x, y));
    }

    public static void drawPiece(Graphics g, Piece p, int x, int y, int size) {
        drawPiece(g, p, new Point(x, y), size);
    }

    public static void drawPiece(Graphics g, Piece p, Point origin) {
        drawPiece(g, p, origin, Board.PIECE_SIZE);
    }

    /**
     * Draw a whole piece
     * @param g the Graphics to use
     * @param p the Piece to draw
     * @param origin the origin point (0,0) of the piece
     * @param size the size of the blocks
     */
    public static void drawPiece(Graphics g, Piece p, Point origin, int size) {
        if (p == null) return;
        for (Point x : p.coords) {
            int px = origin.x + (p.position.x + x.x) * size;
            int py = origin.y + (p.position.y + x.y) * size;
            drawBlock(g, px, py, Piece.piece_colors[p.index], size);
        }
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, GameFrame.WINDOW_WIDTH, GameFrame.WINDOW_HEIGHT);
        // make text pretty
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        if (pause) {
            drawPauseScreen(g);
            MainMenu.drawControls(g);
            return;
        }
        for (int y = 0; y < Board.BOARD_HEIGHT; y++) {
            for (int x = 0; x < Board.BOARD_WIDTH; x++) {
                int px = x * Board.PIECE_SIZE + BOARD_X;
                int py = y * Board.PIECE_SIZE + BOARD_Y;
                int p = board.get(x, y);
                if (p == -1) {
                    g.setColor(EMPTY_COLOR);
                    g.fillRect(px, py, Board.PIECE_SIZE - 1, Board.PIECE_SIZE - 1);
                } else
                    drawBlock(g, px, py, Piece.piece_colors[p], Board.PIECE_SIZE);
            }
        }
        g.setColor(EMPTY_COLOR);
        g.fillRect(NEXT_X - Board.PIECE_SIZE * 2, NEXT_Y - Board.PIECE_SIZE * 2, Board.PIECE_SIZE * 5, Board.PIECE_SIZE * 5);
        g.fillRect(HOLD_X - Board.PIECE_SIZE * 2, HOLD_Y - Board.PIECE_SIZE * 2, Board.PIECE_SIZE * 5, Board.PIECE_SIZE * 5);
        drawPiece(g, piece, BOARD_X, BOARD_Y);
        // draw next pieces
        Iterator<Piece> it = next.descendingIterator();
        Point npos = new Point(NEXT_X, NEXT_Y);
        boolean first = true;
        for (int i = 0; i < PEEK_NUM; i++) {
            Piece p = it.next();
            if (first) {
                first = false;
                drawPiece(g, p, npos);
                npos.y += Board.PIECE_SIZE * 5;
            } else {
                drawPiece(g, p, npos, PEEK_SIZE);
                npos.y += PEEK_SIZE * 5;
            }
        }
        drawPiece(g, hold, HOLD_X, HOLD_Y);
        g.setColor(TEXT_COLOR);
        g.setFont(GameFrame.PLAY_BODY);
        g.drawString("NEXT PIECE", NEXT_LABEL_X, NEXT_LABEL_Y);
        g.drawString("HELD PIECE", HOLD_LABEL_X, HOLD_LABEL_Y);
        g.drawString("SCORE: " + getScore(), SCORE_PANEL_X-200, SCORE_PANEL_Y);
        g.drawString("LEVEL: " + level, SCORE_PANEL_X, SCORE_PANEL_Y + 30);
        g.setFont(GameFrame.PLAY_SMALL);
        g.drawString("LINES CLEARED: " + linesCleared, SCORE_PANEL_X, SCORE_PANEL_Y + 60);
        if (level < 20)
            g.drawString("LINES TO NEXT LEVEL: " + (10 - linesLevel), SCORE_PANEL_X, SCORE_PANEL_Y + 90);
        // draw the ghost at 0.5 alpha
        ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        drawPiece(g, ghost, BOARD_X, BOARD_Y);
    }

    public void doKey(int kc) {
        if (piece == null) return;
        if (kc == KeyEvent.VK_LEFT) {
            piece.position.x--;
            int minx = 0;
            for (Point x : piece.coords) {
                // find the minimum x
                if (x.x + piece.position.x < minx)
                    minx = x.x + piece.position.x;
                if (minx >= 0)
                    if (board.get(x.x + piece.position.x, x.y + piece.position.y) > -1) {
                        piece.position.x++;
                        break;
                    }
            }
            // correct piece position (don't go off the edge)
            piece.position.x -= minx;
            cementTimer.stop();
            cementPiece = null;
            dropGhost();
        }
        if (kc == KeyEvent.VK_RIGHT) {
            // similar
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
            cementTimer.stop();
            cementPiece = null;
            dropGhost();
        }
        if (kc == KeyEvent.VK_DOWN) {
            movePieceDown();
        }
        if (kc == KeyEvent.VK_Z || kc == KeyEvent.VK_X || kc == KeyEvent.VK_UP) {
            if (kc != KeyEvent.VK_X)
                piece.rotateCounterClockwise();
            else
                piece.rotateClockwise();
            cementTimer.stop();
            cementPiece = null;
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
    }

    // keys that don't repeat
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            pause = !pause;
            if(pause)
                timer.stop();
            else
                timer.start();
            repaint();
            return;
        }
        if (pause) return;
        if (e.getKeyCode() == KeyEvent.VK_C) {
            if (piece == null) return;
            if (canHold == 1) {
                // hold
                if (hold == null) {
                    hold = next.removeLast();
                    generateNext();
                }
                next.addLast(hold);
                hold = piece;
                hold.position.set(0, 0);
                replace();
                canHold = 3;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (piece == null) return;
            score += (int)5 * (ghost.position.y - piece.position.y);
            piece.position.set(ghost.position.x, ghost.position.y);
            cement();
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                upK = true;
                break;
            case KeyEvent.VK_DOWN:
                downK = true;
                break;
            case KeyEvent.VK_LEFT:
                leftK = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightK = true;
                break;
            case KeyEvent.VK_Z:
                zK = true;
                break;
            case KeyEvent.VK_X:
                xK = true;
                break;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_Z:
            case KeyEvent.VK_X:
                if (!controlTimer.isRunning())
                    controlTimer.start();
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                upK = false;
                break;
            case KeyEvent.VK_DOWN:
                downK = false;
                break;
            case KeyEvent.VK_LEFT:
                leftK = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightK = false;
                break;
            case KeyEvent.VK_Z:
                zK = false;
                break;
            case KeyEvent.VK_X:
                xK = false;
                break;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_Z:
            case KeyEvent.VK_X:
                if (!upK && !downK && !leftK && !rightK && !zK && !xK)
                    if (controlTimer.isRunning())
                        controlTimer.stop();
        }
    }

    @Override
    public void removed() {
        // stop the timers and fade bg music
        timer.stop();
        if (bg != null) {
            bg.fadeOut(1.0);
        }
    }

    // move piece down
    public void actionPerformed(ActionEvent e) {
        if (cementTimer.isRunning()) return;
        if (piece == null) return;
        movePieceDown();
        repaint();
    }

    protected void movePieceDown() {
        boolean drop = false;
        // check if we should start cementing
        for (Point x : piece.coords) {
            int tx = x.x + piece.position.x;
            int ty = x.y + piece.position.y + 1;
            if (ty >= Board.BOARD_HEIGHT || board.get(tx, ty) > -1) {
                drop = true;
                break;
            }
        }
        if (drop) {
            if (!cementTimer.isRunning() && cementPiece != piece) {
                cementTimer.start();
                cementPiece = piece;
            }
        } else {
            piece.position.y++;
        }
        score++;
    }

    // this generates a new batch of pieces when we're running low
    protected void generateNext() {
        if (next.size() >= PEEK_NUM)
            return;
        // generate a shuffled array of [0, PIECE_NUM)
        // this is a variant of Fisher-Yates
        int shuffle[] = new int[Piece.PIECE_NUM];
        for (int n = 1; n <= Piece.PIECE_NUM; n++) {
            // insert n-1 at a random position [0, n)
            int pos = (int)(Math.random() * n);
            if (pos + 1 != n)
                shuffle[n - 1] = shuffle[pos];
            shuffle[pos] = n - 1;
        }
        for (int p : shuffle) {
            Piece pp = new Piece(p);
            // rotate randomly
            int snum = (int)(Math.random() * 4);
            for (int i = 0; i < snum; i++)
                pp.rotateClockwise();
            next.addFirst(pp);
        }
    }

    // replace with nothing
    protected void replace() {
        newPieceTimer.start();
        piece = ghost = null;
    }

    // replace current piece
    protected void replace(Piece with) {
        piece = with;
        piece.position.set(Board.BOARD_WIDTH / 2, -3);
        ghost = (Piece)piece.clone();
        dropGhost();
    }

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Position the ghost piece
     */
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

    /**
     * Called when we clear some lines
     * 
     * Usually play a sound effect
     * but may increment level
     */
    private void clearedLines(int n) {
        score += n * (n + 1) * 250;
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
            setColor(level);
            if (level == 20) {
                Sounds.Sound.YOU_WIN.play();
            }
            //
        }
    }

    /**
     * Change the timer based on level
     */
    private void setTimer(int level) {
        if (level >= 20)
            timer.setDelay(10);
        else
            timer.setDelay((int)((22750 - 1150 * level) / 27.0));
    }

    /**
     * Change bg color based on level
     */
    private void setColor(int level) {
        backgroundColor = BACKGROUNDS[level % BACKGROUNDS.length];
    }

    /**
     * Cement the current piece
     */
    private void cement() {
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
        // clear lines
        int n = board.clearLines();
        clearedLines(n);
        // replace piece
        replace();
    }

    // self-explanatory
    public static void drawPauseScreen(Graphics g) {
        g.setFont(GameFrame.PLAY_TITLE);
        g.setColor(TEXT_COLOR);
        FontMetrics mt = g.getFontMetrics();
        MainMenu.drawCenter(g, "PAUSED", mt, 80);
    }
}
