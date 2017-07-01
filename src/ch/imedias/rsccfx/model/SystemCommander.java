package ch.imedias.rsccfx.model;

import com.google.common.base.CharMatcher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Logger;

public class SystemCommander {
  private static final Logger LOGGER =
      Logger.getLogger(SystemCommander.class.getName());

  /**
   * Executes a command in the Linux terminal.
   * More detailed instructions concerning this command can be found here:
   * https://www.cs.technik.fhnw.ch/confluence16/pages/viewpage.action?pageId=6929334
   *
   * @param command terminal command to be executed.
   * @return String trimmed output of the terminal without whitespaces at beginning / end.
   */
  public SystemCommanderReturnValues executeTerminalCommand(String command) {
    Process process;
    SystemCommanderReturnValues response = new SystemCommanderReturnValues();
    try {
      StringBuilder output = new StringBuilder();
      StringBuilder error = new StringBuilder();
      // Execute Command
      process = Runtime.getRuntime().exec(command);
      int exitCode = process.waitFor();
      response.setExitCode(exitCode);
      // read the output from the command
      BufferedReader outputReader = new BufferedReader(new
          InputStreamReader(process.getInputStream()));
      BufferedReader errorReader = new BufferedReader(new
          InputStreamReader(process.getErrorStream()));
      String line;
      while ((line = outputReader.readLine()) != null) {
        output.append(line).append("\n");
      }
      while ((line = errorReader.readLine()) != null) {
        error.append(line).append("\n");
      }
      outputReader.close();
      errorReader.close();
      response.setOutputString(output.toString().trim());
      response.setErrorString(output.toString().trim());
    } catch (Exception exception) {
      LOGGER.severe("Exception thrown when running the command: "
          + command
          + "\n Exception Message: " + exception.getMessage());
      exception.printStackTrace();
      throw new IllegalArgumentException();
    }

    return response;
  }


  /**
   * Starts and returns a Process based on a conmmand.
   * @param command A String to be executed.
   * @return A Process-Object.
   */
  public Process startProcess(String command) {
    Process process;
    try {
      process = Runtime.getRuntime().exec(command);
    } catch (Exception exception) {
      LOGGER.severe("Exception thrown when running the command: "
          + command
          + "\n Exception Message: " + exception.getMessage());
      throw new IllegalArgumentException();
    }
    return process;
  }



  /**
   * Generates String to run command.
   *
   * @param pathToScript path to the script that should be run.
   *                     Should be fully qualified but can also be null.
   * @param scriptName   name of the script to be run.
   * @param attributes   optional arguments that should be included in the command.
   * @return combines all the params into one simple command for the terminal
   *                     and returns it as a string.
   */
  public String commandStringGenerator(
      String pathToScript, String scriptName, String... attributes) {
    StringBuilder commandString = new StringBuilder();

    if (pathToScript != null) {
      // remove all slashes at the end
      pathToScript = CharMatcher.is('/').trimTrailingFrom(pathToScript);
      // append slash to separate from script name
      commandString.append(pathToScript).append("/");
    }
    commandString.append(scriptName);
    Arrays.stream(attributes)
        .forEach((s) -> commandString.append(" ").append(s));

    return commandString.toString();
  }
}
