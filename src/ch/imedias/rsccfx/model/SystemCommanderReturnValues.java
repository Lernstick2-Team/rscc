package ch.imedias.rsccfx.model;

/**
 * Holds and transports values genereated by the SystemCommander, while executing TerminalCommands.
 */
public class SystemCommanderReturnValues {
  private String outputString;
  private String errorString;
  private int exitCode;

  /**
   * Parameterless Constructor.
   */
  public SystemCommanderReturnValues() {
  }

  /**
   * Constructor.
   * @param outputString String read from command on InputStream.
   * @param errorString String read from command on ErrorStream.
   * @param exitCode ExitCode from the command.
   */
  public SystemCommanderReturnValues(String outputString, String errorString, int exitCode) {
    this.outputString = outputString;
    this.errorString = errorString;
    this.exitCode = exitCode;
  }

  public String getOutputString() {
    return outputString;
  }

  public void setOutputString(String outputString) {
    this.outputString = outputString;
  }

  public String getErrorString() {
    return errorString;
  }

  public void setErrorString(String errorString) {
    this.errorString = errorString;
  }

  public int getExitCode() {
    return exitCode;
  }

  public void setExitCode(int exitCode) {
    this.exitCode = exitCode;
  }
}
