import student.TestCase;

/**
 * Tests for the {@link Bintree#splitRegionForTest(BoundingBox, int)} helper.
 */
public class BintreeTest extends TestCase {
    private BoundingBox world;


    /**
     * Common setup for tests that need a world-sized region.
     */
    public void setUp() {
        world = new BoundingBox(0, 0, 0, 64, 64, 64);
    }


    /**
     * Splitting along the x-axis should divide the width into half/remainder.
     */
    public void testSplitRegionXAxisOddWidth() {
        BoundingBox parent = new BoundingBox(0, 0, 0, 5, 8, 12);
        BoundingBox[] children = Bintree.splitRegionForTest(parent, 0);
        assertEquals(2, children[0].getXWidth());
        assertEquals(3, children[1].getXWidth());
        assertEquals(0, children[0].getX());
        assertEquals(2, children[1].getX());
        assertEquals(parent.getYWidth(), children[0].getYWidth());
        assertEquals(parent.getZWidth(), children[1].getZWidth());
    }


    /**
     * Splitting along the y-axis should shift the y coordinate for the upper
     * child.
     */
    public void testSplitRegionYAxisEvenWidth() {
        BoundingBox parent = new BoundingBox(5, 5, 5, 10, 6, 7);
        BoundingBox[] children = Bintree.splitRegionForTest(parent, 1);
        assertEquals(3, children[0].getYWidth());
        assertEquals(3, children[1].getYWidth());
        assertEquals(5, children[0].getY());
        assertEquals(8, children[1].getY());
        assertEquals(parent.getXWidth(), children[0].getXWidth());
        assertEquals(parent.getZWidth(), children[1].getZWidth());
    }


    /**
     * Splitting along the z-axis with minimal depth still needs to allocate one
     * cell to the first child.
     */
    public void testSplitRegionZAxisSingleUnit() {
        BoundingBox parent = new BoundingBox(2, 2, 2, 4, 4, 1);
        BoundingBox[] children = Bintree.splitRegionForTest(parent, 2);
        assertEquals(1, children[0].getZWidth());
        assertEquals(0, children[1].getZWidth());
        assertEquals(2, children[0].getZ());
        assertEquals(3, children[1].getZ());
    }


    /**
     * Splitting along the y-axis when the width is one should still allocate a
     * single unit to the first child and zero to the second.
     */
    public void testSplitRegionYAxisSingleUnit() {
        BoundingBox parent = new BoundingBox(0, 10, 0, 6, 1, 3);
        BoundingBox[] children = Bintree.splitRegionForTest(parent, 1);
        assertEquals(1, children[0].getYWidth());
        assertEquals(0, children[1].getYWidth());
        assertEquals(10, children[0].getY());
        assertEquals(11, children[1].getY());
    }


    /**
     * Splitting along the y-axis with an odd width should produce asymmetric
     * children widths.
     */
    public void testSplitRegionYAxisOddWidth() {
        BoundingBox parent = new BoundingBox(0, 0, 0, 4, 5, 4);
        BoundingBox[] children = Bintree.splitRegionForTest(parent, 1);
        assertEquals(2, children[0].getYWidth());
        assertEquals(3, children[1].getYWidth());
        assertEquals(0, children[0].getY());
        assertEquals(2, children[1].getY());
    }


    /**
     * Inserting the same object twice should not duplicate entries.
     */
    public void testDuplicateInsertIgnored() {
        Bintree tree = new Bintree(world);
        Balloon repeat = balloon("Repeat");
        tree.insert(repeat);
        tree.insert(repeat);
        assertEquals(1, countOccurrences(tree.print(), "Repeat"));
    }


    /**
     * Removing a present object should succeed and the object should disappear
     * from traversal output.
     */
    public void testRemoveExistingObject() {
        Bintree tree = new Bintree(world);
        Balloon a = balloon("A");
        Balloon b = balloon("B");
        Balloon c = balloon("C");
        Balloon d = balloon("D");
        Balloon e = balloon("E");
        Balloon[] all = {a, b, c, d, e};
        for (Balloon balloon : all) {
            tree.insert(balloon);
        }
        assertTrue(tree.remove(c));
        assertFalse(tree.remove(c));
        assertEquals(0, countOccurrences(tree.print(), "C"));
    }


    /**
     * Removing an object that was never inserted should return false.
     */
    public void testRemoveMissingObject() {
        Bintree tree = new Bintree(world);
        assertFalse(tree.remove(balloon("ghost")));
    }


