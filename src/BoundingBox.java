/**
 * Immutable axis-aligned bounding box used by the ATC data structures.
 */
public class BoundingBox {
    private final int x;
    private final int y;
    private final int z;
    private final int xWidth;
    private final int yWidth;
    private final int zWidth;

    /**
     * Creates a bounding box.
     *
     * @param xCoord
     *            x origin (minimum)
     * @param yCoord
     *            y origin (minimum)
     * @param zCoord
     *            z origin (minimum)
     * @param xSize
     *            width in the x direction
     * @param ySize
     *            width in the y direction
     * @param zSize
     *            width in the z direction
     */
    public BoundingBox(int xCoord, int yCoord, int zCoord, int xSize, int ySize,
        int zSize) {
        x = xCoord;
        y = yCoord;
        z = zCoord;
        xWidth = xSize;
        yWidth = ySize;
        zWidth = zSize;
    }


    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public int getZ() {
        return z;
    }


    public int getXWidth() {
        return xWidth;
    }


    public int getYWidth() {
        return yWidth;
    }


    public int getZWidth() {
        return zWidth;
    }


    /**
     * Checks if this box intersects the other box (strict overlap, adjacency
     * does not count).
     *
     * @param other
     *            the other bounding box
     * @return true when the two boxes overlap with non-zero volume
     */
    public boolean intersects(BoundingBox other) {
        return overlapsOnAxis(x, xWidth, other.x, other.xWidth)
            && overlapsOnAxis(y, yWidth, other.y, other.yWidth)
            && overlapsOnAxis(z, zWidth, other.z, other.zWidth);
    }


    /**
     * Checks if this box intersects the given parameters.
     *
     * @param xCoord
     *            x origin
     * @param yCoord
     *            y origin
     * @param zCoord
     *            z origin
     * @param xSize
     *            width in x direction
     * @param ySize
     *            width in y direction
     * @param zSize
     *            width in z direction
     * @return true when the boxes overlap with non-zero volume
     */
    public boolean intersects(int xCoord, int yCoord, int zCoord, int xSize,
        int ySize, int zSize) {
        return overlapsOnAxis(x, xWidth, xCoord, xSize)
            && overlapsOnAxis(y, yWidth, yCoord, ySize)
            && overlapsOnAxis(z, zWidth, zCoord, zSize);
    }


    /**
     * Computes the intersection between two boxes.
     *
     * @param other
     *            other box
     * @return intersection box, or null if there is no overlap
     */
    public BoundingBox intersection(BoundingBox other) {
        if (!intersects(other)) {
            return null;
        }
        int nx = Math.max(x, other.x);
        int ny = Math.max(y, other.y);
        int nz = Math.max(z, other.z);
        int nxw = Math.min(x + xWidth, other.x + other.xWidth) - nx;
        int nyw = Math.min(y + yWidth, other.y + other.yWidth) - ny;
        int nzw = Math.min(z + zWidth, other.z + other.zWidth) - nz;
        return new BoundingBox(nx, ny, nz, nxw, nyw, nzw);
    }


    /**
     * Creates a string that follows the assignment formatting.
     *
     * @return formatted string "(x, y, z, xwid, ywid, zwid)"
     */
    public String format() {
        return "(" + x + ", " + y + ", " + z + ", " + xWidth + ", " + yWidth
            + ", " + zWidth + ")";
    }


    /**
     * Checks if the supplied point is contained within this box.
     *
     * @param px
     *            x coordinate of point
     * @param py
     *            y coordinate of point
     * @param pz
     *            z coordinate of point
     * @return true if the point lies within the box volume
     */
    public boolean containsPoint(int px, int py, int pz) {
        return px >= x && px < x + xWidth && py >= y && py < y + yWidth
            && pz >= z && pz < z + zWidth;
    }


    private boolean overlapsOnAxis(int startA, int widthA, int startB,
        int widthB) {
        int endA = startA + widthA;
        int endB = startB + widthB;
        return startA < endB && startB < endA;
    }
}

