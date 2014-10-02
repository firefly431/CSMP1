package cs.tetris.geom;

import cs.tetris.Piece;

/**
 * Utility Point class. Contains x and y as public members. Contains utility
 * functions for point transformations.
 */
public class Point {
    public int x, y;
    /**
     * Create a Point at (0, 0)
     */
    public Point() { this(0, 0); }
    /**
     * Create a Point at (x, y)
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Rotate this point clockwise;
     * Transforms this point to (y, -x)
     * @see #rotatePointCounterClockwise()
     * @see Piece#rotateClockwise()
     */
    public void rotatePointClockwise() {
        int temp = x;
        x = y;
        y = -1 * temp;
    }

    /**
     * Rotate this point counterclockwise;
     * Transforms this point to (-y, x);
     * @see #rotatePointClockwise()
     * @see Piece#rotateCounterClockwise() 
     */
    public void rotatePointCounterClockwise() {
        int temp = x;
        x = -1 * y;
        y = temp;
    }

    /**
     * Set this point's coordinates to the specified x and y coordinates
     * @param x The new x-coordinates
     * @param y The new y-coordinates
     */
    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
