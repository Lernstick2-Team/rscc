package ch.imedias.rsccfx.view;

import ch.imedias.rscc.ProcessExecutor;
import ch.imedias.rsccfx.ControlledPresenter;
import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.ViewController;
import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.util.KeyUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * Presenter class of RsccSupportView. Defines the behaviour of interactions
 * and initializes the size of the GUI components.
 * The supporter can enter the key given from the help requester to establish a connection.
 */
public class RsccSupportPresenter implements ControlledPresenter {
  private static final Logger LOGGER =
      Logger.getLogger(RsccSupportPresenter.class.getName());

  private static final double WIDTH_SUBTRACTION_ENTERKEY = 100d;

  private String validImage =
      getClass().getClassLoader().getResource("images/valid.svg").toExternalForm();
  private String invalidImage =
      getClass().getClassLoader().getResource("images/invalid.svg").toExternalForm();

  private final Rscc model;
  private final RsccSupportView view;
  private final HeaderPresenter headerPresenter;
  private final KeyUtil keyUtil;
  private final BooleanProperty serviceRunning = new SimpleBooleanProperty(false);
  Task startServiceTask;
  ProcessExecutor offerProcessExecutor = new ProcessExecutor();
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
    startServiceTask = createService();
  }

  private void initImages() {
    view.validationImg.load(invalidImage);
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

  /**
   * Updates the validation image after every key pressed.
   */
  private void attachEvents() {
    view.connectBtn.setOnAction(event -> model.connectToUser());

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
            if (newValue) {
              view.startServiceTitledPane.setExpanded(false);
              view.contentBox.getChildren().removeAll(view.startServiceInnerPane);
              view.contentBox.getChildren().add(1, view.keyInputInnerPane);
              model.setConnectionStatus("", 0);
            }
          }
        }
    );
    view.startServiceTitledPane.expandedProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (oldValue != newValue) {
            if (newValue) {
              view.keyInputTitledPane.setExpanded(false);
              view.contentBox.getChildren().removeAll(view.keyInputInnerPane);
              view.contentBox.getChildren().add(2, view.startServiceInnerPane);
              model.setConnectionStatus(view.strings.statusBoxServiceIdle, 0);
            }
          }
        }
    );

    // handles statusBox updates from connectionStatus property in model
    model.connectionStatusStyleProperty().addListener((observable, oldValue, newValue) -> {
      view.statusBox.getStyleClass().clear();
      view.statusBox.getStyleClass().add(newValue);
    });
    model.connectionStatusTextProperty().addListener((observable, oldValue, newValue) -> {
      Platform.runLater(() -> {
        view.statusLbl.textProperty().set(newValue);
      });
    });

    // make it possible to connect by pressing enter
    view.keyFld.setOnKeyPressed(ke -> {
      if (ke.getCode() == KeyCode.ENTER && keyUtil.isKeyValid()) {
        model.connectToUser();
      }
    });

    // initial start of service
    view.startServiceBtn.setOnAction(event -> new Thread(createService()).start());

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
    view.keyInputTitledPane.disableProperty().bind(serviceRunningProperty());
    view.startServiceTitledPane.disableProperty().bind(serviceRunningProperty());
    view.headerView.backBtn.disableProperty().bind(serviceRunningProperty());
    view.headerView.settingsBtn.disableProperty().bind(serviceRunningProperty());
    view.headerView.helpBtn.disableProperty().bind(serviceRunningProperty());

    // react if the service is running or is being stopped
    serviceRunningProperty().addListener((observable, oldValue, newValue) -> {
          if (oldValue != newValue) {
            if (newValue) {
              // change layout to running state
              view.startServiceBtn.setOnAction(event2 -> startServiceTask.cancel());
              view.startServiceBtn.setText(view.strings.stopService);
              model.setConnectionStatus(view.strings.statusBoxServiceStarted, 2);
            } else {
              endService();
              // prepare to offer again
              startServiceTask = createService();
              view.startServiceBtn.setOnAction(event2 -> new Thread(startServiceTask).start());
              view.startServiceBtn.setText(view.strings.startService);
              model.setConnectionStatus(view.strings.statusBoxServiceStopped, 3);
            }
          }
        }
    );
  }

  private void initBindings() {
    // disable connect button if key is NOT valid
    view.connectBtn.disableProperty().bind(keyUtil.keyValidProperty().not());
  }

  /**
   * Initializes the functionality of the header, e.g. back and settings button.
   */
  private void initHeader() {
    // Set all the actions regarding buttons in this method.
    headerPresenter.setBackBtnAction(event -> viewParent.setView("home"));
    headerPresenter.setHelpBtnAction(event ->
        popOverHelper.helpPopOver.show(view.headerView.helpBtn));
    headerPresenter.setSettingsBtnAction(event ->
        popOverHelper.settingsPopOver.show(view.headerView.settingsBtn));
    // TODO: Set actions on buttons (Help, Settings)
  }

  private Task createService() {
    Task task = new Task<Void>() {
      @Override
      public Void call() {
        Number compression = model.getVncCompression();
        Number quality = model.getVncQuality();
        List<String> commandList = new ArrayList<>();
        commandList.add("xtightvncviewer");
        commandList.add("-listen");
        commandList.add("-compresslevel");
        commandList.add(compression.toString());
        commandList.add("-quality");
        commandList.add(quality.toString());
        if (model.getVncBgr233()) {
          commandList.add("-bgr233");
        }
        offerProcessExecutor.executeProcess(commandList.toArray(
            new String[commandList.size()]));
        return null;
      }
    };
    task.setOnRunning(event -> setServiceRunning(true));
    task.setOnCancelled(event -> setServiceRunning(false));
    return task;
  }

  private void endService() {
    // end the offering process
    offerProcessExecutor.destroy();
    ProcessExecutor processExecutor = new ProcessExecutor();
    processExecutor.executeProcess("killall", "-9", "stunnel4");
  }

  public boolean isServiceRunning() {
    return serviceRunning.get();
  }

  public void setServiceRunning(boolean serviceRunning) {
    this.serviceRunning.set(serviceRunning);
  }

  public BooleanProperty serviceRunningProperty() {
    return serviceRunning;
  }
}
