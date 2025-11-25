import java.util.Random;
import student.TestCase;

/**
 * Unit tests for {@link AirObjectSkipList}.
 */
public class AirObjectSkipListTest extends TestCase {
    private AirObjectSkipList list;

    /**
     * Creates a deterministic skip list for each test.
     */
    public void setUp() {
        list = new AirObjectSkipList(new Random(0xBEEF));
    }


    /**
     * Helper for building simple balloon objects.
     *
     * @param name
     *            object name
     * @return balloon
     */
    private Balloon balloon(String name) {
        return new Balloon(name, 1, 2, 3, 4, 5, 6, "hot", 10);
    }


    /**
     * Deterministic random generator for forcing skip list levels.
     */
    private static class FixedRandom extends Random {
        private static final long serialVersionUID = 1L;
        private final int[] values;
        private int index;

        FixedRandom(int... sequence) {
            values = sequence.clone();
            index = 0;
        }


        @Override
        public int nextInt() {
            int slot = index < values.length ? index : values.length - 1;
            index++;
            return values[slot];
        }


        @Override
        public boolean nextBoolean() {
            return (nextInt() & 1) == 0;
        }
    }


    /**
     * Search should find objects that exist in the skip list.
     */
    public void testSearchFindsExistingObject() {
        Balloon first = balloon("Alpha");
        Balloon second = balloon("Beta");
        assertTrue(list.insert(first));
        assertTrue(list.insert(second));
        assertSame(first, list.search("Alpha"));
        assertSame(second, list.search("Beta"));
        assertNull(list.search("Missing"));
    }


    /**
     * Duplicate names must be rejected.
     */
    public void testInsertRejectsDuplicateNames() {
        Balloon obj = balloon("Dup");
        assertTrue(list.insert(obj));
        assertFalse(list.insert(balloon("Dup")));
        assertEquals(1, list.size());
        assertFalse(list.insert(null));
        assertFalse(list.insert(new Balloon(null, 1, 1, 1, 1, 1, 1, "type", 5)));
    }


    /**
     * Removing a missing name should return null and leave the size unchanged.
     */
    public void testRemoveMissingName() {
        list.insert(balloon("Keep"));
        list.insert(balloon("Remove"));
        assertNotNull(list.remove("Remove"));
        assertNull(list.remove("Remove"));
        assertEquals(1, list.size());
        assertNull(list.remove(null));
    }


    /**
     * Clearing the list should remove all nodes and reset the reporting depth.
     */
    public void testClearResetsStructure() {
        list.insert(balloon("One"));
        list.insert(balloon("Two"));
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals("SkipList is empty", list.formatStructure());
        assertNull(list.search("One"));
    }


    /**
     * isEmpty should switch to false once data is inserted and back to true
     * after clearing.
     */
    public void testIsEmptyTracksInsertions() {
        assertTrue(list.isEmpty());
        list.insert(balloon("Solo"));
        assertFalse(list.isEmpty());
        list.clear();
        assertTrue(list.isEmpty());
    }


    /**
     * traverseRange should ignore calls with null boundaries.
     */
    public void testTraverseRangeIgnoresNullParameters() {
        list.insert(balloon("Alpha"));
        final int[] visits = {0};
        list.traverseRange(null, "z", new AirObjectSkipList.Visitor() {
            @Override
            public void visit(AirObject obj) {
                visits[0]++;
            }
        });
        list.traverseRange("a", null, new AirObjectSkipList.Visitor() {
            @Override
            public void visit(AirObject obj) {
                visits[0]++;
            }
        });
        assertEquals(0, visits[0]);
    }


    /**
     * traverseRange should include both boundary keys and return items in order.
     */
    public void testTraverseRangeInclusiveBounds() {
        list.insert(balloon("Alpha"));
        list.insert(balloon("Beta"));
        list.insert(balloon("Gamma"));
        final StringBuilder builder = new StringBuilder();
        list.traverseRange("Alpha", "Beta", new AirObjectSkipList.Visitor() {
            @Override
            public void visit(AirObject obj) {
                if (builder.length() > 0) {
                    builder.append(",");
                }
                builder.append(obj.getName());
            }
        });
        assertEquals("Alpha,Beta", builder.toString());
    }


    /**
     * traverseRange should return immediately if start is greater than end.
     */
    public void testTraverseRangeWithInvalidBounds() {
        list.insert(balloon("Alpha"));
        final int[] visits = {0};
        list.traverseRange("z", "a", new AirObjectSkipList.Visitor() {
            @Override
            public void visit(AirObject obj) {
                visits[0]++;
            }
        });
        assertEquals(0, visits[0]);
    }


    /**
     * Removing a multi-level node must update every level so that search no
     * longer locates the deleted entry.
     */
    public void testRemoveMultiLevelNodeFullyDeletes() {
        AirObjectSkipList tallList = new AirObjectSkipList(
            new FixedRandom(0, 0, 1, 1, 1));
        Balloon high = balloon("High");
        Balloon low = balloon("Low");
        assertTrue(tallList.insert(high));
        assertTrue(tallList.insert(low));
        assertNotNull(tallList.remove("High"));
        assertNull(tallList.search("High"));
        assertEquals(1, tallList.size());
        assertFalse(tallList.formatStructure().contains("High"));
    }


    /**
     * Removing a non-existent key should leave the structure unchanged.
     */
    public void testRemoveMissingKeyLeavesStructure() {
        list.insert(balloon("Alpha"));
        list.insert(balloon("Charlie"));
        assertNull(list.remove("Bravo"));
        assertEquals(2, list.size());
        assertNotNull(list.search("Alpha"));
        assertNotNull(list.search("Charlie"));
    }


    /**
     * Searching for null should return null without walking any levels.
     */
    public void testSearchNullReturnsNull() {
        list.insert(balloon("Alpha"));
        assertNull(list.search(null));
    }


    /**
     * FixedRandom should keep returning the last provided value once the input
     * sequence is exhausted.
     */
    public void testFixedRandomRepeatsLastValue() {
        FixedRandom rnd = new FixedRandom(0, 2);
        assertEquals(0, rnd.nextInt());
        assertEquals(2, rnd.nextInt());
        assertEquals(2, rnd.nextInt());
        assertEquals(2, rnd.nextInt());
    }


    /**
     * Removing the highest level node should allow the skip list to shrink its
     * current level back to zero.
     */
    public void testRemoveShrinksLevel() {
        FixedRandom rnd = new FixedRandom(0, 0, 0, 1, 1, 1, 1);
        AirObjectSkipList tallList = new AirObjectSkipList(rnd);
        Balloon high = balloon("High");
        Balloon low = balloon("Low");
        assertTrue(tallList.insert(high));
        assertTrue(tallList.insert(low));
        assertTrue(tallList.currentLevel() >= 1);
        assertNotNull(tallList.remove("High"));
        assertEquals(0, tallList.currentLevel());
    }
}

