package ch.imedias.rsccfx.model.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
  public void testEqualsHashCode() {
    String description = "Test Description";
    String address = "Test Address";
    String port = "65535";
    boolean encrypted = true;
    boolean chargeable = true;

    // both supporters have the same attributes, so equals and hashcode should also be the same
    Supporter supporter1 = new Supporter(description, address, port, encrypted, chargeable);
    Supporter supporter2 = new Supporter(description, address, port, encrypted, chargeable);
    assertTrue(supporter1.equals(supporter2) && supporter2.equals(supporter1));
    assertTrue(supporter1.hashCode() == supporter2.hashCode());
  }

}
