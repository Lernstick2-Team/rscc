package ch.imedias.rsccfx.model;

import ch.imedias.rsccfx.model.connectionutils.Rscccfp;
import ch.imedias.rsccfx.model.connectionutils.RunRudp;
import ch.imedias.rsccfx.model.util.KeyUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * Stores the key and keyserver connection details.
 * Handles communication with the keyserver.
 */
public class Rscc {
  private static final int PACKAGE_SIZE = 10000; // needed, since a static method access it.
  private static final Logger LOGGER =
      Logger.getLogger(Rscc.class.getName());
  /**
   * Points to the "docker-build_p2p" folder inside resources, relative to the build path.
   * Important: Make sure to NOT include a / in the beginning or the end.
   */
  private static final String DOCKER_FOLDER_NAME = "docker-build_p2p";
  /**
   * sh files can not be executed in the JAR file and therefore must be extracted.
   * ".rscc" is a hidden folder in the user's home directory (e.g. /home/user)
   */
  private static final String RSCC_FOLDER_NAME = ".rscc";
  private static final String STUN_DUMP_FILE_NAME = "ice4jDemoDump.ice";
  private static final String[] STUN_SERVERS = {
      "numb.viagenie.ca", "stun.ekiga.net", "stun.gmx.net", "stun.1und1.de"};
  private final SystemCommander systemCommander;

  private final StringProperty keyServerIp = new SimpleStringProperty("86.119.39.89");
  private final StringProperty keyServerHttpPort = new SimpleStringProperty("800");
  private final IntegerProperty vncPort = new SimpleIntegerProperty(5900);
  private final IntegerProperty icePort = new SimpleIntegerProperty(5050);

  private final BooleanProperty vncViewOnly = new SimpleBooleanProperty();
  private final DoubleProperty vncQuality = new SimpleDoubleProperty();
  private final DoubleProperty vncCompression = new SimpleDoubleProperty();

  private final BooleanProperty vncBgr233 = new SimpleBooleanProperty();
  private final StringProperty connectionStatusText = new SimpleStringProperty();
  private final StringProperty connectionStatusStyle = new SimpleStringProperty();

  private final IntegerProperty udpPackageSize = new SimpleIntegerProperty(
      getUdpPackageSizeStatic());
  private final IntegerProperty proxyPort = new SimpleIntegerProperty(2601);
  private final IntegerProperty stunServerPort = new SimpleIntegerProperty(3478);

  private final String[] connectionStatusStyles = {
      "statusBox", "statusBoxInitialize", "statusBoxSuccess", "statusBoxFail"};

  private final StringProperty terminalOutput = new SimpleStringProperty();

  private final BooleanProperty isForcingServerMode = new SimpleBooleanProperty(false);
  private final BooleanProperty isVncSessionRunning = new SimpleBooleanProperty(false);

  //TODO: Replace when the StunFileGeneration is ready
  private final String pathToStunDumpFile = this.getClass()
      .getClassLoader().getResource(STUN_DUMP_FILE_NAME)
      .toExternalForm().replace("file:", "");

  private final KeyUtil keyUtil;

  private boolean isLocalIceSuccessful = false;
  private boolean isRemoteIceSuccessful = false;
  private InetAddress remoteClientIpAddress;
  private int remoteClientPort;
  private RunRudp rudp;
  private VncViewerHandler vncViewer;
  private VncServerHandler vncServer;
  private Rscccfp rscccfp;
  private String pathToResourceDocker;

  /**
   * Initializes the Rscc model class.
   *
   * @param systemCommander a SystemComander-object that executes shell commands.
   * @param keyUtil         a KeyUtil-object which stores the key, validates and formats it.
   */
  public Rscc(SystemCommander systemCommander, KeyUtil keyUtil) {
    if (systemCommander == null) {
      LOGGER.info("Parameter SystemCommander is NULL");
      throw new IllegalArgumentException("Parameter SystemCommander is NULL");
    }
    if (keyUtil == null) {
      LOGGER.info("Parameter KeyUtil is NULL");
      throw new IllegalArgumentException("Parameter KeyUtil is NULL");
    }
    this.systemCommander = systemCommander;
    this.keyUtil = keyUtil;
    defineResourcePath();
    readServerConfig();

  }



