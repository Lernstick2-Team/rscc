package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;

/**
 * Defines the behaviour of interaction and initializes the size of the GUI components.
 */
public class HeaderPresenter {
  private final Rscc model;
  private final HeaderView view;

  /**
   * Initializes a new HeaderPresenter with the matching view.
   */
  public HeaderPresenter(Rscc model, HeaderView view) {
    this.model = model;
    this.view = view;
  }

  /**
   * Sets the behaviour of the back button in the HeaderView.
   *
   * @param action is used to give the method a lambda expression and defines what happens
   *               when the back button is clicked.
   */
  public void setBackBtnAction(EventHandler<ActionEvent> action) {
    view.backBtn.setOnAction(action);
  }

  /**
   * Sets the title in the center of the HeaderView.
   *
   * @param title new title for the header.
   */
  public void setHeaderTitle(String title) {
    view.headLbl.textProperty().set(title);
  }

  /**
   * Sets the behaviour of the help button in the HeaderView.
   *
   * @param action is used to give the method a lambda expression and defines what happens
   *               when the help button is clicked.
   */
  public void setHelpBtnAction(EventHandler<ActionEvent> action) {
    view.helpBtn.setOnAction(action);
  }

  /**
   * Sets the behaviour of the settings button in the HeaderView.
   *
   * @param action is used to give the method a lambda expression and defines what happens
   *               when the settings button is clicked.
   */
  public void setSettingsBtnAction(EventHandler<ActionEvent> action) {
    view.settBtn.setOnAction(action);
  }

  /**
   * Initializes the size of the HeaderView.
   *
   * @param scene must be initialized and displayed before calling this method.
   *              The size of all header elements are based on it.
   * @throws NullPointerException if called before this object is fully initialized.
   */
  public void initSize(Scene scene) {
    view.headLbl.prefWidthProperty().bind(scene.widthProperty());
    view.headerBox.prefWidthProperty().bind(scene.widthProperty());
  }

}