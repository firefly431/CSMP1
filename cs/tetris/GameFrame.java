package cs.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main game window. Contains menu panel or board panel.
 * @author s506571
 */
public class GameFrame extends JFrame {
    JPanel currentPanel;

    public GameFrame() {
        super("Tetris!");
        setSize(Board.BOARD_WIDTH * Board.PIECE_SIZE,
                Board.BOARD_HEIGHT * Board.PIECE_SIZE);
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
