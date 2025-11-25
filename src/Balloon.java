/**
 * Balloon object.
 */
public class Balloon extends AirObject {
    private final String type;
    private final int ascentRate;

    /**
     * Creates a new balloon.
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
     * @param balloonType
     *            balloon type
     * @param rate
     *            ascent rate
     */
    public Balloon(String name, int x, int y, int z, int xWidth, int yWidth,
        int zWidth, String balloonType, int rate) {
        super(name, x, y, z, xWidth, yWidth, zWidth);
        type = balloonType;
        ascentRate = rate;
    }


    public String getType() {
        return type;
    }


    public int getAscentRate() {
        return ascentRate;
    }


    @Override
    public String toString() {
        return baseInfo() + " " + type + " " + ascentRate;
    }


    @Override
    protected String typePrefix() {
        return "Balloon";
    }
}

