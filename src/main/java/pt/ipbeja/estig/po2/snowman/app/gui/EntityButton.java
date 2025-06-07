package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.app.model.MobileEntity;
import pt.ipbeja.estig.po2.snowman.app.model.PositionContent;
import pt.ipbeja.estig.po2.snowman.app.model.SnowballType;

import java.io.InputStream;

/**
 * EntityButton is a custom Button that can display either a monster or a snowball image.
 * It maintains state flags for whether a monster or a snowball should be shown,
 * and updates its graphic accordingly.
 * @author João Silva
 * @author Paulo Neves
 */
public class EntityButton extends Button {

    private static Image backgroundImage;
    private int size = 100;

    //flag indicating if the objects are visible
    private boolean hasMonster = false;
    private boolean hasSnowball = false;

    /**
     * Constructs an EntityButton initialized with either a monster or a small snowball image,
     * depending on the provided MobileEntity.
     *
     * @param content the type of entity (MONSTER or SNOWBALL) this button initially represents
     */
    public EntityButton(MobileEntity content) {
        ImageView imageView = null;

        /// Choose initial image based on entity type
        if (content == MobileEntity.MONSTER) {
            backgroundImage = new Image("/monster1.png");
            imageView = new ImageView(backgroundImage);
        } else if (content == MobileEntity.SNOWBALL) {
            backgroundImage = new Image("/ballsmall.png");
            imageView = new ImageView(backgroundImage);
        }

        // ImageView dimensions
        if (imageView != null) {
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(false);

            this.setGraphic(imageView);
        } else {
            this.setGraphic(null);
        }

        this.setMinSize(size, size);
        this.setMaxSize(size, size);
        this.setPrefSize(size, size);
        this.setPadding(javafx.geometry.Insets.EMPTY);
        this.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0;");
        this.setFocusTraversable(false);
    }

    /// Clears any entity graphic from the button.
    public void clearEntity() {
        this.setGraphic(null);
    }

    /// Sets the monster visibility flag and updates the button graphic accordingly.
    public void setMonsterVisible(boolean visible) {
        this.hasMonster = visible;
        updateGraphic();
    }

    ///  Returns the correct image path for a given SnowballType.
    private String getImagePathForType(SnowballType type) {
        return switch (type) {
            case SMALL -> "/ballsmall.png";
            case MID -> "/ballmedium.png";
            case BIG -> "/bigball.png";
            case MID_SMALL -> "/midsmall.png";
            case BIG_SMALL -> "/bigsmall.png";
            case BIG_MID -> "/bigmid.png";
            case COMPLETE -> "/snowman.png";
        };
    }

    /// Updates the button to display a snowball image corresponding to the given type.
    public void setSnowballType(SnowballType type) {
        String imagePath = getImagePathForType(type);

        try {
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream == null) {
                System.err.println("FILE NOT FOUND: " + imagePath);
                return;
            }

            Image image = new Image(stream);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            this.setGraphic(imageView);
        } catch (Exception e) {
            System.err.println("ERROR LOADING THE IMAGE: " + e);
        }
    }

    /**
     * Updates the button graphic based on current visibility flags.
     * If hasMonster is true, shows the monster image; else if hasSnowball is true,
     * shows a default small snowball; otherwise, clears the graphic.
     */
    private void updateGraphic() {
        if (hasMonster) {
            ImageView imageView = new ImageView(new Image("/monster1.png"));
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(false);
            this.setGraphic(imageView);
        } else if (hasSnowball) {
            ImageView imageView = new ImageView(new Image("/ballsmall.png"));
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(false);
            this.setGraphic(imageView);
        } else {
            this.setGraphic(null);
        }
    }


}
