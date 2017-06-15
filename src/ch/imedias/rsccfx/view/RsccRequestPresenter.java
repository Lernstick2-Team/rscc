package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.ControlledPresenter;
import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.ViewController;
import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.xml.Supporter;
import ch.imedias.rsccfx.model.xml.SupporterHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Defines the behaviour of interactions
 * and initializes the size of the GUI components.
 */
public class RsccRequestPresenter implements ControlledPresenter {
  private static final Logger LOGGER =
      Logger.getLogger(RsccRequestPresenter.class.getName());
  private static final int GRID_MAXIMUM_COLUMNS = 3;
  public static List<Supporter> supporters = new ArrayList<>();
  private final Rscc model;
  private final RsccRequestView view;
  private final HeaderPresenter headerPresenter;
  private final SupporterHelper supporterHelper;
  private ViewController viewParent;
  private PopOverHelper popOverHelper;
  private int buttonSize = 0;
  Strings strings = new Strings();


  /**
   * Initializes a new RsccRequestPresenter with the matching view.
   *
   * @param model model with all data.
   * @param view  the view belonging to the presenter.
   */
  public RsccRequestPresenter(Rscc model, RsccRequestView view) {
    this.model = model;
    this.view = view;
    headerPresenter = new HeaderPresenter(model, view.headerView);
    supporterHelper = new SupporterHelper(model);
    initHeader();
    initSupporterList();
    attachEvents();
    setupBindings();
    popOverHelper = new PopOverHelper(model, RsccApp.REQUEST_VIEW);
  }


  /**
   * Defines the ViewController to allow changing views.
   */
  public void setViewParent(ViewController viewParent) {
    this.viewParent = viewParent;
  }

