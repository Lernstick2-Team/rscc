package ch.imedias.rsccfx.model.xml;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.imedias.rsccfx.model.Rscc;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by François Martin on 25.05.17.
 */
public class SupporterHelperTest {

  public static final int DEFAULT_SUPPORTER_SIZE = 7;
  SupporterHelper supporterHelper;
  Rscc mockModel;
  String supportersXml;
  Supporter supporter1;
  Supporter supporter2;
  Supporter supporterLast;
  List<Supporter> expectedSupporters;

  /**
   * Initializes test fixture before each test.
   */
  @Before
  public void setUp() {
    mockModel = mock(Rscc.class);
    supporterHelper = new SupporterHelper(mockModel);

    String pathToDefaultSupporters = Rscc.REMOVE_FILE_IN_PATH.apply(
        getClass().getClassLoader().getResource(Rscc.DEFAULT_SUPPORTERS_FILE_NAME).getFile()
    );
    when(mockModel.getPathToDefaultSupporters()).thenReturn(pathToDefaultSupporters);

    supportersXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
        + "<supporters>\n"
        + "    <supporter>\n"
        + "        <address>agora.imedias.ch</address>\n"
        + "        <chargeable>false</chargeable>\n"
        + "        <description>imedias</description>\n"
        + "        <encrypted>false</encrypted>\n"
        + "        <port>5500</port>\n"
        + "    </supporter>\n"
        + "    <supporter>\n"
        + "        <address>agora.imedias.ch</address>\n"
        + "        <chargeable>false</chargeable>\n"
        + "        <description>imedias (encrypted)</description>\n"
        + "        <encrypted>true</encrypted>\n"
        + "        <port>50000</port>\n"
        + "    </supporter>\n"
        + "</supporters>\n";


    supporter1 = new Supporter("imedias", "agora.imedias.ch", "5500",
        false, false);
    supporter2 = new Supporter("imedias (encrypted)", "agora.imedias.ch", "50000",
        true, false);
    expectedSupporters = new ArrayList<>();
    expectedSupporters.add(supporter1);
    expectedSupporters.add(supporter2);
    supporterLast = new Supporter();
  }

  /**
   * Test for {@link SupporterHelper#loadSupporters()}.
   */
  @Test
  public void testLoadSupporters() throws Exception {
    // test if preferences are null
    supporterHelper.setSupportersInPreferences(null);
    testGetDefaultSupporters();
    // testif preferenes are not null
    supporterHelper.setSupportersInPreferences(supportersXml);
    List<Supporter> actualSupporters = supporterHelper.loadSupporters();
    assertEquals(2, actualSupporters.size());
    assertEquals(supporter1, actualSupporters.get(0));
    assertEquals(supporter2, actualSupporters.get(1));
  }

  /**
   * Test for {@link SupporterHelper#saveSupporters(List)}.
   */
  @Test
  public void testSaveSupporters() throws Exception {
    supporterHelper.setSupportersInPreferences(null);
    supporterHelper.saveSupporters(expectedSupporters);
    assertEquals(supportersXml, supporterHelper.getSupportersXmlFromPreferences());
  }

  /**
   * Test for {@link SupporterHelper#getDefaultSupporters()}.
   */
  @Test
  public void testGetDefaultSupporters() throws Exception {
    List<Supporter> actualSupporters = supporterHelper.getDefaultSupporters();
    assertEquals(DEFAULT_SUPPORTER_SIZE, actualSupporters.size());
    assertEquals(supporter1, actualSupporters.get(0));
    assertEquals(supporter2, actualSupporters.get(1));
    assertEquals(supporterLast, actualSupporters.get(DEFAULT_SUPPORTER_SIZE - 1));
  }

}
