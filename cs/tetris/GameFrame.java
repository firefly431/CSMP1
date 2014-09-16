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
    public static int WINDOW_WIDTH = 640;
    public static int WINDOW_HEIGHT = 480;
    private JPanel currentPanel;

    // allow other classes to access the main frame
    private static GameFrame mainFrame = null;

    public static GameFrame get() {
        return mainFrame;
    }

    public GameFrame() {
        super("Tetris!");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        if (mainFrame != null) {
            throw new RuntimeException("More than one GameFrame");
        }
        mainFrame = this;
    }
    
    /**
     * Initialize components and game
     */
    public void init() {
        currentPanel = new MainMenu();
        add(currentPanel);
    }

    public static void main(String argv[]) {
        GameFrame frame = new GameFrame();
        frame.init();
        frame.setVisible(true);
    }
}
