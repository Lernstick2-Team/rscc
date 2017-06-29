package ch.imedias.rsccfx.model;

import ch.imedias.rsccfx.localization.Strings;
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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.function.UnaryOperator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
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

  //Default stettings
  public static final String DEFAULT_KEY_SERVER_IP = "agora.imedias.ch";
  public static final String DEFAULT_KEY_SERVER_HTTP_PORT = "800";
  public static final int DEFAULT_VNC_PORT = 5900;
  public static final int DEFAULT_VNC_QUALITY = 6;
  public static final int DEFAULT_VNC_COMPRESSION = 6;
  public static final int DEFAULT_ICE_PORT = 5050;
  public static final int DEFAULT_PROXY_PORT = 2601;
  public static final int DEFAULT_UDP_PACKAGE_SIZE = 10000;
  public static final boolean DEFAULT_FORCING_SERVER_MODE = false;
  public static final boolean DEFAULT_VNC_VIEW_ONLY = false;
  public static final boolean DEFAULT_VNC_BGR_233 = false;
  public static final int DEFAULT_STUN_SERVER_PORT = 3478;
  public static final String DEFAULT_STUN_SERVERS = "numb.viagenie.ca;"
      + "stun.ekiga.net;stun.gmx.net;stun.1und1.de";

  //PreferencesNames
  public static final String PREFERENCES_KEY_SERVER_IP = "keyServerIp";
  public static final String PREFERENCES_KEY_SERVER_HTTP_PORT = "keyServerHttpPort";
  public static final String PREFERENCES_VNC_PORT = "vncPort";
  public static final String PREFERENCES_ICE_PORT = "icePort";
  public static final String PREFERENCES_UDP_PACKAGE_SIZE = "udpPackageSize";
  public static final String PREFERENCES_PROXY_PORT = "proxyPort";
  public static final String PREFERENCES_STUN_SERVER_PORT = "stunServerPort";
  public static final String PREFERENCES_FORCING_SERVER_MODE = "forcingServerMode";
  public static final String PREFERENCES_VNC_VIEW_ONLY = "vncViewOnly";
  public static final String PREFERENCES_VNC_BGR_233 = "vncBgr233";
  public static final String PREFERENCES_VNC_COMPRESSION = "vncCompression";
  public static final String PREFERENCES_VNC_QUALITY = "vncQuality";
  public static final String PREFERENCES_STUN_SERVERS = "STUNServers";
  public static final String DELIMITER = ";";

  private static String[] STUN_SERVERS;
  private static final int PACKAGE_SIZE = 10000; // needed, since a static method access it.
  // TODO: make access depend on current setting
  private static final Logger LOGGER =
      Logger.getLogger(Rscc.class.getName());
  /**
   * Points to the "docker-build_p2p" folder inside resources, relative to the build path.
   * Important: Make sure to NOT include a / in the beginning or the end.
   */
  private static final String DOCKER_FOLDER_NAME = "docker-build_p2p";
  public static final String DEFAULT_SUPPORTERS_FILE_NAME = "rscc-defaults-lernstick.xml";
  public static final String DEFAULT_OSX_SERVER_FILE_NAME = "OSXvnc-server";


  /**
   * sh files can not be executed in the JAR file and therefore must be extracted.
   * ".rscc" is a hidden folder in the user's home directory (e.g. /home/user)
   */
  private static final String RSCC_FOLDER_NAME = ".config/rscc";
  private static final String[] EXTRACTED_RESOURCES =
      {DOCKER_FOLDER_NAME, DEFAULT_SUPPORTERS_FILE_NAME, DEFAULT_OSX_SERVER_FILE_NAME};
  public static final UnaryOperator<String> REMOVE_FILE_IN_PATH =
      string -> string.replaceFirst("file:", "");
  private final SystemCommander systemCommander;
  private final CommandHandler command;

  //StatusBar
  public static final String STATUS_BAR_STYLE_IDLE = "statusBar";
  public static final String STATUS_BAR_STYLE_INITIALIZE = "statusBarInitialize";
  public static final String STATUS_BAR_STYLE_SUCCESS = "statusBarSuccess";
  public static final String STATUS_BAR_STYLE_FAIL = "statusBarFail";
  private final StringProperty statusBarTextKeyGeneration = new SimpleStringProperty();
  private final StringProperty statusBarStyleClassKeyGeneration = new SimpleStringProperty();
  private final StringProperty statusBarTextSupporter = new SimpleStringProperty();
  private final StringProperty statusBarStyleClassSupporter = new SimpleStringProperty();
  private final StringProperty statusBarTextKeyInput = new SimpleStringProperty();
  private final StringProperty statusBarStyleClassKeyInput = new SimpleStringProperty();
  private final StringProperty statusBarTextStartService = new SimpleStringProperty();
  private final StringProperty statusBarStyleClassStartService = new SimpleStringProperty();

  //Settings
  private final StringProperty keyServerIp = new SimpleStringProperty();
  private final StringProperty keyServerHttpPort = new SimpleStringProperty();
  private final IntegerProperty vncPort = new SimpleIntegerProperty();
  private final IntegerProperty icePort = new SimpleIntegerProperty();
  private final BooleanProperty vncViewOnly = new SimpleBooleanProperty();
  private final DoubleProperty vncQuality = new SimpleDoubleProperty();
  private final DoubleProperty vncCompression = new SimpleDoubleProperty();
  private final BooleanProperty vncBgr233 = new SimpleBooleanProperty();
  private final IntegerProperty udpPackageSize = new SimpleIntegerProperty(
      getUdpPackageSizeStatic());
  private final IntegerProperty proxyPort = new SimpleIntegerProperty();
  private final IntegerProperty stunServerPort = new SimpleIntegerProperty();
  private final BooleanProperty forcingServerMode = new SimpleBooleanProperty(false);

  //States
  private final BooleanProperty vncSessionRunning = new SimpleBooleanProperty(false);
  private final BooleanProperty vncServerProcessRunning = new SimpleBooleanProperty(false);
  private final BooleanProperty vncViewerProcessRunning = new SimpleBooleanProperty(false);
  private final BooleanProperty connectionEstablishmentRunning = new SimpleBooleanProperty(false);
  private final BooleanProperty rscccfpHasTalkedToOtherClient = new SimpleBooleanProperty(false);
  private final BooleanProperty isSshRunning = new SimpleBooleanProperty(false);
  private final BooleanProperty isKeyRefreshInProgress = new SimpleBooleanProperty(false);

  private final Preferences preferences = Preferences.userNodeForPackage(Rscc.class);

  public final Strings strings = new Strings();
  private final KeyUtil keyUtil;
  private String pathToResources;
  private String pathToResourceDocker;
  private String pathToDefaultSupporters;
  private static String pathToOsxServer;
  private boolean isLocalIceSuccessful = false;
  private boolean isRemoteIceSuccessful = false;
  private InetAddress remoteClientIpAddress;
  private int remoteClientPort;
  private RunRudp rudp;
  private VncViewerHandler vncViewer;
  private VncServerHandler vncServer;
  private Rscccfp rscccfp;


  /**
   * Initializes the Rscc model class.
   *
   * @param systemCommander a SystemComander-object that executes shell commands.
   * @param keyUtil         a KeyUtil-object which stores the key, validates and formats it.
   */
  public Rscc(SystemCommander systemCommander, KeyUtil keyUtil, CommandHandler command) {
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
    this.command = command;
    defineResourcePath();
    loadUserPreferences();
  }

  /**
   * Loads the UserPreferences.
   */
  private void loadUserPreferences() {
    setKeyServerIp(preferences.get(PREFERENCES_KEY_SERVER_IP, DEFAULT_KEY_SERVER_IP));
    setKeyServerHttpPort(preferences.get(PREFERENCES_KEY_SERVER_HTTP_PORT,
        DEFAULT_KEY_SERVER_HTTP_PORT));
    setVncPort(preferences.getInt(PREFERENCES_VNC_PORT, DEFAULT_VNC_PORT));
    setIcePort(preferences.getInt(PREFERENCES_ICE_PORT, DEFAULT_ICE_PORT));
    setUdpPackageSize(preferences.getInt(PREFERENCES_UDP_PACKAGE_SIZE, DEFAULT_UDP_PACKAGE_SIZE));
    setProxyPort(preferences.getInt(PREFERENCES_PROXY_PORT, DEFAULT_PROXY_PORT));
    setStunServerPort(preferences.getInt(PREFERENCES_STUN_SERVER_PORT, DEFAULT_STUN_SERVER_PORT));
    setVncViewOnly(preferences.getBoolean(PREFERENCES_VNC_VIEW_ONLY, DEFAULT_VNC_VIEW_ONLY));
    setVncBgr233(preferences.getBoolean(PREFERENCES_VNC_BGR_233, DEFAULT_VNC_BGR_233));
    setVncCompression(preferences.getDouble(PREFERENCES_VNC_COMPRESSION, DEFAULT_VNC_COMPRESSION));
    setVncQuality(preferences.getDouble(PREFERENCES_VNC_QUALITY, DEFAULT_VNC_QUALITY));

    String stunServers = preferences.get(PREFERENCES_STUN_SERVERS,
        DEFAULT_STUN_SERVERS);
    setStunServers(stunServers.split(DELIMITER));

    LOGGER.info("Loaded UserPrefs");
  }

  /**
   * Saves the UserPreferences.
   */
  public void saveUserPreferences() {
    preferences.put(PREFERENCES_KEY_SERVER_IP, getKeyServerIp());
    preferences.put(PREFERENCES_KEY_SERVER_HTTP_PORT, getKeyServerHttpPort());
    preferences.putInt(PREFERENCES_VNC_PORT, getVncPort());
    preferences.putInt(PREFERENCES_ICE_PORT, getIcePort());
    preferences.putInt(PREFERENCES_UDP_PACKAGE_SIZE, getUdpPackageSize());
    preferences.putInt(PREFERENCES_PROXY_PORT, getProxyPort());
    preferences.putInt(PREFERENCES_STUN_SERVERS, getStunServerPort());
    preferences.putBoolean(PREFERENCES_VNC_VIEW_ONLY, getVncViewOnly());
    preferences.putBoolean(PREFERENCES_VNC_BGR_233, getVncBgr233());
    preferences.putDouble(PREFERENCES_VNC_COMPRESSION, getVncCompression());
    preferences.putDouble(PREFERENCES_VNC_QUALITY, getVncQuality());

    preferences.put(PREFERENCES_STUN_SERVERS, String.join(DELIMITER, STUN_SERVERS));

    LOGGER.info("Saved UserPrefs");
  }

  public static int getUdpPackageSizeStatic() {
    return PACKAGE_SIZE;
  }


  /**
   * Sets resource path, according to the application running either as a JAR or in the IDE.
   */
  private void defineResourcePath() {
    String userHome = System.getProperty("user.home");
    LOGGER.fine("userHome " + userHome);
    URL theLocationOftheRunningClass = this.getClass().getProtectionDomain()
        .getCodeSource().getLocation();
    LOGGER.fine("Source Location: " + theLocationOftheRunningClass);
    File actualClass = new File(theLocationOftheRunningClass.getFile());
    if (actualClass.isDirectory()) {
      LOGGER.fine("Running in IDE");
      // set paths of the files
      pathToResourceDocker =
          REMOVE_FILE_IN_PATH.apply(
              getClass().getClassLoader().getResource(DOCKER_FOLDER_NAME).getFile()
          );
      pathToDefaultSupporters =
          REMOVE_FILE_IN_PATH.apply(
              getClass().getClassLoader().getResource(DEFAULT_SUPPORTERS_FILE_NAME).getFile()
          );
      pathToOsxServer =
          REMOVE_FILE_IN_PATH.apply(
              getClass().getClassLoader().getResource(DEFAULT_OSX_SERVER_FILE_NAME).getFile()
          );
    } else {
      LOGGER.fine("Running in JAR");
      pathToResources = userHome + "/" + RSCC_FOLDER_NAME;
      // set paths of the files
      pathToResourceDocker = pathToResources + "/" + DOCKER_FOLDER_NAME;
      pathToDefaultSupporters = pathToResources + "/" + DEFAULT_SUPPORTERS_FILE_NAME;
      pathToOsxServer = pathToResources + "/" + DEFAULT_OSX_SERVER_FILE_NAME;
      // extract all resources out of the JAR file
      Arrays.stream(EXTRACTED_RESOURCES).forEach(resource ->
          extractJarContents(theLocationOftheRunningClass, pathToResources, resource)
      );
    }
  }

  /**
   * Extracts files from running JAR to folder.
   *
   * @param filter filters the files that will be extracted by this string.
   */
  private void extractJarContents(URL sourceLocation, String destinationDirectory, String filter) {
    JarFile jarFile = null;
    LOGGER.fine("Extract Jar Contents");
    try {
      LOGGER.fine("sourceLocation: " + sourceLocation.getFile());
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
        LOGGER.fine("JarEntry: " + item.getName());
        File targetFile = new File(destinationDirectory, item.getName());
        if (!targetFile.exists()) {
          targetFile.getParentFile().mkdirs();
          targetFile = new File(destinationDirectory, item.getName());
        }
        if (item.isDirectory()) {
          LOGGER.fine("JarEntry: " + item.getName() + " is a directory");
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
    SystemCommanderReturnValues returnValues = systemCommander.executeTerminalCommand(command);

    if (returnValues.getExitCode() != 0) {
      LOGGER.severe("Command failed: " + command + " ExitCode: " + returnValues.getExitCode());
      return;
    }
    isSshRunning.setValue(true);
  }


  /**
   * Kills the connection to the keyserver.
   */
  public void killConnection() {
    if (rscccfp != null) {
      LOGGER.info("RSCCFP not null. Close RSCCFP");
      rscccfp.closeConnection();
    }

    if (rudp != null) {
      LOGGER.info("Proxy not null. Try to close Proxy");
      rudp.closeRudpConnection();
    }

    if (vncServer != null && isVncServerProcessRunning()) {
      LOGGER.info("vncServer not null. Try to close VncServerProcess");
      vncServer.killVncServerProcess();
    }

    if (vncViewer != null && isVncViewerProcessRunning()) {
      LOGGER.info("Try to close VncViewer Process");
      vncViewer.killVncViewerProcess();
    }

    // Execute port_stop.sh with the generated key to kill the SSH connections
    LOGGER.info("SSH connection still active - try closing SSH connection");
    String command = systemCommander.commandStringGenerator(
        pathToResourceDocker, "port_stop.sh", keyUtil.getKey());
    systemCommander.executeTerminalCommand(command);
    keyUtil.setKey("");
    LOGGER.info("Everything should be closed");
  }


  /**
   * Requests a key from the key server.
   */
  public void requestKeyFromServer() {
    setConnectionEstablishmentRunning(true);
    setStatusBarKeyGeneration(strings.statusBarSettingKeyserver, STATUS_BAR_STYLE_INITIALIZE);

    keyServerSetup();

    setStatusBarKeyGeneration(strings.statusBarRequestingKey, STATUS_BAR_STYLE_INITIALIZE);

    String command = systemCommander.commandStringGenerator(
        pathToResourceDocker, "port_share.sh", Integer.toString(getVncPort()));

    SystemCommanderReturnValues returnValues = systemCommander.executeTerminalCommand(command);

    if (returnValues.getExitCode() != 0) {
      LOGGER.severe("Command failed: " + command + " ExitCode: " + returnValues.getExitCode());
      setStatusBarKeyGeneration(strings.statusBarKeyGeneratedFailed, STATUS_BAR_STYLE_FAIL);
      setIsKeyRefreshInProgress(false);
      return;
    }

    keyUtil.setKey(returnValues.getOutputString()); // update key in model
    rscccfp = new Rscccfp(this, true);
    rscccfp.setDaemon(true);
    rscccfp.start();

    setStatusBarKeyGeneration(strings.statusBarKeyGeneratedSuccess, STATUS_BAR_STYLE_INITIALIZE);
    setIsKeyRefreshInProgress(false);

    try {
      rscccfp.join();

      if (getRscccfpHasTalkedToOtherClient()) {
        LOGGER.info("RSCC: Starting VNCServer");

        vncServer = new VncServerHandler(this);
        vncServer.startVncServerListening();

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          LOGGER.info(e.getMessage());
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

        setStatusBarKeyGeneration(strings.statusBarVncServerWaiting, STATUS_BAR_STYLE_SUCCESS);

        setRscccfpHasTalkedToOtherClient(false);
      }

    } catch (Exception e) {
      LOGGER.info(e.getMessage());
      killConnection();
    }
    setConnectionEstablishmentRunning(false);
  }


  /**
   * Updates StatusBar on KeyGeneration.
   *
   * @param text       The Text to set.
   * @param styleClass The StyleClass to set to.
   */
  public void setStatusBarKeyGeneration(String text, String styleClass) {
    statusBarTextKeyGenerationProperty().set(text);
    statusBarStyleClassKeyGenerationProperty().set(styleClass);
  }

  /**
   * Updates StatusBar on KeyInput.
   *
   * @param text  The Text to set.
   * @param styleClass  The StyleClass to set to.
   */
  public void setStatusBarKeyInput(String text, String styleClass) {
    statusBarTextKeyInputProperty().set(text);
    statusBarStyleClassKeyInputProperty().set(styleClass);
  }

  /**
   * Updates StatusBar on StartService.
   *
   * @param text       The Text to set.
   * @param styleClass The StyleClass to set to.
   */
  public void setStatusBarStartService(String text, String styleClass) {
    statusBarTextStartServiceProperty().set(text);
    statusBarStyleClassStartServiceProperty().set(styleClass);
  }

  /**
   * Updates StatusBar on KeyInput.
   *
   * @param text       The Text to set.
   * @param styleClass The StyleClass to set to.
   */
  public void setStatusBarSupporter(String text, String styleClass) {
    statusBarTextSupporterProperty().set(text);
    statusBarStyleClassSupporterProperty().set(styleClass);
  }

  /**
   * Starts connection to the user.
   */
  public void connectToUser() {
    setConnectionEstablishmentRunning(true);

    setStatusBarKeyInput(strings.statusBarSettingKeyserver, STATUS_BAR_STYLE_INITIALIZE);

    keyServerSetup();

    String command = systemCommander.commandStringGenerator(pathToResourceDocker,
        "port_connect.sh", Integer.toString(getVncPort()), keyUtil.getKey());

    setStatusBarKeyInput(strings.statusBarKeyserverConnected, STATUS_BAR_STYLE_INITIALIZE);

    SystemCommanderReturnValues returnValues = systemCommander.executeTerminalCommand(command);

    if (returnValues.getExitCode() != 0) {
      LOGGER.severe("Command failed: " + command + " ExitCode: " + returnValues.getExitCode());
      setStatusBarKeyInput(strings.statusBarKeyNotVerified + getKeyUtil().getKey(),
          STATUS_BAR_STYLE_FAIL);

      setConnectionEstablishmentRunning(false);
      return;
    }

    rscccfp = new Rscccfp(this, false);
    rscccfp.setDaemon(true);
    rscccfp.start();

    try {
      rscccfp.join();
    } catch (InterruptedException e) {
      LOGGER.info(e.getMessage());
    }

    rudp = null;

    if (isLocalIceSuccessful) {
      rudp = new RunRudp(this, true, true);
    } else if (!isLocalIceSuccessful && isRemoteIceSuccessful) {
      rudp = new RunRudp(this, false, true);
    }
    vncViewer = new VncViewerHandler(this);

    if (rudp != null) {
      LOGGER.info("RSCC: Starting rudp");
      rudp.start();
    }

    LOGGER.info("RSCC: Starting VNCViewer");
    setStatusBarKeyInput(strings.statusBarVncViewerStarting, STATUS_BAR_STYLE_INITIALIZE);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int i = 0;
    int result = 1;
    while (i < 2 && result != 0) {
      result = vncViewer.startVncViewerConnecting("localhost",
          (rudp != null) ? getProxyPort() : vncPort.getValue());
      i++;
     try {
       Thread.sleep(1000);
      } catch (InterruptedException e) {
        LOGGER.info(e.getMessage());
      }
    }

    if (isVncSessionRunning()) {
      if (rudp != null) {
        setStatusBarKeyInput(strings.statusBarVncConnectionEstablishedICE,
            STATUS_BAR_STYLE_SUCCESS);
      } else {
        setStatusBarKeyInput(strings.statusBarVncConnectionEstablishedServer,
            STATUS_BAR_STYLE_SUCCESS);
      }
    //} else if (i == 10) {
    //  setStatusBarKeyInput(strings.statusBarConnectionFailed,
     //     STATUS_BAR_STYLE_SUCCESS);
   // }
    setConnectionEstablishmentRunning(false);
  }
  }

  /**
   * Refreshes the key by killing the connection, requesting a new key and starting the server
   * again.
   */
  public void refreshKey() {
    setIsKeyRefreshInProgress(true);
    killConnection();
    requestKeyFromServer();
  }

  /**
   * Starts VNCViewer in reverse mode (-listen).
   */
  public void startViewerReverse() {
    setConnectionEstablishmentRunning(true);
    if (vncViewer == null) {
      vncViewer = new VncViewerHandler(this);
    }
    vncViewer.startVncViewerListening();
    setConnectionEstablishmentRunning(false);
  }

  /**
   * Calls Supporter from addressbook (Starts VNC Server in Reverse mode).
   *
   * @param address     public reachable IP/Domain.
   * @param port        public reachable Port where vncViewer is listening.
   * @param isEncrypted sets if connection should be encrypted.
   */
  public void callSupporterDirect(String address, String port, boolean isEncrypted) {
    setConnectionEstablishmentRunning(true);
    setStatusBarSupporter(strings.statusBarConnectingTo + address + ":" + port,
        STATUS_BAR_STYLE_INITIALIZE);
    int portValue = -1;
    if (!port.equals("")) {
      portValue = Integer.parseInt(port);
    }
    vncServer = new VncServerHandler(this);
    vncServer.startVncServerReverse(address, portValue > 0 ? portValue : 5500, isEncrypted);
    setConnectionEstablishmentRunning(false);
  }

  /**
   * Starts the VNC Viewer as in listening mode.
   */
  public void startVncViewerAsService() {
    setConnectionEstablishmentRunning(true);
    setStatusBarStartService(strings.statusBarVncViewerServiceStarting,
        STATUS_BAR_STYLE_INITIALIZE);
    vncViewer = new VncViewerHandler(this);
    vncViewer.startVncViewerListening();
    setStatusBarStartService(strings.statusBarVncViewerServiceRunning,
        STATUS_BAR_STYLE_SUCCESS);

    setConnectionEstablishmentRunning(false);
  }

  /**
   * Stops the VNC Viewer.
   */
  public void stopVncViewerAsService() {
    setConnectionEstablishmentRunning(true);
    vncViewer.killVncViewerProcess();
    setStatusBarStartService(strings.statusBarVncViewerServiceStopped,
        STATUS_BAR_STYLE_INITIALIZE);

    setConnectionEstablishmentRunning(false);
  }

  // Getters and Setters from here
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

  public void setVncQuality(double vncQuality) {
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

  public void setStunServers(String[] servers) {
    STUN_SERVERS = servers;
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

  public boolean isForcingServerMode() {
    return forcingServerMode.get();
  }

  public void setForcingServerMode(boolean forcingServerMode) {
    this.forcingServerMode.set(forcingServerMode);
  }

  public BooleanProperty forcingServerModeProperty() {
    return forcingServerMode;
  }

  public SystemCommander getSystemCommander() {
    return systemCommander;
  }

  public boolean isVncSessionRunning() {
    return vncSessionRunning.get();
  }

  public void setVncSessionRunning(boolean vncSessionRunning) {
    this.vncSessionRunning.set(vncSessionRunning);
  }

  public BooleanProperty vncSessionRunningProperty() {
    return vncSessionRunning;
  }

  public VncServerHandler getVncServer() {
    return vncServer;
  }

  public void setVncServer(VncServerHandler vncServer) {
    this.vncServer = vncServer;
  }

  public boolean isVncServerProcessRunning() {
    return vncServerProcessRunning.get();
  }

  public void setVncServerProcessRunning(boolean vncServerProcessRunning) {
    this.vncServerProcessRunning.set(vncServerProcessRunning);
  }

  public BooleanProperty vncServerProcessRunningProperty() {
    return vncServerProcessRunning;
  }

  public boolean isVncViewerProcessRunning() {
    return vncViewerProcessRunning.get();
  }

  public void setVncViewerProcessRunning(boolean vncViewerProcessRunning) {
    this.vncViewerProcessRunning.set(vncViewerProcessRunning);
  }

  public BooleanProperty vncViewerProcessRunningProperty() {
    return vncViewerProcessRunning;
  }

  public boolean isConnectionEstablishmentRunning() {
    return connectionEstablishmentRunning.get();
  }

  public void setConnectionEstablishmentRunning(boolean connectionEstablishmentRunning) {
    this.connectionEstablishmentRunning.set(connectionEstablishmentRunning);
  }

  public BooleanProperty connectionEstablishmentRunningProperty() {
    return connectionEstablishmentRunning;
  }

  public boolean getRscccfpHasTalkedToOtherClient() {
    return rscccfpHasTalkedToOtherClient.get();
  }

  public void setRscccfpHasTalkedToOtherClient(boolean rscccfpHasTalkedToOtherClient) {
    this.rscccfpHasTalkedToOtherClient.set(rscccfpHasTalkedToOtherClient);
  }

  public BooleanProperty rscccfpHasTalkedToOtherClientProperty() {
    return rscccfpHasTalkedToOtherClient;
  }

  public int getUdpPackageSize() {
    return udpPackageSize.get();
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

  public String getPathToDefaultSupporters() {
    return pathToDefaultSupporters;
  }

  public RunRudp getRudp() {
    return rudp;
  }

  public static String getStatusBarStyleIdle() {
    return STATUS_BAR_STYLE_IDLE;
  }

  public static String getStatusBarStyleInitialize() {
    return STATUS_BAR_STYLE_INITIALIZE;
  }

  public static String getStatusBarStyleSuccess() {
    return STATUS_BAR_STYLE_SUCCESS;
  }

  public static String getStatusBarStyleFail() {
    return STATUS_BAR_STYLE_FAIL;
  }

  public String getStatusBarTextKeyGeneration() {
    return statusBarTextKeyGeneration.get();
  }

  public StringProperty statusBarTextKeyGenerationProperty() {
    return statusBarTextKeyGeneration;
  }

  public String getStatusBarStyleClassKeyGeneration() {
    return statusBarStyleClassKeyGeneration.get();
  }

  public StringProperty statusBarStyleClassKeyGenerationProperty() {
    return statusBarStyleClassKeyGeneration;
  }

  public String getStatusBarTextSupporter() {
    return statusBarTextSupporter.get();
  }

  public StringProperty statusBarTextSupporterProperty() {
    return statusBarTextSupporter;
  }

  public String getStatusBarStyleClassSupporter() {
    return statusBarStyleClassSupporter.get();
  }

  public StringProperty statusBarStyleClassSupporterProperty() {
    return statusBarStyleClassSupporter;
  }

  public String getStatusBarTextKeyInput() {
    return statusBarTextKeyInput.get();
  }

  public StringProperty statusBarTextKeyInputProperty() {
    return statusBarTextKeyInput;
  }

  public String getStatusBarStyleClassKeyInput() {
    return statusBarStyleClassKeyInput.get();
  }

  public StringProperty statusBarStyleClassKeyInputProperty() {
    return statusBarStyleClassKeyInput;
  }

  public String getStatusBarTextStartService() {
    return statusBarTextStartService.get();
  }

  public StringProperty statusBarTextStartServiceProperty() {
    return statusBarTextStartService;
  }

  public String getStatusBarStyleClassStartService() {
    return statusBarStyleClassStartService.get();
  }

  public StringProperty statusBarStyleClassStartServiceProperty() {
    return statusBarStyleClassStartService;
  }

  public boolean isKeyRefreshInProgres() {
    return isKeyRefreshInProgress.get();
  }

  public BooleanProperty isKeyRefreshInProgressProperty() {
    return isKeyRefreshInProgress;
  }

  public void setIsKeyRefreshInProgress(boolean isKeyRefreshInProgress) {
    this.isKeyRefreshInProgress.set(isKeyRefreshInProgress);
  }

  public CommandHandler getCommand() {
    return command;
  }

  public static String getPathToOsxServer() {
    return pathToOsxServer;
  }
}
