package ch.imedias.rsccfx.model.xml;

import static org.junit.Assert.assertEquals;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
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

  /**
   * Test for {@link Supporter#toString()}.
   */
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

  /**
   * Test for {@link Supporter#equals(Object)} and {@link Supporter#hashCode()}.
   */
  @Test
  public void testEqualsContract() {

    EqualsVerifier
        .forClass(Supporter.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();

  }

}
