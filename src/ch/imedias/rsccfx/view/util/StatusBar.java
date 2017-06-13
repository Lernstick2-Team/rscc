package ch.imedias.rsccfx.view.util;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Created by François Martin on 13.06.17.
 */
public class StatusBar extends HBox {

  final Label statusLbl = new Label();

  public StatusBar() {
    this.getStyleClass().add("statusBar");
    this.getChildren().add(statusLbl);
    statusLbl.getStyleClass().add("statusLbl");
  }

  public void setStatusProperties(StringProperty status, StringProperty styleClass){
    statusLbl.textProperty().bind(status);
    styleClass.addListener((observable, oldValue, newValue) -> {
      if (oldValue != newValue) {
        this.getStyleClass().clear();
        this.getStyleClass().add(newValue);
      }
    });
  }

}
