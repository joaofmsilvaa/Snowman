package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import  javafx.scene.control.TextField;

public class StartMenu {

    public static void show(Stage stage) {

        Label title = new Label("A good Snow is hard to build");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        Button level1 = new Button("Level 1");
        level1.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");

        Button level2 = new Button("Level 2");
        level2.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");


        TextField name = new TextField();
        name.setPromptText("Nome: ");
        name.setMaxWidth(120);


        level1.setOnAction(e -> startLevel("map1.txt", name.getText()));
        level2.setOnAction(e -> startLevel("map2.txt", name.getText()));



        ImageView monsterView = new ImageView(new Image("/monster1.png"));
        monsterView.setFitWidth(80);
        monsterView.setFitHeight(80);

        ImageView snowmanView = new ImageView(new Image("/snowman.png"));
        snowmanView.setFitWidth(80);
        snowmanView.setFitHeight(80);

        HBox buttons = new HBox(40);
        buttons.setAlignment(Pos.CENTER);
        buttons.setStyle("-fx-background-color: white;");
        buttons.getChildren().addAll(level1, level2);

        HBox Images = new HBox(40);
        Images.setAlignment(Pos.CENTER);
        Images.setStyle("-fx-background-color: white;");
        Images.getChildren().addAll(monsterView, snowmanView);

        VBox layout = new VBox(40);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white;");

        layout.getChildren().addAll(title, buttons, Images, name);


        Scene scene = new Scene(new StackPane(layout), 600, 600);
        stage.setScene(scene);
        stage.setTitle("Snowman - Menu");
        stage.show();
    }
    private static void startLevel(String fileName, String playerName){

        SnowmanGUI gui = new SnowmanGUI(fileName, playerName );
        gui.start(new Stage());
    }
}