package ch.imedias.rsccfx.model.connectionutils;

import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.connectionutils.rudp.src.ReliableServerSocket;
import ch.imedias.rsccfx.model.connectionutils.rudp.src.ReliableSocket;
import ch.imedias.rsccfx.model.connectionutils.rudp.src.ReliableSocketProfile;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Establishes a RUDP connection between two clients, can be run as server or client.
 * Created by pwg on 09.05.17.
 */
public class RunRudp extends Thread {
  private static final Logger LOGGER = Logger.getLogger(Rscccfp.class.getName());


  private boolean exit = false;
  private Rscc model;
  private boolean viewerIsRudpClient;
  private boolean callAsViewer;
  private ReliableSocketProfile profile;

  private InputStream rudpInputStream;
  private OutputStream rudpOutputStream;
  private InputStream tcpInputStream;
  private OutputStream tcpOutputStream;
  private ReliableSocket rudpSocket;
  private Socket rudpSocket2;
  private ReliableServerSocket rudpServerSocket;
  private Socket tcpSocket;
  private ServerSocket tcpServerSocket;

  private ListMultimap<String, Closeable> closables = ArrayListMultimap.create();

  /**
   * Constructor.
   *
   * @param model              the one and only Model.
   * @param viewerIsRudpClient defines if the object is RUDP Client or Server.
   * @param callAsViewer       defines if the caller is vnc server or Client (vnc-viewer).
   */
  public RunRudp(Rscc model, boolean viewerIsRudpClient, boolean callAsViewer) {
    this.model = model;
    this.viewerIsRudpClient = viewerIsRudpClient;
    this.callAsViewer = callAsViewer;
  }

