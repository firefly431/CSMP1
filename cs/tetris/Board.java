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
    public static int PIECE_SIZE = 10; // 10 px

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
        return grid[y][x];
    }
}
