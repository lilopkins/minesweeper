package uk.hpkns.minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GUIGame extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Label l = new Label("Minesweeper!");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void startGame(int size) {
        launch();
    }
}
