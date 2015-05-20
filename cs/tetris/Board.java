package cs.tetris;

/**
 * Board class.
 *
 * Contains a grid of cells and some helper methods.
 */
public class Board {
    // self-explanatory
    public static int BOARD_WIDTH = 10;
    public static int BOARD_HEIGHT = 20;
    public static int PIECE_SIZE = 20; // in pixels

    // the grid, where the first dimension is y (row)
    // and the second dimension is x (column)
    int[][] grid = new int[BOARD_HEIGHT][BOARD_WIDTH];

    public Board() {
        fill();
    }

    /**
     * Fill with -1s (blank)
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
            // -1 is taken
            return -2;
        }
    }

    public void set(int x, int y, int data) {
        grid[y][x] = data;
    }

    /**
     * Returns if the row y is full
     */
    protected boolean isFull(int y) {
        for (int x : grid[y]) {
            if (x < 0)
                return false;
        }
        return true;
    }

    /**
     * Moves down lines which are full, returning the number
     */
    public int clearLines() {
        int cleared = 0;
        // start from the bottom
        for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
            if (isFull(y)) {
                // move the rows up
                for (int j = y; j > 0; j--) {
                    grid[j] = grid[j-1];
                }
                // clear the top row
                grid[0] = new int[BOARD_WIDTH];
                for (int x = 0; x < BOARD_WIDTH; x++)
                    grid[0][x] = -1;
                // increment y so we process it again
                y++;
                cleared++;
            }
        }
        return cleared;
    }
}
