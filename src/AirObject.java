/**
 * Base class for all AirObjects tracked by the ATC system.
 *
 * @author Matthew Ozoroski (omatthew-tech)
 * @version 2025-11-26
 */
public abstract class AirObject implements Comparable<AirObject> {
    private final String name;
    private final BoundingBox box;

    /**
     * Creates a new air object.
     *
     * @param objectName
     *            object name (may be null for invalid instances)
     * @param x
     *            x origin
     * @param y
     *            y origin
     * @param z
     *            z origin
     * @param xWidth
     *            width in x dimension
     * @param yWidth
     *            width in y dimension
     * @param zWidth
     *            width in z dimension
     */
    protected AirObject(
        String objectName,
        int x,
        int y,
        int z,
        int xWidth,
        int yWidth,
        int zWidth) {
        name = objectName;
        box = new BoundingBox(x, y, z, xWidth, yWidth, zWidth);
    }


    /**
     * @return x origin coordinate
     */
    public int getXorig() {
        return box.getX();
    }


    /**
     * @return y origin coordinate
     */
    public int getYorig() {
        return box.getY();
    }


    /**
     * @return z origin coordinate
     */
    public int getZorig() {
        return box.getZ();
    }


    /**
     * @return x width
     */
    public int getXwidth() {
        return box.getXWidth();
    }


    /**
     * @return y width
     */
    public int getYwidth() {
        return box.getYWidth();
    }


    /**
     * @return z width
     */
    public int getZwidth() {
        return box.getZWidth();
    }


    /**
     * @return name of the object
     */
    public String getName() {
        return name;
    }


    /**
     * @return bounding box
     */
    public BoundingBox getBoundingBox() {
        return box;
    }


    /**
     * @return the type name prefix used in the toString output
     */
    protected abstract String typePrefix();


    /**
     * @return formatted prefix shared among all subclasses
     */
    protected String baseInfo() {
        return typePrefix() + " " + name + " " + box.getX() + " " + box.getY()
            + " " + box.getZ() + " " + box.getXWidth() + " " + box.getYWidth()
            + " " + box.getZWidth();
    }


    @Override
    public int compareTo(AirObject other) {
        if (name == null && other.name == null) {
            return 0;
        }
        if (name == null) {
            return -1;
        }
        if (other.name == null) {
            return 1;
        }
        return name.compareTo(other.name);
    }
}
