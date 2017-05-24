package ch.imedias.rsccfx.model;

/**
 * Created by jp on 24/05/17.
 */
public class SystemCommanderReturnValues {
  private String outputString;
  private String errorString;
  private int exitCode;

  public SystemCommanderReturnValues() {
  }

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