    /**
     * Storage backing a leaf should grow beyond its initial capacity and keep
     * the objects sorted lexicographically.
     */
    public void testLeafStorageExpandsCapacityAndSorts() {
        Bintree tiny = new Bintree(new BoundingBox(0, 0, 0, 1, 1, 1));
        String[] names = {"Zulu", "Yankee", "Xray", "Whiskey", "Victor",
            "Uniform"};
        for (String name : names) {
            tiny.insert(narrowBalloon(name));
        }
        String listing = tiny.print();
        assertTrue(listing.contains("Leaf with 6 objects (0, 0, 0, 1, 1, 1) 0"));
        String[] expectedOrder = {"Uniform", "Victor", "Whiskey", "Xray",
            "Yankee", "Zulu"};
        for (int i = 0; i < expectedOrder.length - 1; i++) {
            assertTrue(indexOfName(listing, expectedOrder[i])
                < indexOfName(listing, expectedOrder[i + 1]));
        }
    }


    /**
     * Objects whose names are null must sort before named objects to satisfy
     * the AirObjectStorage comparator.
     */
    public void testNullNamedObjectsSortFirst() {
        Bintree tiny = new Bintree(new BoundingBox(0, 0, 0, 1, 1, 1));
        tiny.insert(narrowBalloon("Alpha"));
        tiny.insert(narrowBalloon(null));
        tiny.insert(narrowBalloon("Bravo"));
        String listing = tiny.print();
        int nullIndex = listing.indexOf("Balloon null");
        assertTrue(nullIndex >= 0);
        assertTrue(nullIndex < indexOfName(listing, "Alpha"));
        assertTrue(nullIndex < indexOfName(listing, "Bravo"));
    }


    /**
     * The AirObjectStorage comparator must handle combinations of null
     * references and null names without throwing and in the correct order.
     *
     * @throws Exception
     *             when reflection fails to access the private helper
     */
    public void testAirObjectStorageCompareNullBranches() throws Exception {
        Balloon alpha = balloon("Alpha");
        Balloon bravo = balloon("Bravo");
        Balloon nullNamed = balloon(null);
        Balloon nullNamed2 = balloon(null);

        assertEquals(0, Bintree.compareAirObjectsForTest(null, null));
        assertTrue(Bintree.compareAirObjectsForTest(null, alpha) < 0);
        assertTrue(Bintree.compareAirObjectsForTest(alpha, null) > 0);
        assertEquals(0,
            Bintree.compareAirObjectsForTest(nullNamed, nullNamed2));
        assertTrue(Bintree.compareAirObjectsForTest(nullNamed, alpha) < 0);
        assertTrue(Bintree.compareAirObjectsForTest(alpha, nullNamed) > 0);
        assertTrue(Bintree.compareAirObjectsForTest(alpha, bravo) < 0);
        assertTrue(Bintree.compareAirObjectsForTest(bravo, alpha) > 0);
    }


    /**
     * Inserting four non-overlapping objects should force the leaf to split
     * into an internal node since the threshold is exceeded and the regions
     * can be divided.
     */
    public void testInsertTriggersLeafSplit() {
        Bintree tree = new Bintree(world);
        tree.insert(balloonAt("A", 1, 1, 1));
        tree.insert(balloonAt("B", 33, 1, 1));
        tree.insert(balloonAt("C", 1, 33, 1));
        tree.insert(balloonAt("D", 33, 33, 1));
        String listing = tree.print();
        assertTrue(listing.startsWith("I (0, 0, 0, 64, 64, 64) 0"));
        assertTrue(countOccurrences(listing, "Leaf with") >= 2);
    }


    /**
     * Even when more than three objects occupy the same sub-region, the leaf
     * should avoid splitting because every bounding box overlaps.
     */
    public void testOverlappingObjectsDoNotSplitLeaf() {
        Bintree tree = new Bintree(world);
        for (int i = 0; i < 4; i++) {
            tree.insert(balloonAt("Overlap" + i, 5, 5, 5));
        }
        String listing = tree.print();
        assertTrue(listing.startsWith("Leaf with 4 objects (0, 0, 0, 64, 64, 64) 0"));
    }


    private Balloon balloon(String name) {
        return new Balloon(name, 1, 1, 1, 2, 2, 2, "hot_air", 5);
    }


    private Balloon narrowBalloon(String name) {
        return new Balloon(name, 0, 0, 0, 1, 1, 1, "hot_air", 5);
    }


    private Balloon balloonAt(String name, int x, int y, int z) {
        return new Balloon(name, x, y, z, 4, 4, 4, "hot_air", 5);
    }


    private int countOccurrences(String haystack, String needle) {
        int count = 0;
        int index = haystack.indexOf(needle);
        while (index >= 0) {
            count++;
            index = haystack.indexOf(needle, index + needle.length());
        }
        return count;
    }


    private int indexOfName(String listing, String name) {
        if (name == null) {
            return listing.indexOf("Balloon null");
        }
        return listing.indexOf(name);
    }

}

