/**
 * Drone object.
 */
public class Drone extends AirObject {
    private final String brand;
    private final int engines;

    /**
     * Creates a new drone record.
     *
     * @param name
     *            object name
     * @param x
     *            x origin
     * @param y
     *            y origin
     * @param z
     *            z origin
     * @param xWidth
     *            x width
     * @param yWidth
     *            y width
     * @param zWidth
     *            z width
     * @param droneBrand
     *            brand name
     * @param engineCount
     *            number of engines
     */
    public Drone(String name, int x, int y, int z, int xWidth, int yWidth,
        int zWidth, String droneBrand, int engineCount) {
        super(name, x, y, z, xWidth, yWidth, zWidth);
        brand = droneBrand;
        engines = engineCount;
    }


    public String getBrand() {
        return brand;
    }


    public int getEngineCount() {
        return engines;
    }


    @Override
    public String toString() {
        return baseInfo() + " " + brand + " " + engines;
    }


    @Override
    protected String typePrefix() {
        return "Drone";
    }
}

