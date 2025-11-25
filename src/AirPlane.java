/**
 * Airplane object.
 */
public class AirPlane extends AirObject {
    private final String carrier;
    private final int flightNumber;
    private final int engineCount;

    /**
     * Creates a new airplane instance.
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
     * @param carrierName
     *            carrier
     * @param flightNum
     *            flight number
     * @param engines
     *            engine count
     */
    public AirPlane(String name, int x, int y, int z, int xWidth, int yWidth,
        int zWidth, String carrierName, int flightNum, int engines) {
        super(name, x, y, z, xWidth, yWidth, zWidth);
        carrier = carrierName;
        flightNumber = flightNum;
        engineCount = engines;
    }


    public String getCarrier() {
        return carrier;
    }


    public int getFlightNumber() {
        return flightNumber;
    }


    public int getEngineCount() {
        return engineCount;
    }


    @Override
    public String toString() {
        return baseInfo() + " " + carrier + " " + flightNumber + " "
            + engineCount;
    }


    @Override
    protected String typePrefix() {
        return "Airplane";
    }
}

