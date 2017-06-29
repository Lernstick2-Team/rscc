package ch.imedias.rsccfx.model;

import com.tigervnc.vncviewer.VncViewer;
import java.util.logging.Logger;


/**
 * Handles a VNC Viewer.
 */
public class VncViewerHandler {
  private static final Logger LOGGER =
      Logger.getLogger(VncViewerHandler.class.getName());
  private final Rscc model;
  private final CommandHandler command;
  private VncViewer viewer;


  /**
   * Constructor to instantiate a VNCViewer.
   * @param model The one and only model.
   */
  public VncViewerHandler(Rscc model) {
    this.model = model;
    command = model.getCommand();
  }


  /**
   * Starts VNC Viewer and tries to connect to a Server. (active connecting mode)
   * Thread lives as long as connection is established.
   *
   * @param hostAddress Address to connect to.
   */
  public void startVncViewerConnecting(String hostAddress, Integer vncViewerPort) {
    Thread startConnecting = new Thread() {
      public void run() {
        LOGGER.info("Starting VNC Viewer Connection");
        String[] args = {hostAddress + ":" + vncViewerPort};
        startVncViewer(args);
        LOGGER.info("Starting VNCViewer with command: " + command);
        model.setVncViewerProcessRunning(true);

        LOGGER.info("VNC - Viewer process has ended");
        if (model.isVncSessionRunning()) {
          model.setStatusBarKeyInput(model.strings.statusBarConnectionClosed,
              model.STATUS_BAR_STYLE_INITIALIZE);
        }
        model.setVncSessionRunning(false);
        model.setVncViewerProcessRunning(false);

        LOGGER.info("Ending VNC Viewer Thread ");
      }
    };
    startConnecting.start();

  }


  /**
   * Starts this VNCViewer listening on localhost.
   */
  public void startVncViewerListening() {
    Thread startListening = new Thread() {
      public void run() {
        String[] args = {"-listen"};
        startVncViewer(args);
        LOGGER.info("Starting VNC Viewer listening Thread ");
        model.setVncViewerProcessRunning(true);
        LOGGER.info("Starting VNCViewer with command: " + command);
        model.setVncSessionRunning(true);
        model.setVncViewerProcessRunning(false);
        model.setVncSessionRunning(false);
      }
    };
    startListening.start();

  }

  /**
   * Kills the VNC Viewer process.
   */

  public void killVncViewerProcess() {
    LOGGER.info("Stopping VNC-Viewer Process");
    model.setVncViewerProcessRunning(false);
    model.setVncSessionRunning(false);
    if (viewer != null) {
      viewer.exit(0);
    }
  }

  private void startVncViewer(String[] args) {
   for(String s:args) {
     System.out.print(s);
   }
    System.out.println();
   //String[] newArgs={"localhost:2601"};
     viewer = new VncViewer(args);

    viewer.start();
  }

}
