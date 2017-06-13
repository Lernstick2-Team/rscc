package ch.imedias.rsccfx.model;

import ch.imedias.rsccfx.localization.Strings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;


/**
 * This Class handles a VNC Server.
 * Created by jp on 11/05/17.
 */
public class VncServerHandler {
  private static final Logger LOGGER =
      Logger.getLogger(VncServerHandler.class.getName());
  private final Rscc model;
  private final String vncServerName = "x11vnc";
  private Process process;
  private final Strings strings = new Strings();

  /**
   * Constructor to instantiate a VNCServer.
   *
   * @param model The one and only Model.
   */
  public VncServerHandler(Rscc model) {
    this.model = model;
  }


  /**
   * Starts VNC Server in Reverse Mode.
   * Thread Live as long as connection is established.
   *
   * @param hostAddress   Address to connect to.
   * @param vncViewerPort Port to connect to.
   * @return true when conneting did not fail.
   */
  public void startVncServerReverse(String hostAddress, Integer vncViewerPort,
                                    boolean isEncrypted) {

    Thread startServerProcessThread = new Thread() {
      public void run() {
        try {

          StringBuilder commandArray = new StringBuilder();
          commandArray.append(vncServerName);
          commandArray.append(" ").append("-connect");
          commandArray.append(" ").append(hostAddress + ":" + vncViewerPort);
          if (isEncrypted) {
            commandArray.append(" ").append("-ssl");
            commandArray.append(" ").append("TMP");
          }

          LOGGER.info("Strating VNC-Server with command: " + commandArray.toString());

          process = model.getSystemCommander().startProcess(commandArray.toString());
          model.setVncServerProcessRunning(true);

          InputStream errorStream = process.getErrorStream();
          BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
          String errorString;
          while (process.isAlive()) {
            errorString = errorReader.readLine();

            if (errorString != null && errorString.contains("failed to connect")) {
              LOGGER.info("Detected: failed to connect");
              model.setStatusBarSupporter(strings.statusBarConnectionFailed,
                  model.STATUS_BAR_STYLE_FAIL);
              killVncServerProcess();
            }

            if (errorString != null && errorString.contains("reverse_connect")
                && errorString.contains("OK")) {
              LOGGER.info("Detected: Reverse connect OK");
              model.setVncSessionRunning(true);
              model.setStatusBarSupporter(strings.statusBarConnected,
                  model.STATUS_BAR_STYLE_SUCCESS);
            }
          }

          LOGGER.info("VNC - Server process has ended");
          if (model.isVncSessionRunning()) {
            model.setStatusBarSupporter(strings.statusBarConnectionClosed,
                model.STATUS_BAR_STYLE_INITIALIZE);
          }
          model.setVncSessionRunning(false);
          model.setVncServerProcessRunning(false);


          errorStream.close();

        } catch (IOException e) {
          e.getStackTrace();
        }

        LOGGER.info("Ending VNC Server Thread ");
      }
    };

    startServerProcessThread.start();
  }


  /**
   * Starts this VNCServer listening on localhost.
   */
  public void startVncServerListening() {

    Thread startServerProcessThread = new Thread() {
      public void run() {
        LOGGER.info("Starting VNC Server Thread");
        model.setVncServerProcessRunning(true);

        try {

          StringBuilder commandArray = new StringBuilder();
          commandArray.append(vncServerName);
          commandArray.append(" ").append("-localhost");
          if (model.getVncViewOnly()) {
            commandArray.append(" ").append("-viewonly");
          }

          LOGGER.info("Strating VNC-Server with command: " + commandArray.toString());

          process = model.getSystemCommander().startProcess(commandArray.toString());

          InputStream errorStream = process.getErrorStream();
          BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
          String errorString;

          while (process.isAlive()) {
            errorString = errorReader.readLine();

            if (errorString != null && errorString.contains("connection from client")) {
              LOGGER.info("Client has connected");
              model.setVncSessionRunning(true);
              if (model.getRudp() != null) {
                model.setStatusBarKeyGeneration(strings.statusBarVncConnectionEstablishedICE,
                    model.STATUS_BAR_STYLE_SUCCESS);
              } else {
                model.setStatusBarKeyGeneration(strings.statusBarVncConnectionEstablishedServer,
                    model.STATUS_BAR_STYLE_SUCCESS);
              }
            }
          }

          LOGGER.info("VNC - Server process has ended");
          errorStream.close();
          model.setVncSessionRunning(false);
          model.setVncServerProcessRunning(false);

        } catch (IOException e) {
          e.getStackTrace();
        }
      }
    };
    startServerProcessThread.start();
  }

  /**
   * kill the VNC Server process.
   */
  public void killVncServerProcess() {
    LOGGER.info("Stopping VNC-Server Process");
    process.destroy();
    model.setVncSessionRunning(false);
    model.setVncServerProcessRunning(false);
  }

}
