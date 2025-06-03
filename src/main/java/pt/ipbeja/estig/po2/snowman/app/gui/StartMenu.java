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

/**
 * StartMenu displays the initial menu for the game, allowing the player
 * to enter their name and choose between two levels. It shows two title lines,
 * a credits line, level-selection buttons, decorative images, and a name field.
 */
public class StartMenu {

    /**
     * Builds and shows the start menu on the provided Stage. The menu includes:
     * - Two title labels ("A Good Snowman Is Hard To Build" and "PO2 edition")
     * - A credits label ("Made by: João Silva and Paulo Neves")
     * - Two level buttons ("Level 1" and "Level 2")
     * - Two images (monster and snowman) for decoration
     * - A TextField for the player to enter their name
     *
     * @param stage the Stage on which to display the start menu
     */
    public static void show(Stage stage) {

        //title Labels
        Label title1 = new Label("A Good Snowman Is Hard To Build");
        title1.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        Label title2 = new Label("PO2 edition");
        title2.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        //credits label
        Label credits = new Label("Made by: João Silva and Paulo Neves");
        credits.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        //Start buttons
        Button startgame = new Button("Start Game");
        startgame.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");

        // Text field for the name
        TextField name = new TextField();
        name.setPromptText("Nome: ");
        name.setMaxWidth(120);

        //Action event for the buttons launch the map
        startgame.setOnAction(e -> startLevel("map1.txt", name.getText()));



        // monster
        ImageView monsterView = new ImageView(new Image("/monster1.png"));
        monsterView.setFitWidth(80);
        monsterView.setFitHeight(80);

        // snowman
        ImageView snowmanView = new ImageView(new Image("/snowman.png"));
        snowmanView.setFitWidth(80);
        snowmanView.setFitHeight(80);

        VBox title = new VBox(20);
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-background-color: white;");
        title.getChildren().addAll(title1, title2);


        HBox Images = new HBox(40);
        Images.setAlignment(Pos.CENTER);
        Images.setStyle("-fx-background-color: white;");
        Images.getChildren().addAll(monsterView, snowmanView);

        VBox layout = new VBox(40);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white;");

        //overall layout
        layout.getChildren().addAll(title, startgame, Images, name, credits);


        Scene scene = new Scene(new StackPane(layout), 600, 600);
        stage.setScene(scene);
        stage.setTitle("Snowman - Menu");
        stage.show();
    }

    /**
     * Helper method that creates a new SnowmanGUI instance and starts it
     * on a new Stage, using the specified map file and player name.
     *
     * @param fileName   the map file to load ex: "map1.txt"
     * @param playerName the name entered by the player
     */
    private static void startLevel(String fileName, String playerName){
        SnowmanGUI gui = new SnowmanGUI(fileName, playerName );
        gui.start(new Stage());
    }
}