package ch.imedias.rsccfx.model;

import com.google.common.base.CharMatcher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Logger;

public class SystemCommander {
  private static final Logger LOGGER =
      Logger.getLogger(SystemCommander.class.getName());
  private Rscc model;

  /**
   * Executes a command in the Linux terminal.
   * More detailed instructions concerning this command can be found here:
   * https://www.cs.technik.fhnw.ch/confluence16/pages/viewpage.action?pageId=6929334
   *
   * @param command terminal command to be executed.
   * @return String trimmed output of the terminal without whitespaces at beginning / end.
   */
  public String executeTerminalCommand(String command) {
    Process process;
    String outputString = ""; // standard return value
    try {
      StringBuilder output = new StringBuilder();
      // Execute Command
      process = Runtime.getRuntime().exec(command);
      int exitCode = process.waitFor();
      System.out.println(exitCode);
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
        output.append(line).append("\n");
      }
      outputReader.close();
      errorReader.close();
      outputString = output.toString().trim();
    } catch (Exception exception) {
      LOGGER.severe("Exception thrown when running the command: "
          + command
          + "\n Exception Message: " + exception.getMessage());
      throw new IllegalArgumentException();
    }
    return outputString;
  }


  /**
   * Executes a TerminalCommand, that listen for a specified StringOutput and sets the
   * setIsVncSessionRunning accordingly.
   *
   * @param command                 to be executed
   * @param whatTerminalNeedsToShow String to compare to and when to set connection ongoing in model
   */
  public String executeTerminalCommandAndUpdateModel(String command,
                                                     String whatTerminalNeedsToShow) {
    Process p;
    StringBuilder output = new StringBuilder();

    try {
      p = Runtime.getRuntime().exec(command);
      final InputStream errorStream = p.getErrorStream();
      final InputStream inputStream = p.getInputStream();

      Thread t = new Thread(new Runnable() {
        public void run() {
          BufferedReader reader = null;
          BufferedReader reader2 = null;
          try {
            reader = new BufferedReader(new InputStreamReader(errorStream));
            reader2 = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
              if (line.contains(whatTerminalNeedsToShow)) {
                model.setIsVncSessionRunning(true);
              }
              output.append(line);
            }
            while ((line = reader2.readLine()) != null) {
              if (line.contains(whatTerminalNeedsToShow)) {
                model.setIsVncSessionRunning(true);
              }
              output.append(line);
            }

          } catch (Exception e) {
            LOGGER.info(e + " " + e.getStackTrace());

          } finally {
            if (reader != null) {
              try {
                reader.close();
              } catch (IOException e) {
                LOGGER.info(e + " " + e.getStackTrace());

              }
            }
          }
        }
      });
      t.start();
      t.join();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      model.setIsVncSessionRunning(false);
    }
    return output.toString();

  }


  /**
   * Generates String to run command.
   *
   * @param pathToScript path to the script that should be run.
   *                     Should be fully qualified but can also be null.
   * @param scriptName   name of the script to be run.
   * @param attributes   optional arguments that should be included in the command.
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

  public void setModel(Rscc model) {
    this.model = model;
  }
}
