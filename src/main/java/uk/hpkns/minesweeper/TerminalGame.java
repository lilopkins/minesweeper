package uk.hpkns.minesweeper;

import java.util.Scanner;

public class TerminalGame {

    public static final String FLAG = "⚑";
    public static final String MINE = "╳";
    public static final String COVERED = "█";
    public static final String INVALID_INPUT = "Invalid input! Try again!";

    private TerminalGame() {}

    private static void printGrid(Grid grid) {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                if (grid.isFlagged(x, y)) {
                    System.out.print(FLAG);
                    continue;
                }
                if (!grid.isUncovered(x, y)) {
                    System.out.print(COVERED);
                    continue;
                }
                if (grid.isMine(x, y)) {
                    // Only for uncovered mines on game loss.
                    System.out.print(MINE);
                    continue;
                }
                byte pos = grid.get(x, y);
                System.out.print(pos & Grid.NUMBER);
            }
            System.out.printf("  %d%n", y);
        }
        for (int i = 0; i < grid.getWidth(); i++) {
            System.out.printf("%d", i % 10);
        }
        System.out.println();
        for (int i = 0; i < grid.getWidth(); i += 10) {
            System.out.printf("%d", i / 10);
        }
        System.out.println();
    }

    public static void start(int size) {
        Grid grid = new Grid(size);
        Scanner scan = new Scanner(System.in);

        while (true) {
            printGrid(grid);
            System.out.println("Enter move: 'UX,Y' to uncover, 'FX,Y' to flag.");
            System.out.print("  > ");

            String input = scan.nextLine();
            if (input.startsWith("U")) {
                // Uncover
                String[] parts = input.substring(1).split(",");
                int x;
                int y;
                try {
                    x = Integer.parseInt(parts[0]);
                    y = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    System.out.println(INVALID_INPUT);
                    continue;
                }
                grid.uncover(x, y);
                if (grid.isMine(x, y)) {
                    // Game loss! Uncover all mines.
                    grid.uncoverAllMines();
                    printGrid(grid);
                    System.out.println("BANG! You lost!");
                    break;
                }
                if (grid.allUncovered()) {
                    // Game win!
                    printGrid(grid);
                    System.out.println("You won!");
                    break;
                }
            } else if (input.startsWith("F")) {
                // Flag
                String[] parts = input.substring(1).split(",");
                int x;
                int y;
                try {
                    x = Integer.parseInt(parts[0]);
                    y = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    System.out.println(INVALID_INPUT);
                    continue;
                }
                grid.flag(x, y);
            } else {
                // Do nothing...
                System.out.println(INVALID_INPUT);
            }

            System.out.println("---------------------------------------------");
        }
    }
}
