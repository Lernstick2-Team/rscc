package ch.imedias.rsccfx.model;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the SystemCommander class.
 */
public class SystemCommanderTest {

  SystemCommander systemCommander;

  /**
   * Initializes test fixture before each test.
   */
  @Before
  public void setUp() throws Exception {
    systemCommander = new SystemCommander();
  }

  /**
   * Test for {@link SystemCommander#executeTerminalCommand(String)}.
   */
  @Test
  public void testExecuteTerminalCommandIllegalArguments() throws Exception {
    try {
      systemCommander.executeTerminalCommand(null);
      fail("IllegalArgumentException was expected when Command is null");
    } catch (Exception e) {
      // expected behavior
    }
  }

  /**
   * Test for {@link SystemCommander#executeTerminalCommand(String)}.
   * Compares method output using standard Linux shell-command "echo".
   */
  @Test
  public void testExecuteTerminalCommand() throws Exception {
    String testTerminalCommand = "testExecuteTerminalCommand";
    StringBuilder command = new StringBuilder("echo ").append(testTerminalCommand);
    SystemCommanderReturnValues returnValues = systemCommander.executeTerminalCommand(command.toString());
    assertEquals(testTerminalCommand, returnValues.getOutputString());
  }

  /**
   * Test for {@link SystemCommander#commandStringGenerator(String, String, String...)}.
   */
  @Test
  public void testCommandStringGenerator() throws Exception {
    // given
    final String pathToScript = "path/To/Script";
    final String pathToScriptWithSlash = pathToScript + "/";
    final String scriptName = "script.sh";
    final String attribute = "attribute";
    final String[] attributes = {"-attr1", "attr2", "--attr3 4"};

    // without slash
    String generatedString = systemCommander.commandStringGenerator(
        pathToScript, scriptName, attribute);
    String expectedString = "path/To/Script/script.sh attribute";
    assertEquals(expectedString, generatedString);

    // with slash
    generatedString = systemCommander.commandStringGenerator(
        pathToScriptWithSlash, scriptName, attribute);
    expectedString = "path/To/Script/script.sh attribute";
    assertEquals(expectedString, generatedString);

    // null as pathToScript
    generatedString = systemCommander.commandStringGenerator(
        null, scriptName, attribute);
    expectedString = "script.sh attribute";
    assertEquals(expectedString, generatedString);

    // multiple attributes
    generatedString = systemCommander.commandStringGenerator(
        pathToScript, scriptName, attributes);
    expectedString = "path/To/Script/script.sh -attr1 attr2 --attr3 4";
    assertEquals(expectedString, generatedString);
  }

}
