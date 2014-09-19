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

    protected int score;

    int[][] grid = new int[BOARD_HEIGHT][BOARD_WIDTH];

    public Board() {
        fill();
        score = 0;
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

    public int getScore() {
        return score;
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

    protected boolean isFull(int y) {
        for (int x : grid[y]) {
            if (x < 0)
                return false;
        }
        return true;
    }

    public void clearLines() {
        int increment = 0;
        for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
            if (isFull(y)) {
                for (int j = y; j > 0; j--) {
                    grid[j] = grid[j-1];
                }
                grid[0] = new int[BOARD_WIDTH];
                for (int x = 0; x < BOARD_WIDTH; x++)
                    grid[0][x] = -1;
                y++;
                score += (increment += 100);
            }
        }
    }
}
