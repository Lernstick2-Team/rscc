package ch.imedias.rsccfx.model.xml;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.imedias.rsccfx.model.Rscc;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Supporter class.
 */
public class SupporterTest {

  Supporter supporter;

  /**
   * Initializes test fixture before each test.
   */
  @Before
  public void setUp() {
    supporter = new Supporter();
  }

  @Test
  public void testToString() throws Exception {
    // if description is empty, the toString representation should be a '+'
    supporter.setDescription("");
    assertEquals("+", supporter.toString());

    // if description is NOT empty, the toString representation should be the description itself
    String testDescription = "Test Description";
    supporter.setDescription(testDescription);
    assertEquals(testDescription, supporter.toString());
  }

}
