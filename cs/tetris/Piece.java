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
public class Piece {
    public static int PIECE_I = 0;
    public static int PIECE_S = 1;
    public static int PIECE_Z = 2;
    public static int PIECE_L = 3;
    public static int PIECE_J = 4;
    public static int PIECE_T = 5;
    public static int PIECE_O = 6;
    public static Color piece_colors[] = {
        Color.CYAN, Color.GREEN, Color.RED, Color.ORANGE, Color.BLUE,
        Color.MAGENTA, Color.YELLOW
    };
    private static int piece_coords[][][] = {
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

    public Piece(int index) {
        this(index, 0, 0);
    }

    public Piece(int index, int x, int y) {
        coords = new Point[4];
        for (int i = 0; i < 4; i++)
            coords[i] = new Point(piece_coords[index][i][0], piece_coords[index][i][1]);
        position = new Point(x, y);
    }
}
