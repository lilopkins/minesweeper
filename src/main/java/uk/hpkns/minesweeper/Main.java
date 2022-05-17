package uk.hpkns.minesweeper;

public class Main {

    public static void main(String[] args) {
        if (System.getenv().containsKey("TERM")) {
            TerminalGame.start(10);
        } else {
            GUIGame.startGame(10);
        }
    }
}
