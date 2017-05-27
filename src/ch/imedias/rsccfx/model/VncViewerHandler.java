package ch.imedias.rsccfx.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;


/**
 * This Class handles a VNC Viewer.
 * Created by jp on 11/05/17.
 */
public class VncViewerHandler {
  private static final Logger LOGGER =
      Logger.getLogger(VncViewerHandler.class.getName());
  private final Rscc model;
  private final String vncViewerName = "vncviewer";
  private Process process;

  /**
   * Constructor to instantiate a VNCViewer.
   *
   * @param model The one and only model.
   */
  public VncViewerHandler(Rscc model) {
    this.model = model;
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

          StringBuilder commandArray = new StringBuilder();
          commandArray.append(vncViewerName);
          commandArray.append(" ").append("-compresslevel");
          commandArray.append(" ").append(Integer.toString((int) model.getVncCompression()));
          commandArray.append(" ").append("-quality");
          commandArray.append(" ").append(Integer.toString((int) model.getVncQuality()));
          if (model.getVncBgr233()) {
            commandArray.append(" ").append("-bgr233");
          }
          commandArray.append(" ").append(hostAddress + "::" + vncViewerPort);

          LOGGER.info("Strating VNCViewer with command: " + commandArray.toString());

          process = model.getSystemCommander().startProcess(commandArray.toString());

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
          model.setVncSessionRunning(false);
          model.setVncViewerProcessRunning(false);

          errorStream.close();

        } catch (IOException e) {
          e.getStackTrace();
        }

        LOGGER.info("Ending VNC Viewer Thread ");
      }
    };

    startViewerProcessThread.start();
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


          StringBuilder commandArray = new StringBuilder();
          commandArray.append(vncViewerName);
          commandArray.append(" ").append("-listen");
          commandArray.append(" ").append("-compresslevel");
          commandArray.append(" ").append(Integer.toString((int) model.getVncCompression()));
          commandArray.append(" ").append("-quality");
          commandArray.append(" ").append(Integer.toString((int) model.getVncQuality()));
          if (model.getVncBgr233()) {
            commandArray.append(" ").append("-bgr233");
          }

          LOGGER.info("Strating VNCViewer with command: " + commandArray.toString());

          process = model.getSystemCommander().startProcess(commandArray.toString());

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
          e.getStackTrace();
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
