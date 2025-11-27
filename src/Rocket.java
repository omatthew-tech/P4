/**
 * Rocket object.
 *
 * @author Matthew Ozoroski (omatthew-tech)
 * @version 2025-11-26
 */
public class Rocket extends AirObject {
    private final int ascentRate;
    private final double trajectory;

    /**
     * Creates a new rocket record.
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
     * @param rate
     *            ascent rate
     * @param traj
     *            trajectory
     */
    public Rocket(
        String name,
        int x,
        int y,
        int z,
        int xWidth,
        int yWidth,
        int zWidth,
        int rate,
        double traj) {
        super(name, x, y, z, xWidth, yWidth, zWidth);
        ascentRate = rate;
        trajectory = traj;
    }


    public int getAscentRate() {
        return ascentRate;
    }


    public double getTrajectory() {
        return trajectory;
    }


    @Override
    public String toString() {
        return baseInfo() + " " + ascentRate + " " + trajectory;
    }


    @Override
    protected String typePrefix() {
        return "Rocket";
    }
}
