package ch.imedias.rsccfx.view.util;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Created by François Martin on 13.06.17.
 */
public class StatusBar extends HBox {

  final Label statusLbl = new Label();

  /**
   * Constructor for a ne StatusBar.
   */
  public StatusBar() {
    this.getStyleClass().add("statusBar");
    this.getChildren().add(statusLbl);
    statusLbl.getStyleClass().add("statusLbl");
  }

  /**
   * Sets Listeners on properties to have the StatusBar updated.
   *
   * @param textProperty       property containing the text to display.
   * @param styleClassProperty proprty containing the style to display.
   */
  public void setStatusProperties(StringProperty textProperty, StringProperty styleClassProperty) {
    textProperty.addListener((observable, oldValue, newValue) -> {
      Platform.runLater(() -> {
        statusLbl.textProperty().set(newValue);
      });
    });
    styleClassProperty.addListener((observable, oldValue, newValue) -> {
      if (oldValue != newValue) {
        Platform.runLater(() -> {
          this.getStyleClass().clear();
          this.getStyleClass().add(newValue);
        });
      }
    });
  }

}
