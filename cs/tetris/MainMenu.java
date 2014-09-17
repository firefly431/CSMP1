/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

import javax.swing.*;
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
public class MainMenu extends StatePanel {
    protected static BufferedImage menuImg = null;

    public MainMenu() {
        //
    }

    public static void init() {
        try {
            menuImg = ImageIO.read(new File("menu.png"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load menu image");
        }
    }

    @Override
    public void paint(Graphics g) {
        if (menuImg != null)
            g.drawImage(menuImg, 0, 0, this);
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            GameFrame.get().transition(new GamePanel());
        }
    }
}
