package uk.hpkns.minesweeper;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

import static uk.hpkns.minesweeper.Grid.NUMBER;

public class GUIGame extends Application {

    public static final URL FLAG = Objects.requireNonNull(GUIGame.class.getResource("/flag.png"));
    public static final URL MINE = Objects.requireNonNull(GUIGame.class.getResource("/mine.png"));
    public static final double BUTTON_SIZE = 32d;
    public static final double ICON_SIZE = 16d;
    private Grid grid;
    Button[][] btnGrid;
    private boolean gameOver;
    private long gameStartTime;

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
        gameStartTime = 0;
        grid = new Grid(16);
        gridPane.getChildren().clear();
        btnGrid = new Button[grid.getHeight()][];
        for (int y = 0; y < grid.getHeight(); y++) {
            btnGrid[y] = new Button[grid.getWidth()];
            for (int x = 0; x < grid.getWidth(); x++) {
                Button btn = new Button();
                btn.setMinWidth(BUTTON_SIZE);
                btn.setMinHeight(BUTTON_SIZE);
                btn.setMaxHeight(BUTTON_SIZE);
                btn.setMaxWidth(BUTTON_SIZE);
                int finalX = x;
                int finalY = y;
                btn.setOnAction(e -> {
                    if (gameOver) return;
                    if (grid.isFlagged(finalX, finalY)) return;

                    if (gameStartTime == 0) gameStartTime = System.currentTimeMillis();

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
                        return;
                    }

                    // End game with win if everything uncovered
                    if (grid.allUncovered()) {
                        gameOver = true;
                        String leaderboard = "See the leaderboard at hpkns.uk/minesweeper.";
                        long completionTimeMillis = System.currentTimeMillis() - gameStartTime;

                        TextInputDialog nameDlg = new TextInputDialog();
                        nameDlg.setTitle("Leaderboard");
                        nameDlg.setHeaderText("Your name");
                        nameDlg.setContentText("Enter your name for submission to the leaderboard...");
                        Optional<String> name = nameDlg.showAndWait();

                        if (name.isPresent()) {
                            try {
                                URL url = new URL(String.format("https://minesweeper-leaderboard.hpkns.uk/submit/%s/%d", name.get(), completionTimeMillis));
                                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                                http.setRequestMethod("POST");
                                http.setDoOutput(true);
                                http.getInputStream(); // To send request
                            } catch (IOException ex) {
                                // Probably just not online.
                                leaderboard = "Submission to the leaderboard failed.";
                            }
                        } else {
                            leaderboard += " Your time wasn't submitted.";
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("You won!");
                        alert.setHeaderText("You won!");
                        alert.setContentText(String.format("You won the game in %.2f seconds! %s", ((float) completionTimeMillis) / 1000, leaderboard));
                        alert.showAndWait();
                        return;
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
                btnGrid[y][x].setGraphic(null);
                if (grid.isFlagged(x, y)) {
                    ImageView img = new ImageView(FLAG.toExternalForm());
                    img.setFitWidth(ICON_SIZE);
                    img.setFitHeight(ICON_SIZE);
                    btnGrid[y][x].setGraphic(img);
                    continue;
                }
                if (!grid.isUncovered(x, y)) {
                    continue;
                }
                if (grid.isMine(x, y)) {
                    // Only for uncovered mines on game loss.
                    ImageView img = new ImageView(MINE.toExternalForm());
                    img.setFitWidth(ICON_SIZE);
                    img.setFitHeight(ICON_SIZE);
                    btnGrid[y][x].setGraphic(img);
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