  /**
   * Sets resource path, according to the application running either as a JAR or in the IDE.
   */
  private void defineResourcePath() {
    String userHome = System.getProperty("user.home");
    URL theLocationOftheRunningClass = this.getClass().getProtectionDomain()
        .getCodeSource().getLocation();
    File actualClass = new File(theLocationOftheRunningClass.getFile());
    if (actualClass.isDirectory()) {
      pathToResourceDocker =
          getClass().getClassLoader().getResource(DOCKER_FOLDER_NAME)
              .getFile().replaceFirst("file:", "");

    } else {
      pathToResourceDocker = userHome + "/" + RSCC_FOLDER_NAME + "/" + DOCKER_FOLDER_NAME;
      extractJarContents(theLocationOftheRunningClass,
          userHome + "/" + RSCC_FOLDER_NAME, DOCKER_FOLDER_NAME);
    }
  }

  /**
   * Extracts files from running JAR to folder.
   *
   * @param filter filters the files that will be extracted by this string.
   */
  private void extractJarContents(URL sourceLocation, String destinationDirectory, String filter) {
    JarFile jarFile = null;
    try {
      jarFile = new JarFile(new File(sourceLocation.getFile()));
    } catch (IOException e) {
      LOGGER.severe("Exception thrown when trying to get file from: "
          + sourceLocation
          + "\n Exception Message: " + e.getMessage());
    }
    Enumeration<JarEntry> contentList = jarFile.entries();
    while (contentList.hasMoreElements()) {
      JarEntry item = contentList.nextElement();
      if (item.getName().contains(filter)) {
        LOGGER.fine(item.getName());
        File targetFile = new File(destinationDirectory, item.getName());
        if (!targetFile.exists()) {
          targetFile.getParentFile().mkdirs();
          targetFile = new File(destinationDirectory, item.getName());
        }
        if (item.isDirectory()) {
          continue;
        }
        try (
            InputStream fromStream = jarFile.getInputStream(item);
            FileOutputStream toStream = new FileOutputStream(targetFile)
        ) {
          while (fromStream.available() > 0) {
            toStream.write(fromStream.read());
          }

        } catch (FileNotFoundException e) {
          LOGGER.severe("Exception thrown when reading from file: "
              + targetFile.getName()
              + "\n Exception Message: " + e.getMessage());
        } catch (IOException e) {
          LOGGER.severe("Exception thrown when trying to copy jar file contents to local"
              + "\n Exception Message: " + e.getMessage());
        }
        targetFile.setExecutable(true);
      }
    }
  }

  /**
   * Sets up the server with use.sh.
   */
  private void keyServerSetup() {
    String command = systemCommander.commandStringGenerator(
        pathToResourceDocker, "use.sh", getKeyServerIp(), getKeyServerHttpPort());
    systemCommander.executeTerminalCommand(command);
  }

  /**
   * Kills the connection to the keyserver.
   */
  public void killConnection() {
    if (rscccfp != null) {
      rscccfp.closeConnection();
    }
    if (rudp != null) {
      rudp.setIsOngoing(false);
    }
    if (vncServer != null) {
      vncServer.killVncServer();
    }
    if (vncViewer != null) {
      vncViewer.killVncViewer();
    }

    systemCommander.executeTerminalCommand("pkill ssh");

    // Execute port_stop.sh with the generated key to kill the connection
    String command = systemCommander.commandStringGenerator(
        pathToResourceDocker, "port_stop.sh", keyUtil.getKey());
    systemCommander.executeTerminalCommand(command);
    keyUtil.setKey("");
  }

