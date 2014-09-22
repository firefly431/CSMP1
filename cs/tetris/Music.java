package cs.tetris;

import java.io.*;
import javax.sound.sampled.*;
import cs.util.RandomAccessFileInputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class Music {
    private AtomicBoolean playing;
    private AudioInputStream in;
    private SourceDataLine out;
    private double amplitude;
    public static final int BUFFER_SIZE = 4096;
    public Music(File f) throws FileNotFoundException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        this(new RandomAccessFileInputStream(f));
    }
    public Music(String f) throws FileNotFoundException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        this(new RandomAccessFileInputStream(f));
    }
    public Music(RandomAccessFileInputStream f) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        playing = new AtomicBoolean(false);
        in = AudioSystem.getAudioInputStream(f);
        AudioFormat fmt = in.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
        out = (SourceDataLine)AudioSystem.getLine(info);
        out.open(fmt);
    }
    public void play() throws IOException {
        playing.set(true);
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.start();
                    byte[] buf = new byte[BUFFER_SIZE];
                    int off = 0;
mainloop:
                    while (playing.get()) {
                        off = 0;
                        while (off < BUFFER_SIZE) {
                            int read;
                            try {
                                read = in.read(buf, off, BUFFER_SIZE - off);
                                if (read + off < BUFFER_SIZE)
                                    in.reset();
                            } catch (IOException e) {
                                break mainloop;
                            }
                            if (read < 0) read = 0;
                            off += read;
                        }
                        out.write(buf, 0, BUFFER_SIZE);
                    }
                } finally {
                    out.drain();
                    out.close();
                    try {in.close();} catch (IOException e) {System.out.println("IE");}
                }
            }
        })).start();
    }
    public void stop() {
        playing.set(false);
    }
    public static void main(String argv[]) throws Exception {
        Music m = new Music("bg.wav");
        try {
            m.play();
            System.out.print("Type something to stop: ");
            System.in.read();
        } finally {
            m.stop();
        }
    }
}
