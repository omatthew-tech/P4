import java.util.Random;

/**
 * The world for this project. We have a Skip List and a Bintree
 *
 * @author {Your Name Here}
 * @version {Put Something Here}
 */
public class WorldDB implements ATC {
    private static final int WORLD_SIZE = 1024;
    private final Random rnd;
    private final AirObjectSkipList skiplist;
    private final Bintree bintree;

    /**
     * Create a brave new World.
     * @param r A random number generator to use
     *
     */
    public WorldDB(Random r) {
        rnd = r == null ? new Random() : r;
        skiplist = new AirObjectSkipList(rnd);
        bintree = new Bintree(
            new BoundingBox(0, 0, 0, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE));
        clear();
    }


    /**
     * Clear the world
     *
     */
    public void clear() {
        skiplist.clear();
        bintree.clear();
    }


    // ----------------------------------------------------------
    /**
     * (Try to) insert an AirObject into the database
     * @param a An AirObject.
     * @return True iff the AirObject is successfully entered into the database
     */
    public boolean add(AirObject a) {
        if (!isValidAirObject(a)) {
            return false;
        }
        if (!skiplist.insert(a)) {
            return false;
        }
        bintree.insert(a);
        return true;
    }


    // ----------------------------------------------------------
    /**
     * The AirObject with this name is deleted from the database (if it exists).
     * Print the AirObject's toString value if one with that name exists.
     * If no such AirObject with this name exists, return null.
     * @param name AirObject name.
     * @return A string representing the AirObject, or null if no such name.
     */
    public String delete(String name) {
        if (!isValidName(name)) {
            return null;
        }
        AirObject removed = skiplist.remove(name);
        if (removed == null) {
            return null;
        }
        bintree.remove(removed);
        return removed.toString();
    }


    // ----------------------------------------------------------
    /**
     * Return a listing of the Skiplist in alphabetical order on the names.
     * See the sample test cases for details on format.
     * @return String listing the AirObjects in the Skiplist as specified.
     */
    public String printskiplist() {
        return skiplist.formatStructure();
    }


    // ----------------------------------------------------------
    /**
     * Return a listing of the Bintree nodes in preorder.
     * See the sample test cases for details on format.
     * @return String listing the Bintree nodes as specified.
     */
    public String printbintree() {
        return bintree.print();
    }



    // ----------------------------------------------------------
    /**
     * Print an AirObject with a given name if it exists
     * @param name The name of the AirObject to print
     * @return String showing the toString for the AirObject if it exists
     *         Return null if there is no such name
     */
    public String print(String name) {
        if (!isValidName(name)) {
            return null;
        }
        AirObject obj = skiplist.search(name);
        return obj == null ? null : obj.toString();
    }


    // ----------------------------------------------------------
    /**
     * Return a listing of the AirObjects found in the database between the
     * min and max values for names.
     * See the sample test cases for details on format.
     * @param start Minimum of range
     * @param end Maximum of range
     * @return String listing the AirObjects in the range as specified.
     *         Null if the parameters are bad
     */
    public String rangeprint(String start, String end) {
        if (!isValidName(start) || !isValidName(end)) {
            return null;
        }
        if (start.compareTo(end) > 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Found these records in the range ").append(start)
            .append(" to ").append(end).append("\r\n");
        skiplist.traverseRange(start, end, new AirObjectSkipList.Visitor() {
            @Override
            public void visit(AirObject obj) {
                builder.append(obj.toString()).append("\r\n");
            }
        });
        return builder.toString();
    }


    // ----------------------------------------------------------
    /**
     * Return a listing of all collisions between AirObjects bounding boxes
     * that are found in the database.
     * See the sample test cases for details on format.
     * Note that the collision is only reported for the node that contains the
     * origin of the intersection box.
     * @return String listing the AirObjects that participate in collisions.
     */
    public String collisions() {
        return bintree.collisionsReport();
    }


    // ----------------------------------------------------------
    /**
     * Return a listing of all AirObjects whose bounding boxes
     * that intersect the given bounding box.
     * Note that the collision is only reported for the node that contains the
     * origin of the intersection box.
     * See the sample test cases for details on format.
     * @param x Bounding box upper left x
     * @param y Bounding box upper left y
     * @param z Bounding box upper left z
     * @param xwid Bounding box x width
     * @param ywid Bounding box y width
     * @param zwid Bounding box z width
     * @return String listing the AirObjects that intersect the given box.
     *         Return null if any input parameters are bad
     */
    public String intersect(int x, int y, int z, int xwid, int ywid, int zwid) {
        if (!isValidDimension(x, xwid) || !isValidDimension(y, ywid)
            || !isValidDimension(z, zwid)) {
            return null;
        }
        BoundingBox box = new BoundingBox(x, y, z, xwid, ywid, zwid);
        return bintree.intersectReport(box);
    }


    private boolean isValidAirObject(AirObject obj) {
        if (obj == null || !isValidName(obj.getName())) {
            return false;
        }
        if (!isValidDimension(obj.getXorig(), obj.getXwidth())
            || !isValidDimension(obj.getYorig(), obj.getYwidth())
            || !isValidDimension(obj.getZorig(), obj.getZwidth())) {
            return false;
        }
        if (obj instanceof AirPlane) {
            AirPlane plane = (AirPlane)obj;
            return isValidName(plane.getCarrier())
                && plane.getFlightNumber() > 0 && plane.getEngineCount() > 0;
        }
        if (obj instanceof Balloon) {
            Balloon balloon = (Balloon)obj;
            return isValidName(balloon.getType())
                && balloon.getAscentRate() > 0;
        }
        if (obj instanceof Bird) {
            Bird bird = (Bird)obj;
            return isValidName(bird.getSpecies()) && bird.getNumber() > 0;
        }
        if (obj instanceof Drone) {
            Drone drone = (Drone)obj;
            return isValidName(drone.getBrand())
                && drone.getEngineCount() > 0;
        }
        if (obj instanceof Rocket) {
            Rocket rocket = (Rocket)obj;
            return rocket.getAscentRate() > 0 && rocket.getTrajectory() >= 0;
        }
        return true;
    }


    private boolean isValidName(String value) {
        return value != null && value.trim().length() > 0;
    }


    private boolean isValidDimension(int origin, int width) {
        if (origin < 0 || width <= 0 || width > WORLD_SIZE) {
            return false;
        }
        return origin + width <= WORLD_SIZE;
    }
}
