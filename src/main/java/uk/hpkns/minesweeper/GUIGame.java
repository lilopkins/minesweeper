package uk.hpkns.minesweeper;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static uk.hpkns.minesweeper.Grid.NUMBER;

public class GUIGame extends Application {

    public static final double BUTTON_SIZE = 32d;
    private Grid grid;
    Button[][] btnGrid;
    private boolean gameOver;

    @Override
    public void start(Stage stage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(16, 16, 16, 16));
        ScrollPane scroll = new ScrollPane(borderPane);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        Scene scene = new Scene(scroll, 640, 640);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(4d);
        gridPane.setVgap(4d);

        HBox topRow = new HBox();
        topRow.setSpacing(32d);
        Label lbl = new Label("Minesweeper");
        lbl.setFont(new Font(24d));
        topRow.getChildren().add(lbl);

        Button btnNew = new Button("New game");
        btnNew.setOnAction(actionEvent -> initialiseGrid(gridPane));
        topRow.getChildren().add(btnNew);
        borderPane.setTop(topRow);

        borderPane.setCenter(gridPane);
        initialiseGrid(gridPane);

        stage.setTitle("Minesweeper");
        stage.setScene(scene);
        stage.show();
    }

    private void initialiseGrid(GridPane gridPane) {
        gameOver = false;
        grid = new Grid(16);
        gridPane.getChildren().clear();
        btnGrid = new Button[grid.getHeight()][];
        for (int y = 0; y < grid.getHeight(); y++) {
            btnGrid[y] = new Button[grid.getWidth()];
            for (int x = 0; x < grid.getWidth(); x++) {
                Button btn = new Button();
                btn.setMinWidth(BUTTON_SIZE);
                btn.setMinHeight(BUTTON_SIZE);
                int finalX = x;
                int finalY = y;
                btn.setOnAction(e -> {
                    if (gameOver) return;

                    grid.uncover(finalX, finalY);
                    updateButtonGrid();
                    // End game with loss if finalX, finalY was a mine
                    if (grid.isMine(finalX, finalY)) {
                        grid.uncoverAllMines();
                        updateButtonGrid();
                        gameOver = true;
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("You lost!");
                        alert.setHeaderText("You lost!");
                        alert.setContentText("BANG! You lost the game!");
                        alert.showAndWait();
                    }

                    // End game with win if everything uncovered
                    if (grid.allUncovered()) {
                        gameOver = true;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("You won!");
                        alert.setHeaderText("You won!");
                        alert.setContentText("You won the game!");
                        alert.showAndWait();
                    }
                });
                btn.setOnContextMenuRequested(e -> {
                    grid.flag(finalX, finalY);
                    updateButtonGrid();
                });
                gridPane.add(btn, x, y);
                btnGrid[y][x] = btn;
            }
        }
    }

    private void updateButtonGrid() {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                btnGrid[y][x].setDisable(false);
                btnGrid[y][x].setText(" ");
                if (grid.isFlagged(x, y)) {
                    btnGrid[y][x].setText("F");
                    continue;
                }
                if (!grid.isUncovered(x, y)) {
                    continue;
                }
                if (grid.isMine(x, y)) {
                    // Only for uncovered mines on game loss.
                    btnGrid[y][x].setText("X");
                    continue;
                }

                byte pos = grid.get(x, y);
                btnGrid[y][x].setDisable(true);
                if ((pos & NUMBER) != 0) {
                    btnGrid[y][x].setText(String.format("%d", pos & NUMBER));
                }
            }
        }
    }

    public static void startGame() {
        launch();
    }
}