  /**
   * Starts the TCP and RUDP socket and routes the Packages in between over a Proxy.
   */
  public void run() {
    try {

      String remoteAddressAsString = model.getRemoteClientIpAddress().getHostAddress();

      model.setConnectionStatus("Trying to setup UDP proxy", 1);


      if (viewerIsRudpClient && callAsViewer) {
        //TCP Server & RUDP Client

        // RUDP Client
        LOGGER.info("Connect rudp to " + model.getRemoteClientIpAddress().getHostAddress()
            + ":" + model.getRemoteClientPort());

        //Could be an issue in a p2p session behind NAT, works locally.
        //    rudpSocket = new ReliableSocket(model.getRemoteClientIpAddress().getHostAddress(),
        // model.getRemoteClientPort(), null, model.getIcePort());
        // rudpSocket = new ReliableSocket(model.getRemoteClientIpAddress().getHostAddress(),
        //   model.getRemoteClientPort());

        rudpSocket = new ReliableSocket(model.getRemoteClientIpAddress().getHostAddress(),
            model.getRemoteClientPort(), null, model.getIcePort());

        rudpInputStream = rudpSocket.getInputStream();
        rudpOutputStream = rudpSocket.getOutputStream();

        LOGGER.info("Sucessfully connected rudp to " + model.getRemoteClientIpAddress()
            .getHostAddress() + ":" + model.getRemoteClientPort());

        //TCP Server
        LOGGER.info("Create new tcp-server on " + model.getProxyPort());
        tcpServerSocket = new ServerSocket(model.getProxyPort());
        tcpSocket = tcpServerSocket.accept();
        tcpSocket.setTcpNoDelay(true);

        tcpInputStream = tcpSocket.getInputStream();
        tcpOutputStream = tcpSocket.getOutputStream();

        LOGGER.info("Accepted incoming tcp connection from" + tcpSocket.getInetAddress()
            .getHostAddress());

        startProxy(tcpInputStream, tcpOutputStream, rudpInputStream, rudpOutputStream,
            model.getUdpPackageSize());

        rudpSocket.close();
        tcpSocket.close();
        tcpServerSocket.close();
      }


      if (viewerIsRudpClient && !callAsViewer) {
        //RUDP Server & TCP Client

        //RUDP Server
        LOGGER.info("Create new rudp-server on " + model.getIcePort());
        rudpServerSocket = new ReliableServerSocket(model.getIcePort());
        rudpSocket2 = rudpServerSocket.accept();

        rudpInputStream = rudpSocket2.getInputStream();
        rudpOutputStream = rudpSocket2.getOutputStream();
        LOGGER.info("Accepted incoming rudp connection from" + rudpSocket2.getInetAddress()
            .getHostAddress());

        //TCP Client
        LOGGER.info("Connect tcp to " + InetAddress.getLocalHost() + ":"
            + model.getVncPort());

        tcpSocket = new Socket(InetAddress.getByName("127.0.0.1"), model.getVncPort());

        tcpInputStream = tcpSocket.getInputStream();
        tcpOutputStream = tcpSocket.getOutputStream();

        LOGGER.info("Sucessful tcp connection");

        startProxy(tcpInputStream, tcpOutputStream, rudpInputStream, rudpOutputStream,
            model.getUdpPackageSize());

        rudpSocket2.close();
        tcpSocket.close();
        rudpServerSocket.close();
      }

      if (!viewerIsRudpClient && callAsViewer) {
        //TCP Server & RUDP Server


        //RUDP Server
        LOGGER.info("Create new rudp-server on " + model.getIcePort());
        rudpServerSocket = new ReliableServerSocket(model.getIcePort());
        rudpSocket2 = rudpServerSocket.accept();
        LOGGER.info("Accepted incoming rudp connection from" + rudpSocket2.getInetAddress()
            .getHostAddress());

        rudpInputStream = rudpSocket2.getInputStream();
        rudpOutputStream = rudpSocket2.getOutputStream();

        //TCP Server
        tcpServerSocket = new ServerSocket(model.getProxyPort());
        tcpSocket = tcpServerSocket.accept();
        tcpSocket.setTcpNoDelay(true);
        LOGGER.info("TCP connected");

        tcpInputStream = tcpSocket.getInputStream();
        tcpOutputStream = tcpSocket.getOutputStream();

        LOGGER.info("Accepted incoming tcp connection from" + tcpSocket.getInetAddress()
            .getHostAddress());

        startProxy(tcpInputStream, tcpOutputStream, rudpInputStream, rudpOutputStream,
            model.getUdpPackageSize());

        rudpSocket2.close();
        tcpSocket.close();
        rudpServerSocket.close();
      }


      if (!viewerIsRudpClient && !callAsViewer) {
        //TCP Client & RUDP Client

        // RUDP Client
        LOGGER.info("Connect rudp to " + model.getRemoteClientIpAddress().getHostAddress()
            + ":" + model.getRemoteClientPort());

        //possibly it can be run on any port? should at least.
        //model.getRemoteClientPort(), null, model.getIcePort()); alternative for starting Ice
        //on a fixed port (Could be an issue on p2p connection over NAT)

        // rudpSocket = new ReliableSocket(model.getRemoteClientIpAddress().getHostAddress(),
        //   model.getRemoteClientPort());
        rudpSocket = new ReliableSocket(model.getRemoteClientIpAddress().getHostAddress(),
            model.getRemoteClientPort(), null, model.getIcePort());

        rudpInputStream = rudpSocket.getInputStream();
        rudpOutputStream = rudpSocket.getOutputStream();

        LOGGER.info("Sucessfully connected rudp to " + model.getRemoteClientIpAddress()
            .getHostAddress() + ":" + model.getRemoteClientPort());

        //TCP Client
        LOGGER.info("Connect tcp to " + InetAddress.getByName("127.0.0.1").getHostAddress()
            + ":" + model.getVncPort());

        tcpSocket = new Socket(InetAddress.getByName("127.0.0.1"), model.getVncPort());

        tcpInputStream = tcpSocket.getInputStream();
        tcpOutputStream = tcpSocket.getOutputStream();

        LOGGER.info("Sucessful tcp connection");

        startProxy(tcpInputStream, tcpOutputStream, rudpInputStream, rudpOutputStream,
            model.getUdpPackageSize());
      }
    } catch (Exception e) {
      LOGGER.info(e.toString() + " " + e.getStackTrace());
    }
  }


