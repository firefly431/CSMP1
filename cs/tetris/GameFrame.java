package cs.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main game window. Contains menu panel or board panel.
 */
public class GameFrame extends JFrame {
    // note: WINDOW_WIDTH > BOARD_WIDTH * PIECE_SIZE
    // same for height
    public static final int WINDOW_WIDTH = 640;
    public static final int WINDOW_HEIGHT = 480;
    private StatePanel currentPanel;

    public static Font PLAY_BODY;
    public static Font PLAY_TITLE;
    public static Font PLAY_SMALL;

    // allow other classes to access the main frame
    private static GameFrame mainFrame = null;

    public static GameFrame get() {
        return mainFrame;
    }

    public GameFrame() {
        super("Tetris!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        if (mainFrame != null) {
            throw new RuntimeException("More than one GameFrame");
        }
        mainFrame = this;
    }
    
    /**
     * Initialize components and game
     */
    public void init() {
        try {
            Sounds.init();
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Failed to load sounds");
        }
        // load font
        try {
            // URL
            Font play_base = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getClassLoader().getResourceAsStream("cs/tetris/play.ttf"));
            // Font play_base = Font.createFont(Font.TRUETYPE_FONT, new File("play.ttf"));
            // derive 16pt and 48pt fonts
            PLAY_BODY = play_base.deriveFont(Font.PLAIN, 24);
            PLAY_SMALL = play_base.deriveFont(Font.PLAIN, 16);
            PLAY_TITLE = play_base.deriveFont(Font.PLAIN, 72);
        } catch (Exception e) {
            PLAY_BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 24);
            PLAY_SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
            PLAY_TITLE = new Font(Font.SANS_SERIF, Font.PLAIN, 72);
        }
        MainMenu.init();
        ControlsScreen.init();
        GameOver.init();
        currentPanel = new MainMenu();
        getContentPane().add(currentPanel);
        addKeyListener((KeyListener)currentPanel);
        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    boolean v;
                    do {
                        v = Music.global_mute.get();
                    } while (!Music.global_mute.compareAndSet(v, !v));
                    updateTitle(!v);
                }
            }

            public void keyReleased(KeyEvent e) {}
        });
        pack();
        updateTitle(false);
    }

    protected void updateTitle(boolean mute) {
        setTitle("Tetris! - Press M to " + (mute ? "unmute" : "mute"));
    }

    public JPanel getCurrentPanel() {
        return currentPanel;
    }

    /**
     * Transition to another panel
     *
     * Removes current panel, if any, and adds the target panel and its
     * KeyListener.
     * @param target Target JPanel, must be a KeyListener
     */
    public void transition(StatePanel target) {
        if (currentPanel == target)
            return;
        if (currentPanel != null) {
            remove(currentPanel);
            removeKeyListener(currentPanel);
            currentPanel.removed();
        }
        currentPanel = target;
        getContentPane().add(currentPanel);
        addKeyListener(currentPanel);
        currentPanel.revalidate();
        repaint();
    }

    public static void main(String argv[]) {
        GameFrame frame = new GameFrame();
        frame.init();
        frame.setVisible(true);
    }
}
