package cs.tetris;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Game over screen
 */
public class GameOver extends StatePanel {
    // see controls screen; it's nearly identical
    protected static BufferedImage loseImg = null;
    private int score;

    public GameOver(int score) {
        this.score = score;
    }

    public static void init() {
        try {
            //loseImg = ImageIO.read(new File("lose.png"));
            loseImg = ImageIO.read(GameOver.class.getClassLoader().getResourceAsStream("cs/tetris/lose.png"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load menu image");
        }
    }

    // we draw the score in addition to the image
    @Override
    public void paint(Graphics g) {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        if (loseImg != null)
            g.drawImage(loseImg, 0, 0, this);
        g.setFont(GameFrame.PLAY_BODY);
        g.drawString("Your score was: " + score, GameFrame.WINDOW_WIDTH/2, GameFrame.WINDOW_HEIGHT- 50);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            GameFrame.get().transition(new MainMenu());
        }
    }
}
