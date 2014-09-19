/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 *
 * @author s544545
 */
public class GameOver extends StatePanel {
    protected static BufferedImage loseImg = null;
    private int score;

    public GameOver(int score) {
        this.score = score;
    }

    public static void init() {
        try {
            loseImg = ImageIO.read(new File("lose.png"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load menu image");
        }
    }

    @Override
    public void paint(Graphics g) {
        if (loseImg != null)
            g.drawImage(loseImg, 0, 0, this);
        g.drawString("Your score was " + score, GameFrame.WINDOW_WIDTH/2, GameFrame.WINDOW_HEIGHT- 50);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            GameFrame.get().transition(new MainMenu());
        }
    }
}
