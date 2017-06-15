package ch.imedias.rsccfx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.imedias.rsccfx.model.util.KeyUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the rscc model class.
 */
public class RsccTest {
  private static final String KEY = "123456789";
  private static final String KEY_SERVER_IP = "86.119.39.89";
  private static final String KEY_SERVER_HTTP_PORT = "800";

  Rscc model;
  SystemCommanderReturnValues returnValues;
  SystemCommander mockSystemCommander;
  KeyUtil mockKeyUtil;


  /**
   * Initializes test fixture before each test.
   */
  @Before
  public void setUp() throws Exception {
    mockSystemCommander = mock(SystemCommander.class);
    mockKeyUtil = mock(KeyUtil.class);
    model = new Rscc(mockSystemCommander, mockKeyUtil);
    returnValues = new SystemCommanderReturnValues();
    // since commandStringGenerator is mainly a utility function and is being tested separately
    // call the real method
    doCallRealMethod().when(mockSystemCommander).commandStringGenerator(any(), any(), any());
    model.setKeyServerIp(KEY_SERVER_IP);
    model.setKeyServerHttpPort(KEY_SERVER_HTTP_PORT);
    returnValues.setOutputString(KEY);
    when(mockSystemCommander.executeTerminalCommand(
        argThat(string -> string.contains("port_share.sh")))).thenReturn(returnValues);
    when(mockKeyUtil.getKey()).thenReturn(KEY);
  }

  /**
   * Test for Constructor {@link Rscc#Rscc(SystemCommander, KeyUtil)}.
   */
  @Test
  public void testRsccConstructorIllegalArguments() {
    try {
      new Rscc(null, mockKeyUtil);
      fail("IllegalArgumentException was expected when SystemCommander is null");
    } catch (IllegalArgumentException e) {
      // expected behavior
    }

    try {
      new Rscc(mockSystemCommander, null);
      fail("IllegalArgumentException was expected when KeyUtil is null");
    } catch (IllegalArgumentException e) {
      // expected behavior
    }

    try {
      new Rscc(null, null);
      fail("IllegalArgumentException was expected when all parameters are null");
    } catch (IllegalArgumentException e) {
      // expected behavior
    }
  }

  /**
   * Test for Constructor {@link Rscc#Rscc(SystemCommander, KeyUtil)}.
   */
  @Test
  public void testRsccConstructor() {
    try {
      new Rscc(mockSystemCommander, mockKeyUtil);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test for {@link Rscc#keyServerSetup()}.
   * Not marked with a @Test annotation because it is indirectly called in other tests.
   */
  public void testKeyServerSetup() throws Exception {
    verify(mockSystemCommander).executeTerminalCommand(
        argThat(script -> script.contains("use.sh")
            && script.contains(KEY_SERVER_IP)
            && script.contains(KEY_SERVER_HTTP_PORT)));
  }

  /**
   * Test for {@link Rscc#killConnection()}.
   */
  @Test
  public void testKillConnection() throws Exception {
    model.killConnection();
    verify(mockSystemCommander).executeTerminalCommand(
        argThat(script -> script.contains("port_stop.sh")
            && script.endsWith(KEY)));
    verify(mockKeyUtil).getKey();
  }

  /**
   * Test for {@link Rscc#requestKeyFromServer()}.
   */
  @Ignore
  public void testRequestKeyFromServer() throws Exception {
    model.requestKeyFromServer();
    testKeyServerSetup();
    // make sure the script was executed
    verify(mockSystemCommander).executeTerminalCommand(
        argThat(script -> script.contains("port_share.sh")));
    // make sure the key which is being returned is set right
    verify(mockKeyUtil).setKey(KEY);
  }

  /**
   * Test for {@link Rscc#connectToUser()}.
   */
  @Ignore
  public void testConnectToUser() throws Exception {
    model.connectToUser();
    // make sure the scripts were executed
    this.testKeyServerSetup();
    verify(mockSystemCommander).executeTerminalCommand(
        argThat(script -> script.contains("port_connect.sh")
            && script.endsWith(KEY)));
    verify(mockKeyUtil).getKey();
  }

  /**
   * Test for {@link Rscc#refreshKey()}.
   */
  @Ignore
  public void testRefreshKey() {
    model.refreshKey();
    // make sure the scripts were executed
    verify(mockSystemCommander).executeTerminalCommand(
        argThat(script -> script.contains("port_stop.sh")));
    verify(mockSystemCommander).executeTerminalCommand(
        argThat(script -> script.contains("port_share.sh")));
    // make sure the key which is being returned is set right
    verify(mockKeyUtil).setKey(KEY);
  }

  //  /**
  //   * Test for {@link Rscc#startVncServer()}.
  //   */
  //  @Test
  //  public void testStartVncServer() {
  //    model.startVncServer();
  //    // make sure the scripts were executed
  //    verify(mockSystemCommander).executeTerminalCommand(
  //        argThat(script -> script.contains("x11vnc")));
  //  }

  //  /**
  //   * Test for {@link Rscc#startVncViewer(String, Integer)}.
  //   */
  //  @Test
  //  public void testStartVncViewer() {
  //    String hostAddress = "localhost";
  //    int vncPort = 5900;
  //    model.startVncViewer(hostAddress, vncPort);
  //    // make sure the scripts were executed
  //    verify(mockSystemCommander).executeTerminalCommand(
  //        argThat(script -> script.contains("vncviewer")
  //            && script.contains(hostAddress)));
  //  }

  //  /**
  //   * Test for {@link Rscc#startVncViewer(String, Integer)}.
  //   */
  //  @Test
  //  public void testStartVncViewerIllegalArgument() {
  //    int vncPort = 5900;
  //    try {
  //
  //      model.startVncViewer(null, 5900);
  //      fail("IllegalArgumentException was expected when HostAddress is null");
  //    } catch (IllegalArgumentException e) {
  //      // expected behavior
  //    }
  //  }

  /**
   * Test for all StatusBar setters.
   */
  @Test
  public void testSetConnectionStatus() {
    String statusText = "test";
    model.setStatusBarKeyGeneration(statusText, model.STATUS_BAR_STYLE_IDLE);
    assertEquals(model.statusBarStyleClassKeyGenerationProperty().getValue(),
        model.STATUS_BAR_STYLE_IDLE);
    assertEquals(model.statusBarTextKeyGenerationProperty().getValue(), statusText);

    model.setStatusBarKeyInput(statusText, model.STATUS_BAR_STYLE_IDLE);
    assertEquals(model.statusBarStyleClassKeyInputProperty().getValue(),
        model.STATUS_BAR_STYLE_IDLE);
    assertEquals(model.statusBarTextKeyInputProperty().getValue(), statusText);

    model.setStatusBarStartService(statusText, model.STATUS_BAR_STYLE_IDLE);
    assertEquals(model.statusBarStyleClassStartServiceProperty().getValue(),
        model.STATUS_BAR_STYLE_IDLE);
    assertEquals(model.statusBarTextStartServiceProperty().getValue(), statusText);

    model.setStatusBarSupporter(statusText, model.STATUS_BAR_STYLE_IDLE);
    assertEquals(model.statusBarStyleClassSupporterProperty().getValue(),
        model.STATUS_BAR_STYLE_IDLE);
    assertEquals(model.statusBarTextSupporterProperty().getValue(), statusText);
  }

}
