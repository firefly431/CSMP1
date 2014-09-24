package cs.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.File;

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
    public static Font PLAY_48;
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
        MainMenu.init();
        GameOver.init();
        try {
            Sounds.init();
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Failed to load sounds");
        }
        // load font
        try {
            Font play_base = Font.createFont(Font.TRUETYPE_FONT, new File("play.ttf"));
            // derive 16pt and 48pt fonts
            PLAY_BODY = play_base.deriveFont(Font.PLAIN, 24);
            PLAY_SMALL = play_base.deriveFont(Font.PLAIN, 16);
            PLAY_48 = play_base.deriveFont(Font.PLAIN, 48);
        } catch (Exception e) {
            PLAY_BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
            PLAY_48 = new Font(Font.SANS_SERIF, Font.PLAIN, 48);
        }
        currentPanel = new MainMenu();
        getContentPane().add(currentPanel);
        addKeyListener((KeyListener)currentPanel);
        pack();
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
