/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Sounds class contains sounds that can be played easily once
 * init() is called.
 */
public class Sounds {
    /**
     * Contains sounds to play
     */
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
            if (Music.global_mute.get())
                return;
            if (sound != null)
                sound.start();
        }
    }
    /**
     * Initialize all the sounds
     * @throws Exception
     */
    public static void init() throws Exception {
        for (Sound s : Sound.values())
            s.init();
    }
}
