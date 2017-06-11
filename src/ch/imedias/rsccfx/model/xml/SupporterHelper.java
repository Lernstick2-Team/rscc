package ch.imedias.rsccfx.model.xml;

import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.model.Rscc;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

/**
 * Manages Supporter data, used in conjunction with the list of predefined supporters.
 */
public class SupporterHelper {

  private static final Logger LOGGER =
      Logger.getLogger(SupporterHelper.class.getName());
  private static final String SUPPORTER_PREFERENCES = "supporter";
  private final Preferences preferences = Preferences.userNodeForPackage(RsccApp.class);
  private Rscc model;

  /**
   * Initializes a new SupporterHelper object.
   *
   * @param model Rscc model.
   */
  public SupporterHelper(Rscc model) {
    this.model = model;
  }

  /**
   * Gets the supporter list from the preferences file.
   * If no preferences are found the default list is generated.
   */
  public List<Supporter> loadSupporters() {
    // load preferences
    String supportersXml = getSupportersXmlFromPreferences();
    if (supportersXml == null) {
      // use some hardcoded defaults
      return getDefaultSupporters();
    } else {
      return getSupportersFromXml(supportersXml);
    }
  }

  /**
   * Saves supporters from a list to the preferences file.
   */
  public void saveSupporters(List<Supporter> supporters) {
    String supportersXml = supportersToXml(supporters);
    setSupportersInPreferences(supportersXml);
  }

  /**
   * Returns a default list of supporters.
   */
  public List<Supporter> getDefaultSupporters() {
    LOGGER.info("Loading default supporter list");
    File supportersXmlFile;
    try {
      supportersXmlFile = new File(model.getPathToDefaultSupporters());
    } catch (NullPointerException e) {
      return null;
    }
    return getSupportersFromXml(supportersXmlFile);
  }

  private List<Supporter> getSupportersFromXml(File file) {
    return getSupportersFromXml(fileToString(file));
  }

  private List<Supporter> getSupportersFromXml(String string) {
    List<Supporter> supportersList = null;
    if (string == null) {
      LOGGER.info("String to create a list of supporters from is null!");
      return null;
    }
    StringReader reader = new StringReader(string);

    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Supporters.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      Supporters supporters = (Supporters) jaxbUnmarshaller.unmarshal(reader);

      supportersList = supporters.getSupporters();
    } catch (UnmarshalException unmarshalException) {
      // gets thrown when the format is invalid, in this case return default
      supportersList = getDefaultSupporters();
    } catch (JAXBException e) {
      LOGGER.warning(e.getMessage());
    }
    return supportersList;
  }

  private String supportersToXml(List<Supporter> supporters) {
    String string = null;

    Supporters supportersWrapper = new Supporters();
    supportersWrapper.setSupporters(supporters);

    StringWriter writer = new StringWriter();

    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Supporters.class);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      jaxbMarshaller.marshal(supportersWrapper, writer);

      string = writer.toString();
    } catch (JAXBException e) {
      LOGGER.warning(e.getMessage());
    }
    return string;
  }

  private String fileToString(File file) {
    String output = null;
    try {
      output = Files.toString(file, Charsets.UTF_8);
    } catch (IOException e) {
      LOGGER.warning("IOException during conversion of file to string! " + e.getMessage());
    }
    return output;
  }

  /**
   * Gets the supporters from the saved preferences.
   * @return saved preferences
   */
  public String getSupportersXmlFromPreferences() {
    return preferences.get(SUPPORTER_PREFERENCES, null);
  }

  /**
   * Saves the preferences.
   * @param supportersXmlString supporters which need to be saved
   */
  public void setSupportersInPreferences(String supportersXmlString) {
    if (supportersXmlString != null) {
      preferences.put(SUPPORTER_PREFERENCES, supportersXmlString);
    } else {
      preferences.remove(SUPPORTER_PREFERENCES);
    }
  }


}

