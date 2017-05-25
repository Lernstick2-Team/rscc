package ch.imedias.rsccfx;

import java.util.HashMap;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Displays all of the views and handles the switching between views.
 * Because of the interface "ControlledPresenter", this object gets passed onto every presenter,
 * which gives them access to the methods in this class.
 */
public class ViewController extends StackPane {
  private static final Logger LOGGER =
      Logger.getLogger(ViewController.class.getName());

  private final HashMap<String, Node> views = new HashMap<>();
  private final HashMap<String, ControlledPresenter> presenters = new HashMap<>();

  private StringProperty currentViewName = new SimpleStringProperty("");

  /**
   * Returns an already loaded presenter.
   *
   * @param name of the view / presenter
   * @return presenter object
   */
  public ControlledPresenter getPresenter(String name) {
    return presenters.get(name);
  }

  /**
   * Adds a view / presenter pair to the respective HashMaps.
   */
  private void addView(String name, Node view, ControlledPresenter presenter) {
    views.put(name, view);
    presenters.put(name, presenter);
  }

  /**
   * Loads a view / presenter pair and sets up a reference in the presenter to this object.
   * Usually only needs to be called once for every pair before the start of the app.
   *
   * @param name      of the view / presenter pair
   * @param view      to be loaded
   * @param presenter to be loaded
   */
  public void loadView(String name, Node view, ControlledPresenter presenter) {
    // properly initialize view and presenter and put into HashMap
    presenter.setViewParent(this);
    addView(name, view, presenter);
  }

  /**
   * Sets the current view to the one referenced by 'name'.
   * This method can be called in the presenter to switch to a different view.
   * Controls the way views are being transitioned from one to another.
   *
   * @param name of the view that will be set
   * @return true if successfully loaded, false if view is nonexistent
   */
  public boolean setView(final String name) {
    if (views.get(name) != null) { // view is loaded
      // If at least one view is already being displayed
      if (!getChildren().isEmpty()) {
        //remove displayed view
        getChildren().remove(0);
        //add new view
        getChildren().add(0, views.get(name));
      } else {
        // No view is currently being displayed, so just add it
        getChildren().add(views.get(name));
      }
      currentViewName.setValue(name);
      return true;
    } else {
      LOGGER.info("View " + name + " hasn't been loaded!");
      return false;
    }
  }

  /**
   * Unloads a view / presenter pair from the HashMaps.
   * This method can be used in case a view / presenter pair needs to be reloaded.
   *
   * @param name of the view / presenter pair to be unloaded.
   * @return true if the view and presenter were unloaded and false if view doesn't exist.
   */
  public boolean unloadView(String name) {
    if (views.remove(name) == null | presenters.remove(name) == null) {
      LOGGER.info("View " + name + " doesn't exist!");
      return false;
    } else {
      return true;
    }
  }

  public String getCurrentViewName() {
    return currentViewName.getValue();
  }
}
