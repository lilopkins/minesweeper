package uk.hpkns.minesweeper;

import java.util.Random;

public class Grid {

    /*
     * Stores the grid of the Minesweeper game. The grid is only initialised after the first click.
     */

    public static final byte UNCOVERED = 0b0100_0000;
    public static final byte FLAG      = 0b0010_0000;
    public static final byte MINE      = 0b0001_0000;
    public static final byte NUMBER    = 0b0000_1111;

    private final byte[][] grid;
    private boolean initialised;
    private final int width;
    private final int height;
    private final int mines;

    public Grid(int size) {
        this(size, size);
    }

    public Grid(int width, int height) {
        this(width, height, width * height / 10);
    }

    public Grid(int width, int height, int mines) {
        this.grid = new byte[height][];
        for (int i = 0; i < height; i++) {
            this.grid[i] = new byte[width];
        }
        this.initialised = false;
        this.width = width;
        this.height = height;
        this.mines = mines;
    }

    /**
     * Generate the game grid, ensuring that a particular position is safe.
     * @param safeX The safe grid position X
     * @param safeY The safe grid position Y
     */
    private void generateGame(int safeX, int safeY) {
        if (initialised) throw new AlreadyInitialisedException();
        initialised = true;

        // Randomise mine positions
        Random rng = new Random();

        for (int i = 0; i < mines; i++) {
            int x, y;
            do {
                x = rng.nextInt(width);
                y = rng.nextInt(height);
            } while (x == safeX && y == safeY);

            grid[y][x] = MINE;
        }

        // Fill in numbers
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isMine(x, y)) continue;

                byte number = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        try {
                            if (isMine(x + i, y + j)) number += 1;
                        } catch (OutOfGridException e) {
                            // Ignore, just at edge of grid
                        }
                    }
                }
                grid[y][x] = number;
            }
        }
    }

    /**
     * Uncover grid position (x, y)
     * @param x Grid position X
     * @param y Grid position Y
     */
    public void uncover(int x, int y) {
        // Initialise on first uncover
        if (!initialised)
            generateGame(x, y);

        if (x < 0 || x >= width) throw new OutOfGridException();
        if (y < 0 || y >= height) throw new OutOfGridException();

        if (isUncovered(x, y)) return;

        grid[y][x] += UNCOVERED;

        if ((grid[y][x] & NUMBER) == 0) {
            // Uncover nearby
            uncoverIgnoreError(x - 1, y);
            uncoverIgnoreError(x + 1, y);
            uncoverIgnoreError(x, y - 1);
            uncoverIgnoreError(x, y + 1);
        }
    }

    private void uncoverIgnoreError(int x, int y) {
        try {
            uncover(x, y);
        } catch (OutOfGridException ignored) { }
    }

    /**
     * Toggle the flag in position (x, y)
     * @param x Grid position X
     * @param y Grid position Y
     */
    public void flag(int x, int y) {
        // Cannot flag before game initialised
        if (!initialised)
            return;

        if (x < 0 || x >= width) throw new OutOfGridException();
        if (y < 0 || y >= height) throw new OutOfGridException();

        if (isFlagged(x, y))
            grid[y][x] -= FLAG;
        else
            grid[y][x] += FLAG;
    }

    /**
     * Is a particular grid space a mine? This will return regardless of whether or not the space is uncovered.
     * @param x Grid position X
     * @param y Grid position Y
     * @return Is the space a mine?
     */
    public boolean isMine(int x, int y) {
        if (!initialised)
            return false;

        if (x < 0 || x >= width) throw new OutOfGridException();
        if (y < 0 || y >= height) throw new OutOfGridException();

        return (grid[y][x] & MINE) == MINE;
    }

    /**
     * Is a particular grid space uncovered?
     * @param x Grid position X
     * @param y Grid position Y
     * @return Is the space uncovered?
     */
    public boolean isUncovered(int x, int y) {
        if (!initialised)
            return false;

        if (x < 0 || x >= width) throw new OutOfGridException();
        if (y < 0 || y >= height) throw new OutOfGridException();

        return (grid[y][x] & UNCOVERED) == UNCOVERED;
    }

    /**
     * Is a particular grid space flagged?
     * @param x Grid position X
     * @param y Grid position Y
     * @return Is the space flagged?
     */
    public boolean isFlagged(int x, int y) {
        if (!initialised)
            return false;

        if (x < 0 || x >= width) throw new OutOfGridException();
        if (y < 0 || y >= height) throw new OutOfGridException();

        return (grid[y][x] & FLAG) == FLAG;
    }

    /**
     * Uncover all the mines on the grid
     */
    public void uncoverAllMines() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isMine(x, y) && !isUncovered(x, y))
                    grid[y][x] += UNCOVERED;
            }
        }
    }

    /**
     * Check if all non-mines are uncovered
     * @return True is game is in this complete state
     */
    public boolean allUncovered() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!(isMine(x, y) || isUncovered(x, y)))
                    return false;
            }
        }
        return true;
    }

    /**
     * Get the width of the grid.
     * @return The width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the grid.
     * @return The height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the data at the given position.
     * @param x Grid position X
     * @param y Grid position Y
     * @return The data.
     */
    public byte get(int x, int y) {
        return grid[y][x];
    }

    /**
     * An exception thrown when the position given is out of the {@link Grid} bounds.
     */
    public static class OutOfGridException extends RuntimeException { }

    /**
     * An exception thrown when the {@link Grid} has already been initialised.
     */
    public static class AlreadyInitialisedException extends RuntimeException { }
}
