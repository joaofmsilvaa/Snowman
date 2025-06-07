package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * @author João Silva
 * @author Paulo Neves
 */
public class StartMenu {

    public static void show(Stage stage) {
        VBox layout = createLayout();
        Scene scene = new Scene(new StackPane(layout), 600, 600);
        stage.setScene(scene);
        stage.setTitle("Snowman - Menu");
        stage.show();
    }

    private static VBox createLayout() {
        Label title1 = createLabel("A Good Snowman Is Hard To Build", 28);
        Label title2 = createLabel("PO2 edition", 26);
        Label credits = createLabel("Made by: João Silva and Paulo Neves", 16);
        Label nameLabel = createLabel("Player Name:", 16);

        TextField nameField = new TextField();
        nameField.setPromptText("Your name (3 letters only)");
        nameField.setMaxWidth(120);

        Button startGame = createStartButton(nameField);

        HBox images = new HBox(40, createImage("/monster1.png"), createImage("/snowman.png"));
        images.setAlignment(Pos.CENTER);
        images.setStyle("-fx-background-color: white;");

        HBox nameInput = new HBox(5, nameLabel, nameField);
        nameInput.setAlignment(Pos.CENTER);
        nameInput.setStyle("-fx-background-color: white;");

        VBox titleBox = new VBox(20, title1, title2);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setStyle("-fx-background-color: white;");

        VBox layout = new VBox(40, titleBox, startGame, images, nameInput, credits);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white;");
        return layout;
    }

    private static Label createLabel(String text, int fontSize) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: bold;");
        return label;
    }

    private static ImageView createImage(String path) {
        ImageView view = new ImageView(new Image(path));
        view.setFitWidth(80);
        view.setFitHeight(80);
        return view;
    }

    private static Button createStartButton(TextField nameField) {
        Button button = new Button("Start Game");
        button.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        button.setOnAction(e -> handleStart(nameField.getText()));
        return button;
    }

    private static void handleStart(String name) {
        String playerName = name.trim().toUpperCase();
        if (playerName.length() != 3) {
            showAlert("Error", "You must use only 3 character", "Please, insert only 3 character");
        } else {
            startLevel("map1.txt", playerName);
        }
    }

    private static void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static void startLevel(String fileName, String playerName) {
        new SnowmanGUI(fileName, playerName).start(new Stage());
    }
}
