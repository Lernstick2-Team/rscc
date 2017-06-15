package ch.imedias.rsccfx.view.util;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents the status bar which displays the current state of the connection.
 */
public class StatusBar extends HBox {

  final Label statusLbl = new Label();

  /**
   * Constructs a StatusBar.
   */
  public StatusBar() {
    this.getStyleClass().add("statusBar");
    this.getChildren().add(statusLbl);
    statusLbl.getStyleClass().add("statusLbl");
  }

  /**
   * Sets Listeners on properties to keep the StatusBar updated.
   *
   * @param textProperty       property containing the text to display.
   * @param styleClassProperty proprty containing the css style class to display.
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
