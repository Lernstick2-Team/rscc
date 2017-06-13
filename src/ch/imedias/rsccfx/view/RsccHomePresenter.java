package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.ControlledPresenter;
import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.ViewController;
import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import java.util.logging.Logger;
import javafx.scene.Scene;

/**
 * Defines the behaviour of interactions
 * and initializes the size of the GUI components.
 */
public class RsccHomePresenter implements ControlledPresenter {
  private static final Logger LOGGER =
      Logger.getLogger(RsccHomePresenter.class.getName());

  private static final Double IMG_VIEW_DIVISOR = 3d;
  private static final Double VIEW_BTN_HEIGHT_DIVISOR = 2.5d;
  private static final Double VIEW_BTN_WIDTH_DIVISOR = 1.5d;

  private final Strings strings = new Strings();
  private final Rscc model;
  private final RsccHomeView view;
  private final HeaderPresenter headerPresenter;
  private ViewController viewParent;
  private PopOverHelper popOverHelper;

  /**
   * Initializes a new RsccHomePresenter with the matching view.
   *
   * @param model model with all data.
   * @param view  the view belonging to the presenter.
   */
  public RsccHomePresenter(Rscc model, RsccHomeView view) {
    this.model = model;
    this.view = view;
    headerPresenter = new HeaderPresenter(model, view.headerView);
    attachEvents();
    initHeader();
    popOverHelper = new PopOverHelper(model, RsccApp.HOME_VIEW);
  }

  /**
   * Defines the ViewController to allow changing of views.
   */
  public void setViewParent(ViewController viewParent) {
    this.viewParent = viewParent;
  }

  /**
   * Initializes the size of the whole RsccHomeView elements.
   *
   * @param scene must be initialized and displayed before calling this method;
   *              The size of all header elements are based on it.
   * @throws NullPointerException if called before this object is fully initialized.
   */
  public void initSize(Scene scene) {
    view.requestImgView.fitHeightProperty().bind(scene.heightProperty()
        .subtract(view.headerView.heightProperty()).divide(IMG_VIEW_DIVISOR));
    view.supportImgView.fitHeightProperty().bind(scene.heightProperty()
        .subtract(view.headerView.heightProperty()).divide(IMG_VIEW_DIVISOR));

    view.supportViewBtn.prefWidthProperty().bind(scene.widthProperty()
        .divide(VIEW_BTN_WIDTH_DIVISOR));
    view.supportViewBtn.prefHeightProperty().bind(scene.heightProperty()
        .subtract(view.headerView.heightProperty()).divide(VIEW_BTN_HEIGHT_DIVISOR));

    view.requestViewBtn.prefWidthProperty().bind(scene.widthProperty()
        .divide(VIEW_BTN_WIDTH_DIVISOR));
    view.requestViewBtn.prefHeightProperty().bind(scene.heightProperty()
        .subtract(view.headerView.heightProperty()).divide(VIEW_BTN_HEIGHT_DIVISOR));
  }

  private void attachEvents() {
    view.supportViewBtn.setOnAction(event -> {
      model.setStatusBarKeyInput(view.strings.statusBarPleaseEnterKey,
          model.STATUS_BAR_STYLE_INITIALIZE);
      viewParent.setView(RsccApp.SUPPORT_VIEW);
    });
    view.requestViewBtn.setOnAction(event -> {
      Thread thread = new Thread(model::requestKeyFromServer);
      thread.start();
      viewParent.setView(RsccApp.REQUEST_VIEW);
    });
  }

  private void initHeader() {
    // set all the actions regarding buttons in this method
    headerPresenter.setBackBtnVisibility(false);
    headerPresenter.setSettingsBtnVisibility(false);
    headerPresenter.setHelpBtnAction(event ->
        popOverHelper.helpPopOver.show(view.headerView.helpBtn));
  }
}
