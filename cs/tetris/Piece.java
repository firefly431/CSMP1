// rotation clockwise:        (y, -x)
// rotation counterclockwise: (-y, x)
// put that in the Point class as a method

package cs.tetris;

import java.awt.Color;

import cs.tetris.geom.Point;

/**
 *
 * @author s506571
 */
public class Piece implements Cloneable {
    public static final int PIECE_I = 0;
    public static final int PIECE_S = 1;
    public static final int PIECE_Z = 2;
    public static final int PIECE_L = 3;
    public static final int PIECE_J = 4;
    public static final int PIECE_T = 5;
    public static final int PIECE_O = 6;
    public static final int PIECE_NUM = 7;
    
    public static final Color piece_colors[] = {
        Color.CYAN, Color.GREEN, Color.RED, Color.ORANGE, Color.BLUE,
        Color.MAGENTA, Color.YELLOW
    };
    private static final int piece_coords[][][] = {
        {{0, -2}, {0, -1}, {0, 0}, {0, 1}},
        {{0, -1}, {1, -1}, {-1, 0}, {0, 0}},
        {{-1, -1}, {0, -1}, {0, 0}, {1, 0}},
        {{0, -2}, {0, -1}, {0, 0}, {1, 0}},
        {{0, -2}, {0, -1}, {0, 0}, {-1, 0}},
        {{0, -1}, {-1, 0}, {0, 0}, {1, 0}},
        {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
    };
    Point coords[];
    Point position;
    int index;

    public Piece(int index) {
        this(index, 0, 0);
    }

    public Piece(int index, int x, int y) {
        this.index = index;
        coords = new Point[4];
        for (int i = 0; i < 4; i++)
            coords[i] = new Point(piece_coords[index][i][0], piece_coords[index][i][1]);
        position = new Point(x, y);
    }
    
    public void rotateClockwise() {
        for(Point p : coords) {
            p.rotatePointClockwise();
        }
    }
    
    public void rotateCounterClockwise() {
        for(Point p : coords) {
            p.rotatePointCounterClockwise();
        }
    }

    @Override
    public Object clone() {
        Piece p = new Piece(index, position.x, position.y);
        System.arraycopy(coords, 0, p.coords, 0, 4);
        return p;
    }
}

