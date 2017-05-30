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

  SupporterHelper supporterHelper;
  Rscc mockModel;
  String supportersXml;
  Supporter supporter1;
  Supporter supporter2;
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

    supportersXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<supporters>\n" +
        "    <supporter>\n" +
        "        <description>imedias</description>\n" +
        "        <address>agora.imedias.ch</address>\n" +
        "        <port>5500</port>\n" +
        "        <encrypted>false</encrypted>\n" +
        "        <chargeable>false</chargeable>\n" +
        "    </supporter>\n" +
        "    <supporter>\n" +
        "        <description>imedias (encrypted)</description>\n" +
        "        <address>agora.imedias.ch</address>\n" +
        "        <port>50000</port>\n" +
        "        <encrypted>true</encrypted>\n" +
        "        <chargeable>false</chargeable>\n" +
        "    </supporter>\n" +
        "</supporters>";


    supporter1 = new Supporter("imedias", "agora.imedias.ch", "5500", false, false);
    supporter2 = new Supporter("imedias (encrypted)", "agora.imedias.ch", "50000", true, false);
    expectedSupporters = new ArrayList<>();
    expectedSupporters.add(supporter1);
    expectedSupporters.add(supporter2);
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
  }

  /**
   * Test for {@link SupporterHelper#getDefaultSupporters()}.
   */
  @Test
  public void testGetDefaultSupporters() throws Exception {
  }

}
