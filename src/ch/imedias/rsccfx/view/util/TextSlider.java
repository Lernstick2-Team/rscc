package ch.imedias.rsccfx.view.util;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Slider;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * Represents a custom slider which has the value on the top of the thumb at all times.
 */
public class TextSlider extends StackPane {

  private final Slider slider;
  private final Text valueText = new Text();

  /**
   * Initializes a new slider with the value as a text on top of the thumb.
   *
   * @param min   minimum value of the slider.
   * @param max   maximum value of the slider.
   * @param value standard value of the slider.
   */
  public TextSlider(int min, int max, int value) {
    this.setAlignment(Pos.CENTER);
    this.setPadding(new Insets(40, 0, 0, 0));

    slider = new Slider(min, max, value) {
      @Override
      protected void layoutChildren() {
        super.layoutChildren();

        Region thumb = (Region) lookup(".thumb");
        if (thumb != null) {
          valueText.setLayoutX(
              thumb.getLayoutX()
                  + thumb.getWidth() / 2
                  - valueText.getLayoutBounds().getWidth() / 2
          );
          valueText.setLayoutY(10);
        }
      }
    };

    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);

    valueText.setTextOrigin(VPos.TOP);
    valueText.textProperty().bind(
        slider.valueProperty().asString("%,.0f"));
    valueText.getStyleClass().add("sliderTxts");

    getChildren().addAll(valueText, slider);

  }

  public DoubleProperty sliderValueProperty() {
    return slider.valueProperty();
  }

}