  /**
   * Requests a key from the key server.
   */
  public void requestKeyFromServer() {
    setConnectionStatus("Setting keyserver...", 1);

    keyServerSetup();

    setConnectionStatus("Requesting key from server...", 1);

    String command = systemCommander.commandStringGenerator(
        pathToResourceDocker, "port_share.sh", Integer.toString(getVncPort()), pathToStunDumpFile);
    String key = systemCommander.executeTerminalCommand(command);

    keyUtil.setKey(key); // update key in model
    rscccfp = new Rscccfp(this, true);
    rscccfp.setDaemon(true);
    rscccfp.start();

    try {
      rscccfp.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    LOGGER.info("RSCC: Starting VNCServer");

    vncServer = new VncServerHandler(this, null, null, false);
    vncServer.start();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    rudp = null;

    if (isLocalIceSuccessful && isRemoteIceSuccessful) {
      rudp = new RunRudp(this, true, false);
    } else if (isLocalIceSuccessful && !isRemoteIceSuccessful) {
      rudp = new RunRudp(this, false, false);
    } else if (!isLocalIceSuccessful && isRemoteIceSuccessful) {
      rudp = new RunRudp(this, true, false);
    }

    if (rudp != null) {
      LOGGER.info("RSCC: Starting rudp");

      rudp.start();
    }

    setConnectionStatus("VNC-Server waits for incoming connection", 2);
  }

  /**
   * Stops the vnc server.
   */
  public void stopVncServer() {
    String command = systemCommander.commandStringGenerator(null, "killall", "x11vnc");
    systemCommander.executeTerminalCommand(command);
  }

  /**
   * Sets the Status of the connection establishment.
   *
   * @param text             Text to show for the connection status.
   * @param statusStyleIndex Index of the connectionStatusStyles.
   */
  public void setConnectionStatus(String text, int statusStyleIndex) {
    if (statusStyleIndex < 0 || statusStyleIndex >= connectionStatusStyles.length || text == null) {
      throw new IllegalArgumentException();
    }
    setConnectionStatusText(text);
    setConnectionStatusStyle(getConnectionStatusStyles(statusStyleIndex));
  }

  /**
   * Starts connection to the user.
   */
  public void connectToUser() {
    setConnectionStatus("Get key from keyserver...", 1);

    keyServerSetup();
    String command = systemCommander.commandStringGenerator(pathToResourceDocker,
        "port_connect.sh", Integer.toString(getVncPort()), keyUtil.getKey());

    setConnectionStatus("Connected to keyserver.", 1);

    systemCommander.executeTerminalCommand(command);

    rscccfp = new Rscccfp(this, false);
    rscccfp.setDaemon(true);
    rscccfp.start();

    try {
      rscccfp.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    RunRudp rudp = null;

    if (isLocalIceSuccessful) {
      rudp = new RunRudp(this, true, true);
    } else if (!isLocalIceSuccessful && isRemoteIceSuccessful) {
      rudp = new RunRudp(this, false, true);
    }

    if (rudp != null) {
      LOGGER.info("RSCC: Starting rudp");
      setConnectionStatus("Starting direct VNC connection.", 1);

      rudp.start();

      LOGGER.info("RSCC: Starting VNCViewer");
      setConnectionStatus("Starting VNC Viewer.", 1);

      vncViewer = new VncViewerHandler(
          this, "localhost", getProxyPort(), false);

    } else {
      vncViewer = new VncViewerHandler(
          this, "localhost", vncPort.getValue(), false);
    }
    vncViewer.start();
  }

  /**
   * Refreshes the key by killing the connection, requesting a new key and starting the server
   * again.
   */
  public void refreshKey() {
    setConnectionStatus("Refreshing key...", 1);
    killConnection();
    requestKeyFromServer();
  }

  /**
   * Reads the docker server configuration from file ssh.rc under "/pathToResourceDocker".
   */
  private void readServerConfig() {
    String configFilePath = pathToResourceDocker + "/ssh.rc";
    try {
      List<String> lines = Files.readAllLines(Paths.get(configFilePath), Charset.forName("UTF-8"));
      for (String line : lines) {
        if (line.contains("p2p_server=") && !line.endsWith("=")) {
          setKeyServerIp(line.split("=")[1]);
        } else if (line.contains("http_port=") && !line.endsWith("=")) {
          setKeyServerHttpPort(line.split("=")[1]);
        }
      }
      LOGGER.fine("Set serverIP to: " + getKeyServerIp()
          + "\n Set serverHTTP-port to: " + getKeyServerHttpPort());
    } catch (IOException e) {
      LOGGER.severe("Exception thrown when reading from file: "
          + configFilePath
          + "\n Exception Message: " + e.getMessage());
    }
  }

  public String getKeyServerIp() {
    return keyServerIp.get();
  }

  public void setKeyServerIp(String keyServerIp) {
    this.keyServerIp.set(keyServerIp);
  }

  public StringProperty keyServerIpProperty() {
    return keyServerIp;
  }

  public String getKeyServerHttpPort() {
    return keyServerHttpPort.get();
  }

  public void setKeyServerHttpPort(String keyServerHttpPort) {
    this.keyServerHttpPort.set(keyServerHttpPort);
  }

  public StringProperty keyServerHttpPortProperty() {
    return keyServerHttpPort;
  }

  public int getVncPort() {
    return vncPort.get();
  }

  public void setVncPort(int vncPort) {
    this.vncPort.set(vncPort);
  }

  public IntegerProperty vncPortProperty() {
    return vncPort;
  }

  public BooleanProperty vncViewOnlyProperty() {
    return vncViewOnly;
  }

  public double getVncQuality() {
    return vncQuality.get();
  }

  public void setVncQuality(int vncQuality) {
    this.vncQuality.set(vncQuality);
  }

  public DoubleProperty vncQualityProperty() {
    return vncQuality;
  }

  public boolean getVncViewOnly() {
    return vncViewOnly.get();
  }

  public void setVncViewOnly(boolean vncViewOnly) {
    this.vncViewOnly.set(vncViewOnly);
  }

  public double getVncCompression() {
    return vncCompression.get();
  }

  public void setVncCompression(double vncCompression) {
    this.vncCompression.set(vncCompression);
  }

  public DoubleProperty vncCompressionProperty() {
    return vncCompression;
  }

  public boolean getVncBgr233() {
    return vncBgr233.get();
  }

  public void setVncBgr233(boolean vncBgr233) {
    this.vncBgr233.set(vncBgr233);
  }

  public BooleanProperty vncBgr233Property() {
    return vncBgr233;
  }

  public KeyUtil getKeyUtil() {
    return keyUtil;
  }

  public String getConnectionStatusText() {
    return connectionStatusText.get();
  }

  public void setConnectionStatusText(String connectionStatusText) {
    this.connectionStatusText.set(connectionStatusText);
  }

  public StringProperty connectionStatusTextProperty() {
    return connectionStatusText;
  }

  public String getConnectionStatusStyle() {
    return connectionStatusStyle.get();
  }

  public void setConnectionStatusStyle(String connectionStatusStyle) {
    this.connectionStatusStyle.set(connectionStatusStyle);
  }

  public StringProperty connectionStatusStyleProperty() {
    return connectionStatusStyle;
  }

  public String getConnectionStatusStyles(int i) {
    return connectionStatusStyles[i];
  }

  public InetAddress getRemoteClientIpAddress() {
    return remoteClientIpAddress;
  }

  public void setRemoteClientIpAddress(InetAddress remoteClientIpAddress) {
    this.remoteClientIpAddress = remoteClientIpAddress;
  }

  public int getRemoteClientPort() {
    return remoteClientPort;
  }

  public void setRemoteClientPort(int remoteClientPort) {
    this.remoteClientPort = remoteClientPort;
  }

  public int getIcePort() {
    return icePort.get();
  }

  public void setIcePort(int icePort) {
    this.icePort.set(icePort);
  }

  public IntegerProperty icePortProperty() {
    return icePort;
  }

  public String[] getStunServers() {
    return STUN_SERVERS;
  }

  public boolean isLocalIceSuccessful() {
    return isLocalIceSuccessful;
  }

  public void setLocalIceSuccessful(boolean localIceSuccessful) {
    isLocalIceSuccessful = localIceSuccessful;
  }

  public boolean isRemoteIceSuccessful() {
    return isRemoteIceSuccessful;
  }

  public void setRemoteIceSuccessful(boolean remoteIceSuccessful) {
    isRemoteIceSuccessful = remoteIceSuccessful;
  }

  public void setTerminalOutput(String terminalOutput) {
    this.terminalOutput.set(terminalOutput);
  }

  public boolean getIsForcingServerMode() {
    return isForcingServerMode.get();
  }

  public void setIsForcingServerMode(boolean isForcingServerMode) {
    this.isForcingServerMode.set(isForcingServerMode);
  }

  public BooleanProperty isForcingServerModeProperty() {
    return isForcingServerMode;
  }

  public SystemCommander getSystemCommander() {
    return systemCommander;
  }

  public boolean isIsVncSessionRunning() {
    return isVncSessionRunning.get();
  }

  public void setIsVncSessionRunning(boolean isVncSessionRunning) {
    this.isVncSessionRunning.set(isVncSessionRunning);
  }

  public BooleanProperty isVncSessionRunningProperty() {
    return isVncSessionRunning;
  }

  public VncServerHandler getVncServer() {
    return vncServer;
  }

  public void setVncServer(VncServerHandler vncServer) {
    this.vncServer = vncServer;
  }

  public int getUdpPackageSize() {
    return udpPackageSize.get();
  }

  public static int getUdpPackageSizeStatic() {
    return PACKAGE_SIZE;
  }

  public void setUdpPackageSize(int udpPackageSize) {
    this.udpPackageSize.set(udpPackageSize);
  }

  public IntegerProperty udpPackageSizeProperty() {
    return udpPackageSize;
  }

  public IntegerProperty proxyPortProperty() {
    return proxyPort;
  }

  public int getProxyPort() {
    return proxyPort.get();
  }

  public void setProxyPort(int proxyPort) {
    this.proxyPort.set(proxyPort);
  }

  public IntegerProperty stunServerPortProperty() {
    return stunServerPort;
  }

  public int getStunServerPort() {
    return stunServerPort.get();
  }

  public void setStunServerPort(int stunServerPort) {
    this.stunServerPort.set(stunServerPort);
  }
}
