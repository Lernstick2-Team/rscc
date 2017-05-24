package ch.imedias.rsccfx.model;

import java.util.logging.Logger;

/**
 * This Class handles a VNC viewer.
 * The Thread keeps running as long as the VNCViewer is running.
 * Created by jp on 11/05/17.
 */
public class VncViewerHandler extends Thread {
  private static final Logger LOGGER =
      Logger.getLogger(Rscc.class.getName());
  private final SystemCommander systemCommander;
  private final Rscc model;
  private final String vncViewerName = "vncviewer";
  private String hostAddress;
  private Integer vncViewerPort;
  private boolean listeningMode;

  /**
   * Constructor to instantiate a VNCViewer.
   * @param model The one and only Model.
   * @param hostAddress Address to connect to.
   * @param vncViewerPort Port to connect to.
   * @param listeningMode defines if it should listen or not.
   */
  public VncViewerHandler(Rscc model, String hostAddress,
                          Integer vncViewerPort, boolean listeningMode) {

    this.listeningMode = listeningMode;
    this.model = model;
    this.hostAddress = hostAddress;
    this.vncViewerPort = vncViewerPort;
    this.systemCommander = model.getSystemCommander();
  }


  /**
   * Starts the VNCViewer in the given mode (Reverse or normal).
   */
  public void run() {
    if (listeningMode) {
      startVncViewerReverse();
    } else {
      startVncViewer();
    }
  }


  /**
   * Starts this VNCViewer connecting to <code>hostAddress</code> and <code>vncViewerPort</code>.
   * Loops until the VNCViewer is connected to the VNCServer.
   */
  private void startVncViewer() {
    String vncViewerAttributes = "-bgr233 " + " " + hostAddress + "::" + vncViewerPort;

    String command = systemCommander.commandStringGenerator(null,
        vncViewerName, vncViewerAttributes);

    String connectionStatus = null;

    int cycle = 0;
    do {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      connectionStatus = systemCommander.executeTerminalCommandAndUpdateModel(
          command, "Connected");
      LOGGER.info("VNCviewer: " + connectionStatus);
      cycle++;
    } while (!connectionStatus.contains("Connected") && cycle < 8);

  }


  /**
   * Starts this VNCViewer in Listening mode.
   */
  private void startVncViewerReverse() {

    //Correct weird vncviewer behavious: it adds the portnumber to 5500 and starts
    // service on this port (0=5500, 1=5501)
    int recalculatedPort;
    if (vncViewerPort != null) {
      recalculatedPort = vncViewerPort - 5500;
    } else {
      recalculatedPort = 0;
    }

    String vncViewerAttributes = "-listen" + recalculatedPort;

    String command = systemCommander.commandStringGenerator(null,
        vncViewerName, vncViewerAttributes);

    systemCommander.executeTerminalCommandAndUpdateModel(
        command, "Connected");
  }


  /**
   * Kills all processes with the Name of the VNCViewer.
   */
  public void killVncViewer() {
    String command = systemCommander.commandStringGenerator(null,
        "killall", vncViewerName);
    systemCommander.executeTerminalCommand(command);
  }
}
