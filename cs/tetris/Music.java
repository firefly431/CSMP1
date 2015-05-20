package cs.tetris;

import java.io.*;
import javax.sound.sampled.*;
import cs.util.RandomAccessFileInputStream;
import java.util.concurrent.atomic.*;

// HERE BE DRAGONS
// joking aside, this class is pretty complicated
public class Music {
    // we have threads so it has to be atomic
    private AtomicBoolean playing;
    // input stream for the music file
    private AudioInputStream in;
    // audio output line
    private SourceDataLine out;
    // amplitude
    private double amplitude;
    // fade time in samples
    private AtomicInteger fadeSamples;
    // size of the audio buffer
    public static final int BUFFER_SIZE = 4096;
    // whether or not music is muted (global)
    public static AtomicBoolean global_mute = new AtomicBoolean(false);
    public Music(File f) throws FileNotFoundException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        this(new RandomAccessFileInputStream(f));
    }
    public Music(String f) throws FileNotFoundException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        this(new RandomAccessFileInputStream(f));
    }
    public Music(RandomAccessFileInputStream f) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        playing = new AtomicBoolean(false);
        fadeSamples = new AtomicInteger(0);
        in = AudioSystem.getAudioInputStream(f);
        AudioFormat fmt = in.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
        out = (SourceDataLine)AudioSystem.getLine(info);
        out.open(fmt);
    }
    // protect this one (used to implement subclasses or the below function)
    protected Music() {
        playing = new AtomicBoolean(false);
        fadeSamples = new AtomicInteger(0);
        amplitude = 1.0;
    }
    // create a music file capable of being controlled by amplitude
    public static Music createWithAmp(RandomAccessFileInputStream f) throws Exception {
        Music m = new Music();
        AudioFormat fmt = new AudioFormat(44100f, 16, 1, true, true);
        // 44.1kHz signed 16-bit linear mono big-endian PCM
        m.in = AudioSystem.getAudioInputStream(fmt, AudioSystem.getAudioInputStream(f));
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
        m.out = (SourceDataLine)AudioSystem.getLine(info);
        m.out.open(fmt);
        return m;
    }
    // see playWithAmp(), as this function just copies directly
    // instead of processing
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
    public void play_with_amplitude() throws IOException {
        playing.set(true);
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // start the output line
                    out.start();
                    // buffer to hold our bytes
                    byte[] buf = new byte[BUFFER_SIZE];
                    int off = 0;
mainloop:
                    // while we're playing music
                    while (playing.get()) {
                        // this is the offset off the byte buffer
                        off = 0;
                        // read until buffer is full
                        while (off < BUFFER_SIZE) {
                            int read;
                            try {
                                // bytes read
                                read = in.read(buf, off, BUFFER_SIZE - off);
                                if (read + off < BUFFER_SIZE)
                                    in.reset(); // restart the in buffer
                            } catch (IOException e) {
                                break mainloop;
                            }
                            if (read < 0) read = 0;
                            // move offset
                            off += read;
                        }
                        // write to the out line
                        for (int i = 0; i < BUFFER_SIZE; i += 2) {
                            // endian-independent read
                            short a = (short)(((buf[i] & 0xFF) << 8) | (buf[i + 1] & 0xFF));
                            // if mute, 0, else multiply a by amplitude
                            a = global_mute.get() ? 0 : (short)(a * amplitude);
                            // write endian-independent
                            buf[i] = (byte)((a >>> 8) & 0xFF);
                            buf[i + 1] = (byte)(a & 0xFF);
                            // if fade, decrease amplitude
                            int fs = fadeSamples.get();
                            if (fs > 0) {
                                amplitude -= 1.0 / fs;
                                if (amplitude < 0) {
                                    playing.set(false);
                                    amplitude = 0;
                                }
                            }
                        }
                        // write the buffer
                        out.write(buf, 0, BUFFER_SIZE);
                    }
                } finally {
                    // play all remaining samples
                    out.drain();
                    // and close the line
                    out.close();
                    // close the in stream
                    try {in.close();} catch (IOException e) {System.out.println("IE");}
                    // whew!
                }
            }
        })).start();
    }
    public void stop() {
        playing.set(false);
    }
    public void fadeOut(double secs) {
        fadeSamples.set((int)(secs * in.getFormat().getFrameRate() / amplitude));
    }
    public static void main(String argv[]) throws Exception {
        Music m = Music.createWithAmp(new RandomAccessFileInputStream("bg.wav"));
        try {
            m.play_with_amplitude();
            System.out.print("Type something to stop: ");
            System.in.read();
        } finally {
            m.fadeOut(1.0);
        }
    }

    void setAmplitude(double d) {
        amplitude = d;
    }
}
