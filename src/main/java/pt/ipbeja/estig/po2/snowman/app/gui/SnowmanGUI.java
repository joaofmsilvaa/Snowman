package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;

public class SnowmanGUI extends Application {
    @Override
    public void start(Stage stage) {

        //BoardModel boardModel = new BoardModel(10, 10); // Tamanho 10x10
        //SnowmanBoard snowmanBoard = new SnowmanBoard(boardModel);

        //
        Pane root = new Pane();
        //root.getChildren().add(snowmanBoard);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}