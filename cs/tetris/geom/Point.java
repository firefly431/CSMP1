/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris.geom;

/**
 * Utility Point class. Contains x and y as public members. Contains utility
 * functions for point transformations.
 * @author s506571
 */
public class Point {
    public int x, y;
    public Point() { this(0, 0); }
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void rotatePointClockwise() {
        int temp = x;
        x = y;
        y = -1 * temp;
    }

    public void rotatePointCounterClockwise() {
        int temp = x;
        x = -1 * y;
        y = temp;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
