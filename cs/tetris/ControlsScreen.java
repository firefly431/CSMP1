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
 * @author s506571
 */
public class ControlsScreen extends StatePanel {
    protected static BufferedImage controlsImg = null;

    public ControlsScreen() {
        //
    }

    public static void init() {
        try {
            //controlsImg = ImageIO.read(new File("control.png"));
            controlsImg = ImageIO.read(ControlsScreen.class.getClassLoader().getResourceAsStream("cs/tetris/control.png"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load menu image");
        }
    }

    @Override
    public void paint(Graphics g) {
        if (controlsImg != null)
            g.drawImage(controlsImg, 0, 0, this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            GameFrame.get().transition(new MainMenu());
            Sounds.Sound.WOOO.play();
        }
    }
}
