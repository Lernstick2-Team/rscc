package ch.imedias.rsccfx.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles all of the different commands depending on the operating system.
 */
public class CommandHandler {

  private static final Logger LOGGER =
      LogManager.getLogger(CommandHandler.class.getName());

  private static final String ARCH_LINUX = "ARCH_LINUX";
  private static final String DEBIAN = "DEBIAN";
  private static final String MAC_OS = "MAC_OS";
  private static final String WINDOWS = "WINDOWS";

  private Command vncViewer;
  private Command vncViewerListen;
  private Command vncViewerCompression;
  private Command vncViewerQuality;
  private Command vncViewerBgr233;

  private Command vncServer;
  private Command vncServerPort;
  private Command vncServerLocalhost;
  private Command vncServerViewOnly;
  private Command vncServerReverse;
  private Command vncServerEncrypted;

  private StringProperty os = new SimpleStringProperty();

  /**
   * Sets the currently used OS and also initializes all commands.
   */
  public CommandHandler() {
    String os = determineOs();
    setOs(os);
    LOGGER.info("Running on the OS: " + os);
    initializeCommands();
  }

  /**
   * Initializes all of the commands.
   * Put new commands here, defining a command like this:
   * {@link Command#Command(String, String,    String, String)}
   * example = new  Command(debian, archLinux, macOs,  windows)
   * or:
   * {@link Command#Command(String)}
   * example = new  Command(command) => will be the same for all OS'es
   */
  private void initializeCommands() {
    vncViewer =
        new Command("vncviewer");
    vncViewerListen =
        new Command("-listen");
    vncViewerCompression =
        new Command("-compresslevel", "-CompressLevel", "-CompressionLevel", null);
    vncViewerQuality =
        new Command("-quality", "-QualityLevel", "-QualityLevel", null);
    vncViewerBgr233 =
        new Command("-bgr233", "-LowColorLevel", "-LowColorLevel", null);

    vncServer =
        new Command(
                "x11vnc",
                "x11vnc",
                Rscc.getPathToOsxServer() + Rscc.DEFAULT_OSX_SERVER_FILE_NAME + " -rfbnoauth",
                null);
    vncServerPort =
        new Command(":", ":", " -connectPort ", null);
    vncServerLocalhost =
        new Command("-localhost");
    vncServerViewOnly =
        new Command("-viewonly", "-viewonly", "-disableRemoteEvents", null);
    vncServerReverse =
        new Command("-connect", "-connect", "-connectHost ", null);
    vncServerEncrypted =
        new Command("-ssl TMP", "", "", ""); // TODO: how are these commands in other OS'es?

  }

  private String determineOs() {
    if (System.getProperty("os.version").contains("ARCH")) {
      return ARCH_LINUX;
    }
    if (System.getProperty("os.name").startsWith("Mac OS")) {
      return MAC_OS;
    }
    if (System.getProperty("os.name").startsWith("Windows")) {
      return WINDOWS;
    }
    // since Debian-based systems are not easily detectable
    // assume a Debian system if none of the above applies
    return DEBIAN;
  }

  public String getOs() {
    return os.get();
  }

  public StringProperty osProperty() {
    return os;
  }

  public void setOs(String os) {
    this.os.set(os);
  }

  public String getVncViewer() {
    return vncViewer.getCommand(getOs());
  }

  public String getVncViewerListen() {
    return vncViewerListen.getCommand(getOs());
  }

  public String getVncViewerCompression() {
    return vncViewerCompression.getCommand(getOs());
  }

  public String getVncViewerQuality() {
    return vncViewerQuality.getCommand(getOs());
  }

  public String getVncViewerBgr233() {
    return vncViewerBgr233.getCommand(getOs());
  }

  public String getVncServer() {
    return vncServer.getCommand(getOs());
  }

  public String getVncServerPort() {
    return vncServerPort.getCommand(getOs());
  }

  public String getVncServerLocalhost() {
    return vncServerLocalhost.getCommand(getOs());
  }

  public String getVncServerViewOnly() {
    return vncServerViewOnly.getCommand(getOs());
  }

  public String getVncServerEncrypted() {
    return vncServerEncrypted.getCommand(getOs());
  }

  public String getVncServerReverse() {
    return vncServerReverse.getCommand(getOs());
  }

  private static class Command {
    private final String commandDebian;
    private final String commandArchLinux;
    private final String commandMacOs;
    private final String commandWindows;

    public Command(String commandDebian,
                   String commandArchLinux,
                   String commandMacOs,
                   String commandWindows) {
      this.commandDebian = commandDebian;
      this.commandArchLinux = commandArchLinux;
      this.commandMacOs = commandMacOs;
      this.commandWindows = commandWindows;
    }

    /**
     * Initializes a new command, using the same command for all OS'es.
     *
     * @param command to be used with all OS'es.
     */
    public Command(String command) {
      this.commandDebian = command;
      this.commandArchLinux = command;
      this.commandMacOs = command;
      this.commandWindows = command;
    }

    /**
     * Get the command depending on the used OS.
     *
     * @param os the currently used OS
     * @return the command corresponding to the OS.
     */
    public String getCommand(String os) {
      switch (os) {
        case ARCH_LINUX:
          return commandArchLinux;
        case DEBIAN:
          return commandDebian;
        case MAC_OS:
          return commandMacOs;
        case WINDOWS:
          return commandWindows;
        default:
          return null;
      }
    }
  }
}
