package ch.imedias.rsccfx.model.xml;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.imedias.rsccfx.model.Rscc;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by François Martin on 25.05.17.
 */
public class SupporterHelperTest {

  SupporterHelper supporterHelper;
  Rscc mockModel;
  String supportersXml;

  /**
   * Initializes test fixture before each test.
   */
  @Before
  public void setUp(){
    mockModel = mock(Rscc.class);
    supporterHelper = new SupporterHelper(mockModel);

    String pathToDefaultSupporters = Rscc.REMOVE_FILE_IN_PATH.apply(
        getClass().getClassLoader().getResource(Rscc.DEFAULT_SUPPORTERS_FILE_NAME).getFile()
    );
    when(mockModel.getPathToDefaultSupporters()).thenReturn(pathToDefaultSupporters);
    supportersXml = ""
  }

  /**
   * Test for {@link SupporterHelper#loadSupporters()}.
   */
  @Test
  public void testLoadSupporters() throws Exception {

  }

  @Test
  public void testSaveSupporters() throws Exception {
  }

  @Test
  public void testGetDefaultSupporters() throws Exception {
  }

}
