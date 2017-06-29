package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.ControlledPresenter;
import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.ViewController;
import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.util.KeyUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Presenter class of RsccSupportView. Defines the behaviour of interactions
 * and initializes the size of the GUI components.
 * The supporter can enter the key given from the help requester to establish a connection.
 */
public class RsccSupportPresenter implements ControlledPresenter {
  private static final Logger LOGGER =
      LogManager.getLogger(RsccSupportPresenter.class.getName());

  private static final double WIDTH_SUBTRACTION_ENTERKEY = 100d;
  private final Rscc model;
  private final RsccSupportView view;
  private final HeaderPresenter headerPresenter;
  private final KeyUtil keyUtil;
  private String validImage =
      getClass().getClassLoader().getResource("images/valid.svg").toExternalForm();
  private String invalidImage =
      getClass().getClassLoader().getResource("images/invalid.svg").toExternalForm();
  private ViewController viewParent;
  private PopOverHelper popOverHelper;

  /**
   * Initializes a new RsccSupportPresenter with the according view.
   *
   * @param model model with all data.
   * @param view  the view belonging to the presenter.
   */
  public RsccSupportPresenter(Rscc model, RsccSupportView view) {
    this.model = model;
    this.view = view;
    this.keyUtil = model.getKeyUtil();
    initImages();
    headerPresenter = new HeaderPresenter(model, view.headerView);
    attachEvents();
    initHeader();
    initBindings();
    popOverHelper = new PopOverHelper(model, RsccApp.SUPPORT_VIEW);
  }

  private void initImages() {
    String validationImage = keyUtil.isKeyValid() ? validImage : invalidImage;
    Platform.runLater(() -> view.validationImg.load(validationImage));
  }

  /**
   * Defines the ViewController to allow changing of views.
   */
  public void setViewParent(ViewController viewParent) {
    this.viewParent = viewParent;
  }

  /**
   * Initializes the size of the whole RsccSupportView elements.
   *
   * @param scene must be initialized and displayed before calling this method;
   *              The size of all header elements are based on it.
   * @throws NullPointerException if called before this object is fully initialized.
   */
  public void initSize(Scene scene) {
    // initialize view

    view.keyFld.prefWidthProperty().bind(scene.widthProperty()
        .subtract(WIDTH_SUBTRACTION_ENTERKEY));

  }

  private void attachEvents() {
    view.connectBtn.setOnAction(event -> {
      Thread thread = new Thread(model::connectToUser);
      thread.start();
    });

    // formats the key while typing
    StringProperty key = view.keyFld.textProperty();
    key.addListener(
        (observable, oldKey, newKey) -> {
          // set the key in KeyUtil and get the formatted version
          keyUtil.setKey(key.get());
          key.setValue(keyUtil.getFormattedKey());
        }
    );

    // handles TitledPane switching between the two TitledPanes
    view.keyInputTitledPane.expandedProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (oldValue != newValue) {
            if (newValue && view.startServiceTitledPane.isExpanded()) {
              view.startServiceTitledPane.setExpanded(false);
              view.contentBox.getChildren().removeAll(view.startServiceInnerPane);
              view.contentBox.getChildren().add(1, view.keyInputInnerPane);
            }
            // keep titledPane open if it was closed by clicking on it while it is already open
            if (oldValue && !view.startServiceTitledPane.isExpanded()) {
              view.keyInputTitledPane.setExpanded(true);
            }
          }
        }
    );
    view.startServiceTitledPane.expandedProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (oldValue != newValue) {
            if (newValue && view.keyInputTitledPane.isExpanded()) {
              view.keyInputTitledPane.setExpanded(false);
              view.contentBox.getChildren().removeAll(view.keyInputInnerPane);
              view.contentBox.getChildren().add(2, view.startServiceInnerPane);
            }
            // keep titledPane open if it was closed by clicking on it while it is already open
            if (oldValue && !newValue && !view.keyInputTitledPane.isExpanded()) {
              view.startServiceTitledPane.setExpanded(true);
            }
          }
        }
    );

    view.statusBarKeyInput.setStatusProperties(model.statusBarTextKeyInputProperty(),
        model.statusBarStyleClassKeyInputProperty());

    view.statusBarStartService.setStatusProperties(model.statusBarTextStartServiceProperty(),
        model.statusBarStyleClassStartServiceProperty());

    // make it possible to connect by pressing enter
    view.keyFld.setOnKeyPressed(ke -> {
      if (ke.getCode() == KeyCode.ENTER && keyUtil.isKeyValid()) {
        Thread thread = new Thread(model::connectToUser);
        thread.start();
      }
    });

    // change valid image depending on if the key is valid or not
    keyUtil.keyValidProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (oldValue != newValue) {
            if (newValue) {
              Platform.runLater(() -> view.validationImg.load(validImage));
            } else {
              Platform.runLater(() -> view.validationImg.load(invalidImage));
            }
          }
        }
    );

    // when the service is running, disable all interactions
    view.headerView.settingsBtn.disableProperty().bind(model.vncViewerProcessRunningProperty());
    view.headerView.backBtn.disableProperty().bind(model.vncViewerProcessRunningProperty());

    view.startServiceBtn.disableProperty().bind(model.connectionEstablishmentRunningProperty());

    view.startServiceBtn.setOnAction(event -> {
      if (model.isVncViewerProcessRunning()) {
        view.startServiceBtn.setText(view.strings.startService);
        Thread thread = new Thread(model::stopVncViewerAsService);
        thread.start();
      } else {
        Thread thread = new Thread(model::startVncViewerAsService);
        thread.start();
        view.startServiceBtn.setText(view.strings.stopService);
      }
    });

    model.vncSessionRunningProperty().addListener((observableValue, oldValue, newValue) -> {
          if (oldValue && !newValue
              && RsccApp.SUPPORT_VIEW.equals(viewParent.getCurrentViewName())) {
            model.killConnection();
            view.keyFld.clear();
          }
        }
    );

  }

  private void initBindings() {
    // disable connect button if key is NOT valid
    view.connectBtn.disableProperty().bind(
        Bindings.or(
            model.connectionEstablishmentRunningProperty(),
            keyUtil.keyValidProperty().not())
    );
  }

  /**
   * Initializes the functionality of the header, e.g. back and settings button.
   */
  private void initHeader() {
    // Set all the actions regarding buttons in this method.
    headerPresenter.setBackBtnAction(event -> {
      viewParent.setView("home");
      view.keyFld.clear();
      model.killConnection();
    });

    headerPresenter.setHelpBtnAction(event -> {
      if (popOverHelper.helpPopOver.isShowing()) {
        popOverHelper.helpPopOver.hide();
      } else {
        popOverHelper.helpPopOver.show(view.headerView.helpBtn);
      }
    });
    headerPresenter.setSettingsBtnAction(event -> {
      if (popOverHelper.settingsPopOver.isShowing()) {
        popOverHelper.settingsPopOver.hide();
      } else {
        popOverHelper.settingsPopOver.show(view.headerView.settingsBtn);
      }
    });
  }
}
