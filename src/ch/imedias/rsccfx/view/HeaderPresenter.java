package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Defines the behaviour of interaction and initializes the size of the GUI components.
 */
public class HeaderPresenter {
  private static final Logger LOGGER =
      Logger.getLogger(HeaderPresenter.class.getName());
  private final Rscc model;
  private final HeaderView view;

  /**
   * Initializes a new HeaderPresenter with the matching view.
   *
   * @param model model with all data.
   * @param view  the view belonging to the presenter.
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
   * Sets the behaviour of the help button in the HeaderView.
   *
   * @param action is used to give the method a lambda expression and defines what happens
   *               when the help button is clicked.
   */
  public void setHelpBtnAction(EventHandler<ActionEvent> action) {
    view.helpBtn.setOnAction(action);
    //view.createWebHelp();
  }

  /**
   * Sets the behaviour of the settings button in the HeaderView.
   *
   * @param action is used to give the method a lambda expression and defines what happens
   *               when the settings button is clicked.
   */
  public void setSettingsBtnAction(EventHandler<ActionEvent> action) {
    view.settingsBtn.setOnAction(action);
  }

  /**
   * Makes the back button in the header invisible or visible.
   * Is being used in the HomeView to make the back button invisible,
   * since there is no previous page to it.
   *
   * @param isVisible value to set the visibilty of the button to.
   */
  public void setBackBtnVisibility(Boolean isVisible) {
    view.backBtn.setVisible(isVisible);
  }

  /**
   * Makes the settings button in the header invisible or visible.
   * Is being used in the HomeView to make the settings button invisible,
   * since there are no settings in it.
   * @param isVisible defines if the button should be visible or not.
   */
  public void setSettingsBtnVisibility(Boolean isVisible) {
    view.settingsBtn.setVisible(isVisible);
  }

  public BooleanProperty getSettingsBtnDisableProperty() {
    return view.settingsBtn.disableProperty();
  }

}
