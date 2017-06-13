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

  public StatusBar() {
    this.getStyleClass().add("statusBar");
    this.getChildren().add(statusLbl);
    statusLbl.getStyleClass().add("statusLbl");
    setupBindings();
    attachEvents();
  }

  public void setupBindings(){
    statusLbl.textProperty().bind(status);
  }

  public void attachEvents(){
    cssTag.addListener((observable, oldValue, newValue) -> {
      if (oldValue != newValue) {
        this.getStyleClass().clear();
        this.getStyleClass().add(newValue);
      }
    });
  }

  public void setStatus(StringProperty status){
    this.status.unbind();
    this.status.bind(status);
  }

  public void setCssTag(StringProperty cssTag){
    this.cssTag.unbind();
    this.cssTag.bind(cssTag);
  }




}
