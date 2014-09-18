/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.tetris;

/**
 *
 * @author s506571
 */
public class Board {
    // skeleton, should merge with Thienson
    public static int BOARD_WIDTH = 10;
    public static int BOARD_HEIGHT = 20;
    public static int PIECE_SIZE = 20; // in pixels

    int[][] grid = new int[BOARD_HEIGHT][BOARD_WIDTH];

    public Board() {
        fill();
    }

    /**
     * Fill with -1s
     */
    public void fill() {
        for(int y = 0; y < BOARD_HEIGHT; y ++) {
            for(int x = 0; x < BOARD_WIDTH; x ++) {
                grid[y][x] = -1;
            }
        }
    }

    public int get(int x, int y) {
        try {
            return grid[y][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            return -2;
        }
    }

    public void set(int x, int y, int data) {
        grid[y][x] = data;
    }
}
