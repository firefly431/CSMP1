/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

import javax.sound.sampled.*;
import java.io.File;

/**
 *
 * @author s506571
 */
public class Sounds {
    enum Sound {
        AHH, AWW_YEAH, PSHHOOO, TERIS, TETRIS1, WEEE, WOOO, YOU_WIN;
        private Clip sound;
        public String getFileName() {
            return name().toLowerCase().replace('_', ' ') + ".wav";
        }
        public void init() throws Exception {
            AudioInputStream sndIn = AudioSystem.getAudioInputStream(new File(getFileName()));
            sound = AudioSystem.getClip();
            sound.open(sndIn);
        }
        public void play() {
            if (sound != null)
                sound.start();
        }
    }
    public static void init() throws Exception {
        for (Sound s : Sound.values())
            s.init();
    }
}
