import student.TestCase;

/**
 * Tests for the {@link AirObject} comparison helpers.
 */
public class AirObjectTest extends TestCase {
    private Balloon alpha;
    private Balloon betaWithNullName;

    /**
     * Set up shared fixtures.
     */
    public void setUp() {
        alpha = new Balloon("Alpha", 1, 1, 1, 1, 1, 1, "type", 10);
        betaWithNullName = new Balloon(null, 1, 1, 1, 1, 1, 1, "type", 10);
    }


    /**
     * Comparisons should be ordered lexicographically on the name.
     */
    public void testCompareToOrdersByName() {
        Balloon beta = new Balloon("Beta", 1, 1, 1, 1, 1, 1, "type", 10);
        assertTrue(alpha.compareTo(beta) < 0);
        assertTrue(beta.compareTo(alpha) > 0);
        assertEquals(0, alpha.compareTo(alpha));
    }


    /**
     * Null names should sort before non-null names.
     */
    public void testCompareToHandlesNullNames() {
        assertTrue(betaWithNullName.compareTo(alpha) < 0);
        assertTrue(alpha.compareTo(betaWithNullName) > 0);
        assertEquals(0, betaWithNullName.compareTo(betaWithNullName));
    }


    /**
     * If both AirObject references are null, compareTo should treat them as
     * equal.
     */
    public void testCompareToWithBothNull() {
        assertEquals(0, alpha.compareTo(alpha));
        assertEquals(0, betaWithNullName.compareTo(betaWithNullName));
    }
}