  /**
   * Starts the Proxy.
   *
   * @param tcpInput   InputStream on TCP Socket.
   * @param tcpOutput  OutputSocket on TCP Socket.
   * @param rudpInput  InputStream on RUDP Socket.
   * @param rudpOutput OutputStream on RUDP Socket.
   * @param bufferSize Size of the Buffer per package.
   */
  private void startProxy(InputStream tcpInput, OutputStream tcpOutput, InputStream
      rudpInput, OutputStream rudpOutput, int bufferSize) {
    model.setConnectionStatus("UDP proxy succesful", 2);

    final byte[] request = new byte[bufferSize];
    byte[] reply = new byte[bufferSize];

    // a thread to read the client's requests and pass them
    // to the server. A separate thread for asynchronous.
    Thread t1 = new Thread() {
      public void run() {
        int bytesRead;
        try {
          while ((bytesRead = tcpInput.read(request)) != -1 && !exit) {
            rudpOutput.write(request, 0, bytesRead);
            //LOGGER.info("wrote1:" + bytesRead);
            rudpOutput.flush();
          }
        } catch (IOException e) {
          LOGGER.info(e.toString() + " " + e.getStackTrace());
        }

        // the client closed the connection to us, so close
        // connection to the server.
        try {
          rudpOutput.close();
        } catch (IOException e) {
          LOGGER.info(e.toString() + " " + e.getStackTrace());
        } finally {
          try {
            closeAll();

          } catch (Exception e) {
            LOGGER.info(e.toString() + " " + e.getStackTrace());
          }
        }
      }

    };

    // Start the client-to-server request thread running
    t1.start();

    // Read the server's responses
    // and pass them back to the client.

    int bytesRead;
    try {
      while ((bytesRead = rudpInput.read(reply)) != -1 && !exit) {
        tcpOutput.write(reply, 0, bytesRead);
        tcpOutput.flush();
      }
    } catch (IOException e) {
      LOGGER.info(e.toString() + " " + e.getStackTrace());
    } finally {
      try {
        closeAll();

      } catch (Exception e) {
        LOGGER.info(e.toString() + " " + e.getStackTrace());
      }
    }


    // The server closed its connection to us, so we close our
    // connection to our client.
  }

  /**
   * Stops the rudp-Proxa and closes all Sockets and streams.
   */
  public void closeRudpConnection() {
    this.exit = true;
    closeAll();
    this.exit = false;

  }

  private void closeAll() {
    setupClosables();
    closables.forEach(
        (name, closeable) -> {
          if (closeable != null) {
            LOGGER.info(name + " is not null - close");
            try {
              closeable.close();
            } catch (IOException e) {
              LOGGER.warning(e.getMessage());
            }
          }
        }
    );
  }

  private void setupClosables() {

    if (tcpInputStream != null) {
      LOGGER.info("tcpInputStream is not null - close");
      try {
        tcpInputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (rudpInputStream != null) {
      LOGGER.info("rudpInputStream is not null - close");
      try {
        rudpInputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (tcpOutputStream != null) {
      LOGGER.info("tcpOutputStream is not null - close");
      try {
        tcpOutputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (rudpOutputStream != null) {
      LOGGER.info("rudpOutputStream is not null - close");
      try {
        rudpOutputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (rudpSocket != null && !rudpSocket.isClosed()) {
      LOGGER.info("rudpSocket is not null - close");
      try {
        rudpSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (rudpSocket2 != null && !rudpSocket2.isClosed()) {
      LOGGER.info("rudpSocket2 is not null - close");
      try {
        rudpSocket2.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (rudpServerSocket != null && !rudpServerSocket.isClosed()) {
      LOGGER.info("rudpServerSocket is not null - close");
      try {
        rudpServerSocket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (tcpServerSocket != null && !tcpServerSocket.isClosed()) {
      LOGGER.info("tcpServerSocket is not null - close");
      try {
        tcpServerSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (tcpSocket != null && !tcpSocket.isClosed()) {
      LOGGER.info("tcpSocket is not null - close");
      try {
        tcpSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}











