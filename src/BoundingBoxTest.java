import student.TestCase;

/**
 * Focused tests for {@link BoundingBox}.
 */
public class BoundingBoxTest extends TestCase {
    private BoundingBox box;

    /**
     * Common setup for tests.
     */
    public void setUp() {
        box = new BoundingBox(1, 2, 3, 10, 20, 30);
    }


    /**
     * Verifies points strictly inside the box return true.
     */
    public void testContainsPointInside() {
        assertTrue(box.containsPoint(5, 10, 20));
    }


    /**
     * Points outside or exactly on the max boundary must return false.
     */
    public void testContainsPointOutsideEdges() {
        assertFalse(box.containsPoint(0, 10, 20));
        assertFalse(box.containsPoint(11, 10, 20));
        assertFalse(box.containsPoint(5, 1, 20));
        assertFalse(box.containsPoint(5, 22, 20));
        assertFalse(box.containsPoint(5, 10, 2));
        assertFalse(box.containsPoint(5, 10, 33));
    }


    /**
     * Intersection results should match the overlapping region.
     */
    public void testIntersectionDimensions() {
        BoundingBox other = new BoundingBox(5, 10, 0, 4, 15, 12);
        BoundingBox overlap = box.intersection(other);
        assertNotNull(overlap);
        assertEquals(5, overlap.getX());
        assertEquals(10, overlap.getY());
        assertEquals(3, overlap.getZ());
        assertEquals(4, overlap.getXWidth());
        assertEquals(12, overlap.getYWidth());
        assertEquals(9, overlap.getZWidth());
        assertTrue(box.intersects(other));
    }


    /**
     * Adjacent boxes that only share a face do not intersect.
     */
    public void testAdjacentBoxesDoNotIntersect() {
        BoundingBox adjacentX = new BoundingBox(11, 2, 3, 5, 5, 5);
        assertFalse(box.intersects(adjacentX));
        assertNull(box.intersection(adjacentX));
    }


    /**
     * Boxes that touch along the Y dimension should also not intersect.
     */
    public void testAdjacentAlongYDoesNotIntersect() {
        BoundingBox adjacentY = new BoundingBox(1, 22, 3, 5, 4, 4);
        assertFalse(box.intersects(adjacentY));
        assertNull(box.intersection(adjacentY));
    }


    /**
     * Boxes that are adjacent along the Z dimension should not intersect.
     */
    public void testAdjacentAlongZDoesNotIntersect() {
        BoundingBox adjacentZ = new BoundingBox(1, 2, 33, 5, 5, 5);
        assertFalse(box.intersects(adjacentZ));
        assertNull(box.intersection(adjacentZ));
    }


    /**
     * Completely separated boxes have no intersection and never contain the
     * other origin.
     */
    public void testNonOverlappingBoxes() {
        BoundingBox far = new BoundingBox(100, 200, 300, 5, 6, 7);
        assertFalse(box.intersects(far));
        assertNull(box.intersection(far));
        assertFalse(far.containsPoint(box.getX(), box.getY(), box.getZ()));
    }
}

