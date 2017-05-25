package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Worker;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


/**
 * Class that created the help View.
 */
public class HeaderWebView extends VBox {
  private static final Logger LOGGER =
      Logger.getLogger(Rscc.class.getName());

  //final String wikiUrl = "https://wiki.lernstick.ch";
  final ProgressBar progressBar = new ProgressBar();
  final WebView browser = new WebView();
  final WebEngine webEngine = browser.getEngine();
  final Worker<Void> worker = webEngine.getLoadWorker();
  static final int BROWSERWIDTH = 1000;

  public HeaderWebView() {
    initWebHelp();
  }

  /**
   * initialize the web browswer.
   */
  public void initWebHelp() {

    // Bind the progress property of ProgressBar
    // with progress property of Worker
    progressBar.progressProperty().bind(worker.progressProperty());
    System.out.println(this.widthProperty().getValue());
    progressBar.setPrefWidth(BROWSERWIDTH);

    this.getChildren().addAll(browser, progressBar);

    try  {
      String url1 = getClass().getClassLoader().getResource("helpPage.html").toExternalForm();
      //If local Help Page should be shown
      webEngine.load(url1);
    } catch (NullPointerException e) {
      LOGGER.log(Level.SEVERE, "File helpPage.html not found");
    }

    //If external Wiki should be shown
    //webEngine.load(wikiUrl);

  }
}

