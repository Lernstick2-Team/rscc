package ch.imedias.rsccfx.view.util;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Created by François Martin on 13.06.17.
 */
public class StatusBar extends HBox {

  StringProperty status;
  StringProperty cssTag;

  final Label statusLbl = new Label();

  public StatusBar(StringProperty status, StringProperty cssTag) {
    this.status = status;
    this.cssTag = cssTag;
    this.getStyleClass().add("statusBar");
    this.getChildren().add(statusLbl);
    statusLbl.getStyleClass().add("statusLbl");
  }




}
