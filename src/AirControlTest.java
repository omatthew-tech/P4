import java.util.Random;
import student.TestCase;

/**
 * @author {Your Name Here}
 * @version {Put Something Here}
 */
public class AirControlTest extends TestCase {

    /**
     * Sets up the tests that follow. In general, used for initialization
     */
    public void setUp() {
        // Nothing here
    }


    /**
     * Get code coverage of the class declaration.
     *
     * @throws Exception
     */
    public void testRInit() throws Exception {
        AirControl recstore = new AirControl();
        assertNotNull(recstore);
    }


    // ----------------------------------------------------------
    /**
     * Test syntax: Sample Input/Output
     *
     * @throws Exception
     */
    public void testSampleInput() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0xCAFEBEEF);
        WorldDB w = new WorldDB(rnd);

        assertTrue(w.add(new Balloon("B1",
            10, 11, 11, 21, 12, 31, "hot_air", 15)));
        assertTrue(w.add(new AirPlane("Air1",
            0, 10, 1, 20, 2, 30, "USAir", 717, 4)));
        assertTrue(w.add(new Drone("Air2",
            100, 1010, 101, 924, 2, 900, "Droners", 3)));
        assertTrue(w.add(new Bird("pterodactyl",
            0, 100, 20, 10, 50, 50, "Dinosaur", 1)));
        assertFalse(w.add(new Bird("pterodactyl",
            0, 100, 20, 10, 50, 50, "Dinosaur", 1)));
        assertTrue(w.add(new Rocket("Enterprise",
            0, 100, 20, 10, 50, 50, 5000, 99.29)));

        assertFuzzyEquals(
            "Rocket Enterprise 0 100 20 10 50 50 5000 99.29",
            w.delete("Enterprise"));

        assertFuzzyEquals("Airplane Air1 0 10 1 20 2 30 USAir 717 4",
            w.print("Air1"));
        assertNull(w.print("air1"));

        assertFuzzyEquals(
            "I (0, 0, 0, 1024, 1024, 1024) 0\r\n"
                + "  I (0, 0, 0, 512, 1024, 1024) 1\r\n"
                + "    Leaf with 3 objects (0, 0, 0, 512, 512, 1024) 2\r\n"
                + "    (Airplane Air1 0 10 1 20 2 30 USAir 717 4)\r\n"
                + "    (Balloon B1 10 11 11 21 12 31 hot_air 15)\r\n"
                + "    (Bird pterodactyl 0 100 20 10 50 50 Dinosaur 1)\r\n"
                + "    Leaf with 1 objects (0, 512, 0, 512, 512, 1024) 2\r\n"
                + "    (Drone Air2 100 1010 101 924 2 900 Droners 3)\r\n"
                + "  Leaf with 1 objects (512, 0, 0, 512, 1024, 1024) 1\r\n"
                + "  (Drone Air2 100 1010 101 924 2 900 Droners 3)\r\n"
                + "5 Bintree nodes printed\r\n",
                w.printbintree());

        assertFuzzyEquals(
            "Node has depth 3, Value (null)\r\n"
                + "Node has depth 3, "
                + "Value (Airplane Air1 0 10 1 20 2 30 USAir 717 4)\r\n"
                + "Node has depth 1, "
                + "Value (Drone Air2 100 1010 101 924 2 900 Droners 3)\r\n"
                + "Node has depth 2, "
                + "Value (Balloon B1 10 11 11 21 12 31 hot_air 15)\r\n"
                + "Node has depth 2, "
                + "Value (Bird pterodactyl 0 100 20 10 50 50 Dinosaur 1)\r\n"
                + "4 skiplist nodes printed\r\n",
                w.printskiplist());

        assertFuzzyEquals(
            "Found these records in the range a to z\r\n"
                + "Bird pterodactyl 0 100 20 10 50 50 Dinosaur 1\r\n",
                w.rangeprint("a", "z"));
        assertFuzzyEquals(
            "Found these records in the range a to l\r\n",
            w.rangeprint("a", "l"));
        assertNull(w.rangeprint("z", "a"));

        assertFuzzyEquals(
            "The following collisions exist in the database:\r\n"
                + "In leaf node (0, 0, 0, 512, 512, 1024) 2\r\n"
                + "(Airplane Air1 0 10 1 20 2 30 USAir 717 4) "
                + "and (Balloon B1 10 11 11 21 12 31 hot_air 15)\r\n"
                + "In leaf node (0, 512, 0, 512, 512, 1024) 2\r\n"
                + "In leaf node (512, 0, 0, 512, 1024, 1024) 1\r\n",
                w.collisions());

        assertFuzzyEquals(
            "The following objects intersect (0 0 0 1024 1024 1024):\r\n"
                + "In Internal node (0, 0, 0, 1024, 1024, 1024) 0\r\n"
                + "In Internal node (0, 0, 0, 512, 1024, 1024) 1\r\n"
                + "In leaf node (0, 0, 0, 512, 512, 1024) 2\r\n"
                + "Airplane Air1 0 10 1 20 2 30 USAir 717 4\r\n"
                + "Balloon B1 10 11 11 21 12 31 hot_air 15\r\n"
                + "Bird pterodactyl 0 100 20 10 50 50 Dinosaur 1\r\n"
                + "In leaf node (0, 512, 0, 512, 512, 1024) 2\r\n"
                + "Drone Air2 100 1010 101 924 2 900 Droners 3\r\n"
                + "In leaf node (512, 0, 0, 512, 1024, 1024) 1\r\n"
                + "5 nodes were visited in the bintree\r\n",
                w.intersect(0, 0, 0, 1024, 1024, 1024));
    }


    // ----------------------------------------------------------
    /**
     * Verify that deletions remove the record from both structures.
     *
     * @throws Exception
     */
    public void testDeleteRemovesRecord() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0xBEEFCAFE);
        WorldDB world = new WorldDB(rnd);

        AirPlane plane = new AirPlane("Alpha",
            50, 50, 50, 10, 10, 10, "Carrier", 123, 2);
        Balloon balloon = new Balloon("Bravo",
            100, 100, 100, 10, 10, 10, "hot_air", 5);

        assertTrue(world.add(plane));
        assertTrue(world.add(balloon));

        assertFuzzyEquals("Airplane Alpha 50 50 50 10 10 10 Carrier 123 2",
            world.delete("Alpha"));
        assertNull(world.print("Alpha"));
        assertFalse(world.printskiplist().contains("Alpha"));
        assertFalse(world.printbintree().contains("Alpha"));
        assertTrue(world.printskiplist().contains("Bravo"));
    }


    // ----------------------------------------------------------
    /**
     * Range printing should include both bounds when objects exist.
     *
     * @throws Exception
     */
    public void testRangePrintInclusiveBounds() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0xFEEDFACE);
        WorldDB world = new WorldDB(rnd);

        assertTrue(world.add(new AirPlane("Alpha",
            10, 10, 10, 5, 5, 5, "Carrier", 7, 2)));
        assertTrue(world.add(new Balloon("Bravo",
            20, 20, 20, 5, 5, 5, "hot_air", 4)));
        assertTrue(world.add(new Bird("Charlie",
            30, 30, 30, 5, 5, 5, "sparrow", 3)));

        assertFuzzyEquals(
            "Found these records in the range Alpha to Bravo\r\n"
                + "Airplane Alpha 10 10 10 5 5 5 Carrier 7 2\r\n"
                + "Balloon Bravo 20 20 20 5 5 5 hot_air 4\r\n",
            world.rangeprint("Alpha", "Bravo"));
    }


    // ----------------------------------------------------------
    /**
     * Clearing the database should empty both underlying structures.
     *
     * @throws Exception
     */
    public void testClearResetsWorldState() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0x1234);
        WorldDB world = new WorldDB(rnd);

        assertTrue(world.add(new AirPlane("Alpha",
            10, 10, 10, 5, 5, 5, "Carrier", 10, 2)));
        assertTrue(world.add(new Balloon("Bravo",
            20, 20, 20, 5, 5, 5, "hot_air", 4)));
        assertTrue(world.printskiplist().contains("Alpha"));

        world.clear();

        assertFuzzyEquals("SkipList is empty", world.printskiplist());
        assertFuzzyEquals(
            "E (0, 0, 0, 1024, 1024, 1024) 0\r\n"
                + "1 Bintree nodes printed\r\n",
            world.printbintree());
    }


    // ----------------------------------------------------------
    /**
     * Deleting a missing record should leave existing data untouched.
     *
     * @throws Exception
     */
    public void testDeleteMissingRecord() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0x777);
        WorldDB world = new WorldDB(rnd);

        AirPlane plane = new AirPlane("Alpha",
            40, 40, 40, 10, 10, 10, "Carrier", 22, 2);
        assertTrue(world.add(plane));

        assertNull(world.delete("Missing"));
        assertFuzzyEquals(plane.toString(), world.print("Alpha"));
        assertTrue(world.printskiplist().contains("Alpha"));
    }


    // ----------------------------------------------------------
    /**
     * Intersect queries should only list matching objects.
     *
     * @throws Exception
     */
    public void testIntersectFiltersObjects() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0x888);
        WorldDB world = new WorldDB(rnd);

        AirPlane plane = new AirPlane("Alpha",
            100, 100, 100, 20, 20, 20, "Carrier", 33, 2);
        Balloon balloon = new Balloon("Bravo",
            400, 400, 400, 15, 15, 15, "hot_air", 5);
        assertTrue(world.add(plane));
        assertTrue(world.add(balloon));

        String output = world.intersect(90, 90, 90, 60, 60, 60);
        assertTrue(output.contains(plane.toString()));
        assertFalse(output.contains(balloon.toString()));
    }


    /**
     * Intersect queries that extend beyond the world bounds should be rejected.
     */
    public void testIntersectRejectsQueriesBeyondWorld() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0x123);
        WorldDB world = new WorldDB(rnd);

        AirPlane plane = new AirPlane("Edge",
            900, 900, 900, 50, 50, 50, "Carrier", 1, 1);
        assertTrue(world.add(plane));

        assertNull(world.intersect(900, 900, 900, 500, 500, 500));
    }


    // ----------------------------------------------------------
    /**
     * Collisions report should include overlapping objects that occupy
     * the same node.
     *
     * @throws Exception
     */
    public void testCollisionsReportOverlaps() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0x999);
        WorldDB world = new WorldDB(rnd);

        AirPlane plane = new AirPlane("Alpha",
            10, 10, 10, 10, 10, 10, "Carrier", 44, 2);
        Balloon balloon = new Balloon("Bravo",
            12, 12, 12, 10, 10, 10, "hot_air", 6);
        assertTrue(world.add(plane));
        assertTrue(world.add(balloon));

        String collisions = world.collisions();
        assertTrue(collisions.contains(plane.toString()));
        assertTrue(collisions.contains(balloon.toString()));
        assertTrue(collisions.contains("In leaf node"));
    }


    // ----------------------------------------------------------
    /**
     * Test syntax: Check various forms of bad input parameters
     *
     * @throws Exception
     */
    public void testBadInput() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0xCAFEBEEF);
        WorldDB w = new WorldDB(rnd);
        assertFalse(w.add(new AirPlane("a", 1, 1, 1, 1, 1, 1, null, 1, 1)));
        assertFalse(w.add(new AirPlane("a", 1, 1, 1, 1, 1, 1, "Alaska", 0, 1)));
        assertFalse(w.add(new AirPlane("a", 1, 1, 1, 1, 1, 1, "Alaska", 1, 0)));
        assertFalse(w.add(new Balloon(null, 1, 1, 1, 1, 1, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", -1, 1, 1, 1, 1, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, -1, 1, 1, 1, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, -1, 1, 1, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, 1, 0, 1, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, 1, 1, 0, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, 1, 1, 1, 0, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, 1, 1, 1, 1, null, 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, 1, 1, 1, 1, "hot", -1)));
        assertFalse(w.add(new Bird("b", 1, 1, 1, 1, 1, 1, null, 5)));
        assertFalse(w.add(new Bird("b", 1, 1, 1, 1, 1, 1, "Ostrich", 0)));
        assertFalse(w.add(new Drone("d", 1, 1, 1, 1, 1, 1, null, 5)));
        assertFalse(w.add(new Drone("d", 1, 1, 1, 1, 1, 1, "Droner", 0)));
        assertFalse(w.add(new Rocket("r", 1, 1, 1, 1, 1, 1, -1, 1.1)));
        assertFalse(w.add(new Rocket("r", 1, 1, 1, 1, 1, 1, 1, -1.1)));
        assertFalse(w.add(
            new AirPlane("a", 2000, 1, 1, 1, 1, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 2000, 1, 1, 1, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1, 2000, 1, 1, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1, 1, 2000, 1, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1, 1, 1, 2000, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1, 1, 1, 1, 2000, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1000, 1, 1, 1000, 1, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1000, 1, 1, 1000, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1, 1000, 1, 1, 1000, "Alaska", 1, 1)));
        assertNull(w.delete(null));
        assertNull(w.print(null));
        assertNull(w.rangeprint(null, "a"));
        assertNull(w.rangeprint("a", null));
        assertNull(w.intersect(-1, 1, 1, 1, 1, 1));
        assertNull(w.intersect(1, -1, 1, 1, 1, 1));
        assertNull(w.intersect(1, 1, -1, 1, 1, 1));
        assertNull(w.intersect(1, 1, 1, -1, 1, 1));
        assertNull(w.intersect(1, 1, 1, 1, -1, 1));
        assertNull(w.intersect(1, 1, 1, 1, 1, -1));
        assertNull(w.intersect(2000, 1, 1, 1, 1, 1));
        assertNull(w.intersect(1, 2000, 1, 1, 1, 1));
        assertNull(w.intersect(1, 1, 2000, 1, 1, 1));
        assertNull(w.intersect(1, 1, 1, 2000, 1, 1));
        assertNull(w.intersect(1, 1, 1, 1, 2000, 1));
        assertNull(w.intersect(1, 1, 1, 1, 1, 2000));
        assertNull(w.intersect(1000, 1, 1, 1000, 1, 1));
        assertNull(w.intersect(1, 1000, 1, 1, 1000, 1));
        assertNull(w.intersect(1, 1, 1000, 1, 1, 1000));
    }


    // ----------------------------------------------------------
    /**
     * Test empty: Check various returns from commands on empty database
     *
     * @throws Exception
     */
    public void testEmpty() throws Exception {
        WorldDB w = new WorldDB(null);
        assertNull(w.delete("hello"));
        assertFuzzyEquals("SkipList is empty", w.printskiplist());
        assertFuzzyEquals(
            "E (0, 0, 0, 1024, 1024, 1024) 0\r\n"
                + "1 Bintree nodes printed\r\n",
                w.printbintree());
        assertNull(w.print("hello"));
        assertFuzzyEquals("Found these records in the range begin to end\n",
            w.rangeprint("begin", "end"));
        assertFuzzyEquals("The following collisions exist in the database:\n",
            w.collisions());
        assertFuzzyEquals(
            "The following objects intersect (1, 1, 1, 1, 1, 1)\n" +
                "1 nodes were visited in the bintree\n",
                w.intersect(1, 1, 1, 1, 1, 1));
    }
}