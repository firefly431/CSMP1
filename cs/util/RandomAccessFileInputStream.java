package cs.util;

import java.io.*;

/**
 * Stupid class because there is none in the Java standard library
 * @see RandomAccessFile
 */
public class RandomAccessFileInputStream extends InputStream {
    private RandomAccessFile raf;
    private long mark;
    public RandomAccessFileInputStream(File f) throws FileNotFoundException {
        raf = new RandomAccessFile(f, "r");
        mark = 0;
    }
    public RandomAccessFileInputStream(String name) throws FileNotFoundException {
        raf = new RandomAccessFile(name, "r");
        mark = 0;
    }
    @Override
    public int available() throws IOException {
        return (int)(raf.length() - raf.getFilePointer());
    }
    @Override
    public void close() throws IOException {
        raf.close();
    }
    @Override
    public long skip(long n) throws IOException {
        return raf.skipBytes((int)n);
    }
    @Override
    public boolean markSupported() {
        return true;
    }
    @Override
    public void mark(int readlimit) {
        try {
            mark = raf.getFilePointer();
        } catch (IOException e) {
            // crap
            System.err.println("We're not allowed to throw, but IOException!");
        }
    }
    @Override
    public void reset() throws IOException {
        raf.seek(mark);
    }
    @Override
    public int read() throws IOException {
        return raf.read();
    }
    @Override
    public int read(byte[] b) throws IOException {
        return raf.read(b);
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return raf.read(b, off, len);
    }
}
