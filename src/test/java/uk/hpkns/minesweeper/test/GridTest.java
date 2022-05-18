package uk.hpkns.minesweeper.test;

import org.junit.jupiter.api.Test;
import uk.hpkns.minesweeper.Grid;

import static org.junit.jupiter.api.Assertions.*;

public class GridTest {

    @Test
    public void testCreateSquareGrid() {
        Grid grid = new Grid(10);
        assertEquals(10, grid.getWidth(), "correct grid width");
        assertEquals(10, grid.getHeight(), "correct grid height");
    }

    @Test
    public void testCreateRectangularGrid() {
        Grid grid = new Grid(20, 10);
        assertEquals(20, grid.getWidth(), "correct grid width");
        assertEquals(10, grid.getHeight(), "correct grid height");
    }

    @Test
    public void testCreateRectangularGridWithCustomMines() {
        Grid grid = new Grid(20, 10, 2);
        assertEquals(20, grid.getWidth(), "correct grid width");
        assertEquals(10, grid.getHeight(), "correct grid height");

        // Mines are only initialised after first uncover, which is always safe
        grid.uncover(0, 0);

        int totalMines = 0;
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                if (grid.isMine(x, y))
                    totalMines += 1;
            }
        }

        assertEquals(2, totalMines, "correct number of mines");
    }

    @Test
    public void testUncoverActuallyUncovers() {
        Grid grid = new Grid(5);
        grid.uncover(3, 3);
        assertTrue(grid.isUncovered(3, 3), "cell is uncovered");
    }

    @Test
    public void testUncoverOutOfGridBoundsThrows() {
        Grid grid = new Grid(5);
        grid.uncover(0, 0); // To initialise
        assertThrows(Grid.OutOfGridException.class, () -> grid.uncover(6, 0), "throws on x overflow");
        assertThrows(Grid.OutOfGridException.class, () -> grid.uncover(0, 6), "throws on y overflow");
    }

    @Test
    public void testFlagActuallyToggles() {
        Grid grid = new Grid(5);
        grid.uncover(0, 0); // To initialise
        grid.flag(3, 3);
        assertTrue(grid.isFlagged(3, 3), "cell is flagged");
    }

    @Test
    public void testFlagDoesntToggleUncovered() {
        Grid grid = new Grid(5);
        grid.uncover(3, 3);
        grid.flag(3, 3);
        assertFalse(grid.isFlagged(3, 3), "cell isn't flagged");
    }

    @Test
    public void testFlagOutOfGridBoundsThrows() {
        Grid grid = new Grid(5);
        grid.uncover(0, 0); // To initialise
        assertThrows(Grid.OutOfGridException.class, () -> grid.flag(6, 0), "throws on x overflow");
        assertThrows(Grid.OutOfGridException.class, () -> grid.flag(0, 6), "throws on y overflow");
    }

    @Test
    public void testUncoverAllMinesUncovers() {
        Grid grid = new Grid(5);
        grid.uncover(0, 0); // To initialise
        grid.uncoverAllMines();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                if (grid.isMine(x, y))
                    assertTrue(grid.isUncovered(x, y), "mine is uncovered");
            }
        }
    }
}
