package ch.imedias.rsccfx.model.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Streams;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Stores the key, handles the formatting and validation of the key.
 */
public class KeyUtil {

  private static final Logger LOGGER =
      Logger.getLogger(KeyUtil.class.getName());
  public static final int KEY_DELIMITER_EVERY = 3;
  public static final int KEY_MAXIMUM_DIGITS = 9;
  public static final int KEY_AMOUNT_SPACES =
      (KEY_MAXIMUM_DIGITS - KEY_DELIMITER_EVERY) / KEY_DELIMITER_EVERY;
  private static final String KEY_DELIMITER = " ";

  /**
   * Represents the key, which is a number with 9 digits.
   * Example: 123456789
   */
  private final StringProperty key = new SimpleStringProperty();

  /**
   * Represents a formatted version of the key with added spaces every 3 digits.
   * Example: 123 456 789
   */
  private final StringProperty formattedKey = new SimpleStringProperty();

  private final BooleanProperty keyValid = new SimpleBooleanProperty(false);

  public KeyUtil() {
    setupBindings();
  }

  private void setupBindings() {
    // if the key gets changed, this sets the formatted key
    formattedKey.bind(
        Bindings.createStringBinding(
            () -> formatKey(getKey()), key
        )
    );

    // if the key gets changed, the key will get validated
    keyValid.bind(
        Bindings.createBooleanBinding(
            () -> validateKey(getKey()), key
        )
    );
  }

  /**
   * Formats the key, so it has spaces every 3 characters.
   *
   * @param key the unformatted key which should be formatted.
   * @return the formatted key, with spaces every 3 characters.
   */
  public String formatKey(String key) {
    if (key != null) {
      key = deformatKey(key); // make sure the key doesn't have spaces in it already
      // shorten key to 9 characters if it's too long
      if (key.length() > 9) {
        key = key.substring(0, 9);
      }
      // Split the key and join with delimiter
      Iterable<String> pieces = Splitter.fixedLength(KEY_DELIMITER_EVERY).split(key);
      key = Streams.stream(pieces)
          .collect(Collectors.joining(KEY_DELIMITER));
    }
    return key;
  }

  /**
   * Removes spaces in a key which has been previously formatted with spaces.
   *
   * @param key the key, possibly with spaces in it.
   * @return the key without any spaces.
   */
  public String deformatKey(String key) {
    if (key != null) {
      key = key.replace(KEY_DELIMITER, "");
    }
    return key;
  }

  /**
   * Determines if a key is valid or not.
   * The key must not be null and must be a number with exactly 9 digits.
   *
   * @param key the string to validate.
   * @return true when key has a valid format.
   */
  public boolean validateKey(String key) {
    return key != null && key.matches("\\d{9}");
  }

  public String getKey() {
    return key.get();
  }

  /**
   * Sets the key deformatted.
   *
   * @param key can be a formatted or unformatted key.
   */
  public void setKey(String key) {
    this.key.set(deformatKey(key));
    LOGGER.fine("Key was set to: " + getKey() + " and " + getFormattedKey());
  }

  public ReadOnlyStringProperty keyProperty() {
    return key;
  }

  public String getFormattedKey() {
    return formattedKey.get();
  }

  public ReadOnlyStringProperty formattedKeyProperty() {
    return formattedKey;
  }

  public boolean isKeyValid() {
    return keyValid.get();
  }

  public ReadOnlyBooleanProperty keyValidProperty() {
    return keyValid;
  }
}
