package ch.imedias.rsccfx.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;


/**
 * Handles a VNC Viewer.
 */
public class VncViewerHandler {
  private static final Logger LOGGER =
      Logger.getLogger(VncViewerHandler.class.getName());
  private final Rscc model;
  private final CommandHandler command;
  private Process process;

  /**
   * Constructor to instantiate a VNCViewer.
   *
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
    Thread startViewerProcessThread = new Thread() {
      public void run() {
        try {
          LOGGER.info("Starting VNC Viewer Connection");

          String command = getViewerCommand(hostAddress, vncViewerPort, false);

          LOGGER.info("Starting VNCViewer with command: " + command);

          process = model.getSystemCommander().startProcess(command);

          model.setVncViewerProcessRunning(true);

          InputStream errorStream = process.getErrorStream();
          BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
          String errorString;
          while (process.isAlive()) {
            errorString = errorReader.readLine();

            if (errorString != null && (errorString.contains("Connection refused")
                || errorString.contains("Usage"))) {
              LOGGER.info("Detected: Viewer failed to connect");
              killVncViewerProcess();
            }

            if (errorString != null && errorString.contains("Connected to RFB server")) {
              LOGGER.info("Detected: Viewer connected sucessfully");
              model.setVncSessionRunning(true);
            }
          }

          LOGGER.info("VNC - Viewer process has ended");
          if (model.isVncSessionRunning()) {
            model.setStatusBarKeyInput(model.strings.statusBarConnectionClosed,
                model.STATUS_BAR_STYLE_INITIALIZE);
          }
          model.setVncSessionRunning(false);
          model.setVncViewerProcessRunning(false);

          errorStream.close();

        } catch (IOException e) {
          LOGGER.info(e.getMessage());
        }

        LOGGER.info("Ending VNC Viewer Thread ");
      }
    };

    startViewerProcessThread.start();
  }

  private String getViewerCommand(String hostAddress, Integer vncViewerPort, boolean listenMode) {
    StringBuilder commandArray = new StringBuilder();
    commandArray.append(command.getVncViewer());
    if (listenMode) {
      commandArray.append(" ").append(command.getVncViewerListen());
    }
    commandArray.append(" ").append(command.getVncViewerCompression());
    commandArray.append(" ").append(Integer.toString((int) model.getVncCompression()));
    commandArray.append(" ").append(command.getVncViewerQuality());
    commandArray.append(" ").append(Integer.toString((int) model.getVncQuality()));
    if (model.getVncBgr233()) {
      commandArray.append(" ").append(command.getVncViewerBgr233());
    }
    if (!listenMode) {
      commandArray.append(" ").append(hostAddress + ":" + vncViewerPort);
    }
    return commandArray.toString();
  }


  /**
   * Starts this VNCViewer listening on localhost.
   */
  public void startVncViewerListening() {

    Thread startViewerProcessThread = new Thread() {
      public void run() {
        LOGGER.info("Starting VNC Viewer listening Thread ");
        model.setVncViewerProcessRunning(true);
        try {

          String command = getViewerCommand(null, null, true);

          LOGGER.info("Starting VNCViewer with command: " + command);

          process = model.getSystemCommander().startProcess(command);

          InputStream errorStream = process.getErrorStream();
          BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
          String errorString;

          while (process.isAlive()) {
            errorString = errorReader.readLine();

            if (errorString != null && errorString.contains("Connected to RFB server")) {
              LOGGER.info("Detected: Server has connected");
              model.setVncSessionRunning(true);
            }
          }

          model.setVncViewerProcessRunning(false);
          model.setVncSessionRunning(false);
          errorStream.close();
          LOGGER.info("VNC - Viewer process has ended");

        } catch (IOException e) {
          LOGGER.info(e.getMessage());
        }
      }
    };
    startViewerProcessThread.start();
  }

  /**
   * Kills the VNC Viewer process.
   */

  public void killVncViewerProcess() {
    LOGGER.info("Stopping VNC-Viewer Process");
    process.destroy();
    model.setVncViewerProcessRunning(false);
    model.setVncSessionRunning(false);
  }

}
