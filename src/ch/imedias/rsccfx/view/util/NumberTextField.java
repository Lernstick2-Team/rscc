package ch.imedias.rsccfx.view.util;

import java.util.function.Predicate;
import javafx.scene.control.TextField;

public class NumberTextField extends TextField {

  /**
   * Returns true if the entered text is "", which means that the backspace key was pressed.
   */
  private static final Predicate<String> IS_BACKSPACE = ""::equals;

  /**
   * Replaces a range of characters with the given text.
   * Validation was added to the standard implementation.
   */
  @Override
  public void replaceText(int start, int end, String text) {
    if (validate(text)) {
      super.replaceText(start, end, text);
    }
  }

  /**
   * Replaces a range of characters with the given text upon selecting.
   * Validation was added to the standard implementation.
   */
  @Override
  public void replaceSelection(String text) {
    if (validate(text)) {
      super.replaceSelection(text);
    }
  }

  /**
   * Validates every input to make sure nothing but spaces, digits and backspaces can be entered.
   */
  private boolean validate(String text) {
    return text.matches("[0-9]*")
        || IS_BACKSPACE.test(text);
  }

}

