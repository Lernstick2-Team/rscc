package ch.imedias.rsccfx;

import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.SystemCommander;
import ch.imedias.rsccfx.model.util.KeyUtil;
import ch.imedias.rsccfx.view.HeaderPresenter;
import ch.imedias.rsccfx.view.RsccHomePresenter;
import ch.imedias.rsccfx.view.RsccHomeView;
import ch.imedias.rsccfx.view.RsccRequestPresenter;
import ch.imedias.rsccfx.view.RsccRequestView;
import ch.imedias.rsccfx.view.RsccSupportPresenter;
import ch.imedias.rsccfx.view.RsccSupportView;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Starts the Rscc Application.
 */
public class RsccApp extends Application {
  private static final Logger LOGGER =
      Logger.getLogger(RsccApp.class.getName());

  public static final String APP_NAME = "Remote Support";
  /**
   * Declares views for use with ViewController.
   */
  public static final String HOME_VIEW = "home";
  public static final String REQUEST_VIEW = "requestHelp";

  public static final String SUPPORT_VIEW = "supporter";

  private static final double resolution4k = 2560 * 1440;
  private static final double resolutionFullHd = 1920 * 1080;
  private static final double resolutionLow = 1440 * 900;

  private static final double borderToFullHd = (resolutionLow + resolutionFullHd) / 2;
  private static final double borderTo4k = (resolutionFullHd + resolution4k) / 2;

  private static final double ROOT_TEXT_SIZE_4K = 13;
  private static final double ROOT_TEXT_SIZE_FULL_HD = 11;
  private static final double ROOT_TEXT_SIZE_LOW = 10;

  public static double rootTextSize;

  /**
   * Defines the scaling based on the DPI of the screen in relation to a 4K resolution display.
   * Must be used in all views to scale all displayed values that cannot be set in the CSS.
   */
  public static double scalingFactor;

  /**
   * Defines the stylesheet that is being used.
   */
  public static String styleSheet;

  private Rscc model;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
    setLogLevel(Level.INFO);

    // Get Screensize
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

    double screenHeight = primaryScreenBounds.getHeight();
    double screenWidth = primaryScreenBounds.getWidth();

    // set Stage boundaries to visible bounds of the main screen
    stage.setWidth(screenWidth / 1.8);
    stage.setHeight(screenHeight / 1.5);
    stage.setX(screenWidth / 2 - stage.getWidth() / 2);
    stage.setY(screenHeight / 2 - stage.getHeight() / 2);
    stage.setMinWidth((screenWidth / 1.8) / 1.2);
    stage.setMinHeight((screenHeight / 1.5) / 1.3);



    // Initialize stylesheets
    // Choose CSS depending on the resolution and set scaling factor
    double resolution = screenHeight * screenWidth;

    if (resolution > borderTo4k) {
      // 4K resolution
      rootTextSize = ROOT_TEXT_SIZE_4K;
      LOGGER.info("4K Resolution, Text Size: " + rootTextSize);

    } else if (resolution < borderToFullHd) {
      // low resolution (below Full HD)
      rootTextSize = ROOT_TEXT_SIZE_LOW;
      LOGGER.info("Low Resolution, Text Size: " + rootTextSize);
      stage.setMinWidth((screenWidth / 1.5));
      stage.setMinHeight((screenHeight / 1.5));
      stage.setWidth(screenWidth / 1.2);
      stage.setHeight(screenHeight / 1.2);
      stage.setX(screenWidth / 2 - stage.getWidth() / 2);
      stage.setY(screenHeight / 2 - stage.getHeight() / 2);
    } else {
      // Full HD resolution
      rootTextSize = ROOT_TEXT_SIZE_FULL_HD;
      LOGGER.info("Full HD Resolution, Text Size: " + rootTextSize);

    }

    scalingFactor = rootTextSize / ROOT_TEXT_SIZE_4K;

    styleSheet = getClass().getClassLoader()
        .getResource("css/styles.css").toExternalForm();

    SystemCommander systemCommander = new SystemCommander();
    model = new Rscc(systemCommander, new KeyUtil());
    systemCommander.setModel(model);
    ViewController mainView = new ViewController();

    // Set root font size, everything adapts to it afterwards
    mainView.setStyle("-fx-font-size: " + rootTextSize + "px;");

    final Scene scene = new Scene(mainView);

    // Initialize the views and load them into ViewController
    // HomeView
    Node view = new RsccHomeView(model);
    ControlledPresenter presenter = new RsccHomePresenter(model, (RsccHomeView) view);
    mainView.loadView(RsccApp.HOME_VIEW, view, presenter);

    // RequestHelpView
    view = new RsccRequestView(model);
    presenter = new RsccRequestPresenter(model, (RsccRequestView) view);
    mainView.loadView(RsccApp.REQUEST_VIEW, view, presenter);

    // SupporterView
    view = new RsccSupportView(model);
    presenter = new RsccSupportPresenter(model, (RsccSupportView) view);
    mainView.loadView(RsccApp.SUPPORT_VIEW, view, presenter);

    // Set initial screen
    mainView.setView(RsccApp.HOME_VIEW);

    stage.setScene(scene);
    stage.setTitle(APP_NAME);
    stage.show();

    // Initialize sizing of views
    ((RsccHomePresenter) mainView.getPresenter(HOME_VIEW)).initSize(scene);
    ((RsccRequestPresenter) mainView.getPresenter(REQUEST_VIEW)).initSize(scene);
    ((RsccSupportPresenter) mainView.getPresenter(SUPPORT_VIEW)).initSize(scene);

    scene.getStylesheets().add(styleSheet);
  }

  @Override
  public void stop() throws Exception {
    String key = model.getKeyUtil().getKey();
    if (key != null) {
      model.killConnection();
    }
    super.stop();
  }

  private void setLogLevel(Level logLevel) {
    Logger log = LogManager.getLogManager().getLogger("");
    for (Handler h : log.getHandlers()) {
      h.setLevel(logLevel);
    }
  }
}