  private void attachEvents() {
    //Disconnects session on button click
    view.disconnectBtn.setOnAction(
        event -> {
          Thread thread = new Thread(model::killConnection);
          thread.start();
        }
    );

    view.reloadKeyBtn.setOnAction(
        event -> {
          Thread thread = new Thread(model::refreshKey);
          thread.start();
        }
    );

    // handles TitledPane switching between the two TitledPanes
    view.keyGenerationTitledPane.expandedProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (oldValue != newValue) {
            if (newValue) {
              view.supporterTitledPane.setExpanded(false);
              view.contentBox.getChildren().removeAll(view.supporterOuterBox);
              view.contentBox.getChildren().add(1, view.keyGenerationInnerPane);
            }
          }
        }
    );

    view.supporterTitledPane.expandedProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (oldValue != newValue) {
            if (newValue) {
              view.keyGenerationTitledPane.setExpanded(false);
              view.contentBox.getChildren().removeAll(view.keyGenerationInnerPane);
              view.contentBox.getChildren().add(2, view.supporterOuterBox);
            }
          }
        }
    );


    model.vncSessionRunningProperty().addListener((observableValue, oldValue, newValue) -> {
          if (oldValue && !newValue
              && RsccApp.REQUEST_VIEW.equals(viewParent.getCurrentViewName())) {
            model.refreshKey();
          }
        }
    );

    view.statusBarKeyGeneration.setStatusProperties(model.statusBarTextKeyGenerationProperty(),
        model.statusBarStyleClassKeyGenerationProperty());

    view.statusBarSupporter.setStatusProperties(model.statusBarTextSupporterProperty(),
        model.statusBarStyleClassSupporterProperty());
  }

  /**
   * Initializes the size of the whole RsccRequestView elements.
   *
   * @param scene must be initialized and displayed before calling this method;
   *              The size of all header elements are based on it.
   * @throws NullPointerException if called before this object is fully initialized.
   */
  public void initSize(Scene scene) {
    // initialize view
    view.supporterDescriptionLbl.prefWidthProperty().bind(scene.widthProperty().divide(3));
    view.supporterInnerPane.prefWidthProperty().bind(scene.widthProperty().divide(3).multiply(2));
    view.reloadKeyBtn.prefHeightProperty().bind(view.generatedKeyFld.heightProperty());
  }

  /**
   * Initializes the functionality of the header, e.g. back button and settings button.
   */
  private void initHeader() {
    // Set all the actions regarding buttons in this method.
    headerPresenter.setBackBtnAction(event -> {
      model.killConnection();
      viewParent.setView(RsccApp.HOME_VIEW);
    });
    headerPresenter.setHelpBtnAction(event ->
        popOverHelper.helpPopOver.show(view.headerView.helpBtn));
    headerPresenter.setSettingsBtnAction(event ->
        popOverHelper.settingsPopOver.show(view.headerView.settingsBtn));

  }

  /**
   * Setup bindings.
   */
  private void setupBindings() {
    headerPresenter.getSettingsBtnDisableProperty().bind(model.vncServerProcessRunningProperty());

    // disable disconnect button if no session is started
    view.disconnectBtn.disableProperty().bind(model.vncSessionRunningProperty().not());
    view.reloadKeyBtn.disableProperty().bind(model.vncSessionRunningProperty());
  }

  /**
   * Calls createSupporterList() and creates a button for every supporter found.
   */
  public void initSupporterList() {
    List<Supporter> loadedSupporters = supporterHelper.loadSupporters();
    // check if invalid format of XML was found during loading
    if (loadedSupporters == null) {
      loadedSupporters = supporterHelper.getDefaultSupporters();
    }

    loadedSupporters.stream().forEachOrdered(this::createNewSupporterBtn);

    supporterHelper.saveSupporters(supporters);
  }

  /**
   * Creates new SupporterButton and adds it to the GridPane.
   * @param supporter the supporter which a button should be created for.
   */
  public void createNewSupporterBtn(Supporter supporter) {
    supporters.add(supporter);

    Button supporterBtn = new Button(supporter.toString());
    supporterBtn.getStyleClass().add("supporterBtn");
    initButtonSize(supporterBtn);
    attachContextMenu(supporterBtn, supporter);

    supporterBtn.setOnAction(event -> {
      openDialog(supporter, supporterBtn, false);
    });

    int row = buttonSize / GRID_MAXIMUM_COLUMNS;
    int column = buttonSize % GRID_MAXIMUM_COLUMNS;
    view.supporterInnerPane.add(supporterBtn, column, row);
    buttonSize++;
  }

  private void openDialog(Supporter supporter, Button supporterBtn, boolean editMode) {
    // Open Dialog to modify data
    SupporterAttributesDialog supporterAttributesDialog =
        new SupporterAttributesDialog(supporter, this.model, editMode);
    boolean supporterSaved = supporterAttributesDialog.show();
    Supporter lastSupporter = supporters.get(supporters.size() - 1);
    if (supporterSaved) {
      if (lastSupporter == supporter) {
        createNewSupporterBtn(new Supporter());
      }
      // Update data in button name and save to preferences
      supporterBtn.setText(supporter.toString());
      supporterHelper.saveSupporters(supporters);
    }
  }

  /**
   * Deletes a SupporterButton.
   */
  public void deleteSupporterBtn(Button button, Supporter supporter) {
    ObservableList<Node> buttonList = view.supporterInnerPane.getChildren();
    int buttonIndex = buttonList.indexOf(button);
    int row = GridPane.getRowIndex(button);
    int column = GridPane.getColumnIndex(button);
    view.supporterInnerPane.getChildren().remove(button);
    for (int i = buttonIndex; i < buttonList.size(); i++) {
      Button nextButton = (Button) buttonList.get(i);
      // copy positions from next button
      final int nextButtonRow = GridPane.getRowIndex(nextButton);
      final int nextButtonCol = GridPane.getColumnIndex(nextButton);
      // set button at new position
      GridPane.setRowIndex(nextButton, row);
      GridPane.setColumnIndex(nextButton, column);
      row = nextButtonRow;
      column = nextButtonCol;
    }
    buttonSize--;

    // remove the supporter and save list.
    supporters.remove(supporter);
    supporterHelper.saveSupporters(supporters);
  }

  private void attachContextMenu(Button button, Supporter supporter) {
    // Create ContextMenu
    final ContextMenu contextMenu = new ContextMenu();

    MenuItem editMenuItem = new MenuItem(strings.dialogEditButtonText);

    editMenuItem.setOnAction(event -> {
      openDialog(supporter, button, true);
    });

    MenuItem connectMenuItem = new MenuItem(strings.dialogConnectButtonText);
    connectMenuItem.setOnAction(event -> {
      model.callSupporterDirect(supporter.getAddress(), supporter.getPort(),
          supporter.isEncrypted());
    });

    MenuItem deleteMenuItem = new MenuItem(strings.dialogDeleteButtonText);
    deleteMenuItem.setOnAction(event -> deleteSupporterBtn(button, supporter));

    // Add MenuItem to ContextMenu
    contextMenu.getItems().addAll(editMenuItem, connectMenuItem, deleteMenuItem);

    // When user right-click on Supporterbutton
    button.setOnContextMenuRequested(event -> {
      if (supporters.get(supporters.size() - 1) != supporter) {
        contextMenu.show(button, event.getScreenX(),
            event.getScreenY());
      }
    });
  }

  private void initButtonSize(Button button) {
    GridPane.setVgrow(button, Priority.ALWAYS);
    GridPane.setHgrow(button, Priority.ALWAYS);
    GridPane.setValignment(button, VPos.CENTER);
    GridPane.setHalignment(button, HPos.CENTER);
    GridPane.setMargin(button, new Insets(10));

    button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    button.setPadding(new Insets(20));
  }
}
