package ch.imedias.rsccfx.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Handles all of the different commands depending on the operating system.
 */
public class CommandHandler {

  private static final String ARCHLINUX = "ARCHLINUX";
  private static final String DEBIAN = "DEBIAN";
  private static final String MAC_OS = "MAC_OS";
  private static final String WINDOWS = "WINDOWS";

  private Command vncViewer;
  private Command vncViewerListen;
  private Command vncViewerCompression;
  private Command vncViewerQuality;
  private Command vncViewerBgr233;

  private StringProperty os = new SimpleStringProperty();

  public CommandHandler() {
    setOs(determineOs());
    initializeCommands();
  }

  private void initializeCommands() {
    vncViewer =
        new Command("vncviewer");
    vncViewerListen =
        new Command("-listen");
    vncViewerCompression =
        new Command("-compresslevel", "-CompressLevel", "-CompressionLevel",null);
    vncViewerQuality =
        new Command("-quality", "-QualityLevel", "-QualityLevel", null);
    vncViewerBgr233 =
        new Command("-bgr233", "-LowColorLevel", "-LowColorLevel", null);

  }

  private String determineOs() {
    if (System.getProperty("os.version").contains("ARCH")) {
      return ARCHLINUX;
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
    private final String commandArchlinux;
    private final String commandMacOs;
    private final String commandWindows;

    public Command(String commandDebian,
                   String commandArchlinux,
                   String commandMacOs,
                   String commandWindows) {
      this.commandDebian = commandDebian;
      this.commandArchlinux = commandArchlinux;
      this.commandMacOs = commandMacOs;
      this.commandWindows = commandWindows;
    }

    public Command(String command) {
      this.commandDebian = command;
      this.commandArchlinux = command;
      this.commandMacOs = command;
      this.commandWindows = command;
    }

    public String getCommand(String os) {
      switch (os) {
        case ARCHLINUX:
          return commandArchlinux;
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
