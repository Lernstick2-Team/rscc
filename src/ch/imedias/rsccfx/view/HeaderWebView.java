package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.RsccApp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Worker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


/**
 * Represents the web page shown when pressing the help button in the header.
 */
public class HeaderWebView extends VBox {
  private static final Logger LOGGER =
      Logger.getLogger(HeaderWebView.class.getName());

  //final String wikiUrl = "https://wiki.lernstick.ch";
  final ProgressBar progressBar = new ProgressBar();
  final WebView browser = new WebView();
  final WebEngine webEngine = browser.getEngine();
  final Worker<Void> worker = webEngine.getLoadWorker();
  final Label versionLbl = new Label();
  static final int BROWSER_WIDTH = 1000;

  public HeaderWebView() {
    initWebHelp();
  }

  /**
   * Initializes the web browser.
   */
  public void initWebHelp() {

    // Bind the progress property of ProgressBar
    // with progress property of Worker
    progressBar.progressProperty().bind(worker.progressProperty());
    progressBar.setPrefWidth(BROWSER_WIDTH);
    versionLbl.setText("Version: " + RsccApp.APP_VERSION);

    this.getChildren().addAll(browser, versionLbl, progressBar);

    try  {
      String url = getClass().getClassLoader().getResource("helpPage.html").toExternalForm();
      //If local Help Page should be shown
      webEngine.load(url);
    } catch (NullPointerException e) {
      LOGGER.log(Level.SEVERE, "File helpPage.html not found");
    }

    //If external Wiki should be shown
    //webEngine.load(wikiUrl);

  }
}

