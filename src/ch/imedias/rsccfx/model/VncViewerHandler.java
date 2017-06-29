package ch.imedias.rsccfx.model;

import com.tigervnc.rdr.EndOfStream;
import com.tigervnc.vncviewer.VncViewer;

import java.security.Permission;
import java.util.Arrays;
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
  public int startVncViewerConnecting(String hostAddress, Integer vncViewerPort) {

        LOGGER.info("Starting VNC Viewer Connection");
        String[] args = {hostAddress + ":" + vncViewerPort};
        LOGGER.info("Starting VNCViewer with args: " + Arrays.toString(args));
        int result = startVncViewer(args);
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

        return result;

  }


  /**
   * Starts this VNCViewer listening on localhost.
   */
  public void startVncViewerListening() {
    Thread startListening = new Thread() {
      public void run() {
        String[] args = {"-listen"};
        LOGGER.info("Starting VNCViewer with args: " + Arrays.toString(args));
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

  /**
   * Starts the TigerVNC viewer, implemented as JAR dependency.
   * @param args the arguments to be used in the viewer
   * @return 0, if it was exited by the user
   *          1, if the connection could not be made
   *          -1, if the interruption was unexpected
     */
  private int startVncViewer(String[] args) {
   for(String s:args) {
     System.out.print(s);
   }
    System.out.println();
   //String[] newArgs={"localhost:2601"};
     viewer = new VncViewer(args);

    // prevent the VncViewer from calling "System.exit(n)"

    forbidSystemExitCall();
    try {
      viewer.start();
    } catch(EndOfStream eos) {
      LOGGER.info("Return End of stream");
      return 1;
    } catch( ExitTrappedException e ) {
      LOGGER.info("Return closed window");
      // expected behavior, don't allow the System to be exited
      return 0;
    } finally {
      enableSystemExitCall() ;
    }
    LOGGER.info("Return unexpected exception");
    return -1;
  }

  private static class ExitTrappedException extends SecurityException { }

  private static void forbidSystemExitCall() {
    final SecurityManager securityManager = new SecurityManager() {
      public void checkPermission( Permission permission ) {
        if( "exitVM".equals( permission.getName() ) ) {
          throw new ExitTrappedException() ;
        }
      }
    } ;
    System.setSecurityManager( securityManager ) ;
  }

  private static void enableSystemExitCall() {
    System.setSecurityManager( null ) ;
  }

}
