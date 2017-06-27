package ch.imedias.rsccfx.model;

import ch.imedias.rsccfx.model.util.KeyUtil;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Handles all of the different commands depending on the operating system.
 */
public class CommandHandler {

  private static final Logger LOGGER =
      Logger.getLogger(KeyUtil.class.getName());

  private static final String ARCH_LINUX = "ARCH_LINUX";
  private static final String DEBIAN = "DEBIAN";
  private static final String MAC_OS = "MAC_OS";
  private static final String WINDOWS = "WINDOWS";

  private Command vncViewer;
  private Command vncViewerListen;
  private Command vncViewerCompression;
  private Command vncViewerQuality;
  private Command vncViewerBgr233;

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
