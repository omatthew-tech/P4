import student.TestCase;

/**
 * Focused tests that exercise the validation helpers inside {@link WorldDB}.
 *
 * @author Matthew Ozoroski (omatthew-tech)
 * @version 2025-11-26
 */
public class WorldDBValidationTest extends TestCase {
    private WorldDB world;

    /**
     * Fresh world before each test.
     */
    public void setUp() {
        world = new WorldDB(null);
    }


    /**
     * Airplanes must provide a carrier and positive flight/engine values.
     */
    public void testAirPlaneValidation() {
        assertFalse(world.add(new AirPlane("Plane", 10, 10, 10, 5, 5, 5, null,
            1, 1)));
        assertFalse(world.add(new AirPlane("Plane", 10, 10, 10, 5, 5, 5,
            "Carrier", 0, 1)));
        assertFalse(world.add(new AirPlane("Plane", 10, 10, 10, 5, 5, 5,
            "Carrier", 1, 0)));
    }


    /**
     * Balloon validation should reject missing type or non-positive ascent
     * rate.
     */
    public void testBalloonValidation() {
        assertFalse(world.add(new Balloon("Ball", 10, 10, 10, 5, 5, 5, null,
            10)));
        assertFalse(world.add(new Balloon("Ball", 10, 10, 10, 5, 5, 5, "type",
            0)));
    }


    /**
     * Birds with null species or non-positive counts are rejected.
     */
    public void testBirdValidation() {
        assertFalse(world.add(new Bird("Bird", 10, 10, 10, 5, 5, 5, null, 1)));
        assertFalse(world.add(new Bird("Bird", 10, 10, 10, 5, 5, 5, "species",
            0)));
    }


    /**
     * Drones must provide a brand and positive engine count.
     */
    public void testDroneValidation() {
        assertFalse(world.add(new Drone("Drone", 10, 10, 10, 5, 5, 5, null,
            1)));
        assertFalse(world.add(new Drone("Drone", 10, 10, 10, 5, 5, 5, "brand",
            0)));
    }


    /**
     * Rockets require positive ascent rate and non-negative trajectory.
     */
    public void testRocketValidation() {
        assertFalse(world.add(new Rocket("Rocket", 10, 10, 10, 5, 5, 5, 0, 1)));
        assertFalse(world.add(new Rocket("Rocket", 10, 10, 10, 5, 5, 5, 1,
            -0.1)));
    }


    /**
     * Names must be non-null and contain visible characters.
     */
    public void testNameValidationRejectsBlank() {
        assertFalse(world.add(new Balloon("   ", 10, 10, 10, 5, 5, 5, "type",
            5)));
    }


    /**
     * Object dimensions that extend past the world bounds should be rejected.
     */
    public void testDimensionValidationRejectsOversizedBox() {
        assertFalse(world.add(new Balloon("Big", 1000, 0, 0, 50, 50, 50, "type",
            5)));
    }


    /**
     * Intersect queries that start outside the world return null.
     */
    public void testIntersectInvalidOrigin() {
        assertNull(world.intersect(1024, 0, 0, 1, 1, 1));
        assertNull(world.intersect(0, 1024, 0, 1, 1, 1));
        assertNull(world.intersect(0, 0, 1024, 1, 1, 1));
    }


    /**
     * Intersect queries that lie on the edge of the world should succeed.
     */
    public void testIntersectAcceptsEdgeBox() {
        String report = world.intersect(1023, 0, 0, 1, 1, 1);
        assertNotNull(report);
        assertTrue(report.contains("The following objects intersect"));
    }
}
