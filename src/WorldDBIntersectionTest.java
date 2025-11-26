import java.util.Random;
import student.TestCase;

/**
 * Stress-style tests that compare the bintree intersect output with a
 * brute-force filter across all stored objects.
 */
public class WorldDBIntersectionTest extends TestCase {

    /**
     * Randomized regression for intersect reporting.
     */
    public void testIntersectMatchesBruteForce() {
        Random rnd = new Random(0xFACEFEED);
        WorldDB world = new WorldDB(rnd);
        AirObject[] objects = new AirObject[40];
        int count = 0;

        while (count < objects.length) {
            AirObject obj = randomBalloon(rnd, "Obj" + count);
            if (world.add(obj)) {
                objects[count++] = obj;
            }
        }

        for (int i = 0; i < 30; i++) {
            BoundingBox query = randomBox(rnd);
            String report = world.intersect(query.getX(), query.getY(),
                query.getZ(), query.getXWidth(), query.getYWidth(),
                query.getZWidth());
            for (int j = 0; j < objects.length; j++) {
                AirObject obj = objects[j];
                boolean expected = obj.getBoundingBox().intersects(
                    query.getX(), query.getY(), query.getZ(),
                    query.getXWidth(), query.getYWidth(), query.getZWidth());
                boolean actual = report.contains(obj.toString());
                assertEquals("Mismatch for " + obj + " in query " + query
                    .format(), expected, actual);
            }
        }
    }


    private Balloon randomBalloon(Random rnd, String name) {
        int x = rnd.nextInt(900);
        int y = rnd.nextInt(900);
        int z = rnd.nextInt(900);
        int xw = 5 + rnd.nextInt(50);
        int yw = 5 + rnd.nextInt(50);
        int zw = 5 + rnd.nextInt(50);
        return new Balloon(name, x, y, z, xw, yw, zw, "type", 10);
    }


    private BoundingBox randomBox(Random rnd) {
        int x = rnd.nextInt(900);
        int y = rnd.nextInt(900);
        int z = rnd.nextInt(900);
        int xw = 10 + rnd.nextInt(100);
        int yw = 10 + rnd.nextInt(100);
        int zw = 10 + rnd.nextInt(100);
        return new BoundingBox(x, y, z, xw, yw, zw);
    }
}

