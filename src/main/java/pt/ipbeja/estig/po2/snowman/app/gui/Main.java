package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.gui.StartMenu;
import static pt.ipbeja.estig.po2.snowman.app.gui.StartMenu.show;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        show(primaryStage);  // Mostra o menu inicial
    }
}
