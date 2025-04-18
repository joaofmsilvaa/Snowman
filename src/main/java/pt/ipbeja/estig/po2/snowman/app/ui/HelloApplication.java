package pt.ipbeja.estig.po2.snowman.app.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.Model;

import java.util.Optional;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {

        String name = askName();

        Model model = new Model(name);

        String greeting = model.sayHello();

        Label greetingLabel = createLabel(greeting);

        StackPane pane = new StackPane(greetingLabel);

        Scene scene = new Scene(pane, 800, 600);
        stage.setTitle(HelloApplication.class.getSimpleName());
        stage.setScene(scene);
        stage.show();
    }

    private static Label createLabel(String greeting) {
        Label greetingLabel = new Label(greeting);
        greetingLabel.setFont(Font.font(36));
        return greetingLabel;
    }

    private static String askName() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("What is your name?");
        nameDialog.setHeaderText("Hello!");
        nameDialog.setContentText("Insert your name");
        nameDialog.setGraphic(null);
        Optional<String> maybeName = nameDialog.showAndWait();

        return maybeName.orElse("Maria");
    }

    public static void main(String[] args) {
        launch();
    }
}