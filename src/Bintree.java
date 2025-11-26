/**
 * Three-dimensional Bintree that stores AirObjects using alternating
 * orthogonal splits (x, y, z).
 */
public class Bintree {
    private final BoundingBox worldBounds;
    private final FlyweightNode flyweight;
    private BintreeNode root;

    /**
     * Simple counter helper.
     */
    private static class Counter {
        private int value;

        void increment() {
            value++;
        }
    }


    /**
     * Mutable boolean container for recursive operations.
     */
    private static class BooleanBox {
        private boolean value;

        void set() {
            value = true;
        }
    }


    /**
     * Constructs a new Bintree bounded by the provided world box.
     *
     * @param bounds
     *            bounding region for the tree
     */
    public Bintree(BoundingBox bounds) {
        worldBounds = bounds;
        flyweight = new FlyweightNode();
        root = flyweight;
    }


    /**
     * Removes all objects from the bintree.
     */
    public void clear() {
        root = flyweight;
    }


    /**
     * Inserts a new object into the bintree.
     *
     * @param obj
     *            object to insert
     */
    public void insert(AirObject obj) {
        root = root.insert(obj, worldBounds, 0);
    }


    /**
     * Removes an object from the bintree.
     *
     * @param obj
     *            object to remove
     * @return true if any instance was removed
     */
    public boolean remove(AirObject obj) {
        BooleanBox removed = new BooleanBox();
        root = root.remove(obj, worldBounds, 0, removed);
        return removed.value;
    }


    /**
     * Produces a preorder traversal string.
     *
     * @return formatted listing
     */
    public String print() {
        StringBuilder builder = new StringBuilder();
        Counter counter = new Counter();
        root.print(builder, 0, worldBounds, counter);
        builder.append(counter.value).append(" Bintree nodes printed\r\n");
        return builder.toString();
    }


    /**
     * Produces the collisions report.
     *
     * @return collisions string
     */
    public String collisionsReport() {
        StringBuilder builder = new StringBuilder();
        builder.append("The following collisions exist in the database:\r\n");
        if (root != flyweight) {
            root.collectCollisions(builder, worldBounds, 0);
        }
        return builder.toString();
    }


    /**
     * Produces the intersection report for the provided box.
     *
     * @param query
     *            query bounding box
     * @return formatted string
     */
    public String intersectReport(BoundingBox query) {
        StringBuilder builder = new StringBuilder();
        builder.append("The following objects intersect (").append(query.getX())
            .append(" ").append(query.getY()).append(" ").append(query.getZ())
            .append(" ").append(query.getXWidth()).append(" ")
            .append(query.getYWidth()).append(" ")
            .append(query.getZWidth()).append("):\r\n");
        Counter counter = new Counter();
        if (root == flyweight) {
            counter.increment();
        }
        else {
            root.intersect(query, worldBounds, 0, builder, counter);
        }
        builder.append(counter.value).append(
            " nodes were visited in the bintree\r\n");
        return builder.toString();
    }


    /**
     * Bintree node interface.
     */
    private interface BintreeNode {
        BintreeNode insert(AirObject obj, BoundingBox region, int depth);


        BintreeNode remove(AirObject obj, BoundingBox region, int depth,
            BooleanBox removed);


        void print(StringBuilder builder, int depth, BoundingBox region,
            Counter counter);


        void collectCollisions(StringBuilder builder, BoundingBox region,
            int depth);


        void intersect(BoundingBox query, BoundingBox region, int depth,
            StringBuilder builder, Counter counter);


        boolean isFlyweight();
    }


    /**
     * Empty node implementation.
     */
    private class FlyweightNode implements BintreeNode {
        @Override
        public BintreeNode insert(AirObject obj, BoundingBox region,
            int depth) {
            LeafNode leaf = new LeafNode();
            leaf.add(obj);
            return leaf;
        }


        @Override
        public BintreeNode remove(AirObject obj, BoundingBox region,
            int depth, BooleanBox removed) {
            return this;
        }


        @Override
        public void print(StringBuilder builder, int depth, BoundingBox region,
            Counter counter) {
            appendIndent(builder, depth);
            builder.append("E ").append(region.format()).append(" ")
                .append(depth).append("\r\n");
            counter.increment();
        }


        @Override
        public void collectCollisions(StringBuilder builder,
            BoundingBox region, int depth) {
            // Nothing to report
        }


        @Override
        public void intersect(BoundingBox query, BoundingBox region, int depth,
            StringBuilder builder, Counter counter) {
            // flyweight nodes are not visited
        }


        @Override
        public boolean isFlyweight() {
            return true;
        }
    }


    /**
     * Leaf node implementation that stores objects.
     */
    private class LeafNode implements BintreeNode {
        private final AirObjectStorage objects;

        LeafNode() {
            objects = new AirObjectStorage();
        }


        void add(AirObject obj) {
            objects.add(obj);
        }


        @Override
        public BintreeNode insert(AirObject obj, BoundingBox region,
            int depth) {
            objects.add(obj);
            if (shouldSplit(region, depth)) {
                return split(region, depth);
            }
            return this;
        }


        @Override
        public BintreeNode remove(AirObject obj, BoundingBox region,
            int depth, BooleanBox removed) {
            if (objects.remove(obj)) {
                removed.set();
            }
            if (objects.isEmpty()) {
                return flyweight;
            }
            return this;
        }


        @Override
        public void print(StringBuilder builder, int depth, BoundingBox region,
            Counter counter) {
            appendIndent(builder, depth);
            builder.append("Leaf with ").append(objects.size())
                .append(" objects ").append(region.format()).append(" ")
                .append(depth).append("\r\n");
            for (int i = 0; i < objects.size(); i++) {
                appendIndent(builder, depth + 1);
                builder.append("(").append(objects.get(i).toString())
                    .append(")\r\n");
            }
            counter.increment();
        }


        @Override
        public void collectCollisions(StringBuilder builder,
            BoundingBox region, int depth) {
            builder.append("In leaf node ").append(region.format()).append(" ")
                .append(depth).append("\r\n");
            int limit = objects.size();
            for (int i = 0; i < limit; i++) {
                BoundingBox first = objects.get(i).getBoundingBox();
                for (int j = i + 1; j < limit; j++) {
                    BoundingBox second = objects.get(j).getBoundingBox();
                    BoundingBox intersection = first.intersection(second);
                    if (intersection != null && region.containsPoint(
                        intersection.getX(), intersection.getY(),
                        intersection.getZ())) {
                        builder.append("(").append(objects.get(i).toString())
                            .append(") and (")
                            .append(objects.get(j).toString()).append(")\r\n");
                    }
                }
            }
        }


        @Override
        public void intersect(BoundingBox query, BoundingBox region, int depth,
            StringBuilder builder, Counter counter) {
            counter.increment();
            builder.append("In leaf node ").append(region.format()).append(" ")
                .append(depth).append("\r\n");
            for (int i = 0; i < objects.size(); i++) {
                AirObject obj = objects.get(i);
                BoundingBox overlap = obj.getBoundingBox().intersection(query);
                if (overlap != null && region.containsPoint(overlap.getX(),
                    overlap.getY(), overlap.getZ())) {
                    builder.append(obj.toString()).append("\r\n");
                }
            }
        }


        @Override
        public boolean isFlyweight() {
            return false;
        }


        private boolean shouldSplit(BoundingBox region, int depth) {
            if (objects.size() <= 3) {
                return false;
            }
            if (allBoxesOverlap()) {
                return false;
            }
            int axis = depth % 3;
            int axisWidth = axis == 0 ? region.getXWidth()
                : axis == 1 ? region.getYWidth() : region.getZWidth();
            return axisWidth > 1;
        }


        private boolean allBoxesOverlap() {
            if (objects.size() <= 1) {
                return true;
            }
            BoundingBox intersection = objects.get(0).getBoundingBox();
            for (int i = 1; i < objects.size(); i++) {
                intersection = intersection
                    .intersection(objects.get(i).getBoundingBox());
                if (intersection == null) {
                    return false;
                }
            }
            return intersection != null;
        }


        private BintreeNode split(BoundingBox region, int depth) {
            InternalNode internal = new InternalNode(depth % 3);
            AirObject[] entries = snapshot();
            for (int i = 0; i < entries.length; i++) {
                internal.insert(entries[i], region, depth);
            }
            return internal;
        }


        AirObject[] snapshot() {
            return objects.toArray();
        }
    }


    /**
     * Internal node containing two children.
     */
    private class InternalNode implements BintreeNode {
        private BintreeNode left;
        private BintreeNode right;
        private final int axis;

        InternalNode(int splitAxis) {
            axis = splitAxis;
            left = flyweight;
            right = flyweight;
        }


        @Override
        public BintreeNode insert(AirObject obj, BoundingBox region,
            int depth) {
            BoundingBox[] children = splitRegion(region, axis);
            if (children[0].intersects(obj.getBoundingBox())) {
                left = left.insert(obj, children[0], depth + 1);
            }
            if (children[1].intersects(obj.getBoundingBox())) {
                right = right.insert(obj, children[1], depth + 1);
            }
            return this;
        }


        @Override
        public BintreeNode remove(AirObject obj, BoundingBox region,
            int depth, BooleanBox removed) {
            BoundingBox[] children = splitRegion(region, axis);
            if (children[0].intersects(obj.getBoundingBox())) {
                left = left.remove(obj, children[0], depth + 1, removed);
            }
            if (children[1].intersects(obj.getBoundingBox())) {
                right = right.remove(obj, children[1], depth + 1, removed);
            }
            if (left.isFlyweight() && right.isFlyweight()) {
                return flyweight;
            }
            AirObjectStorage combined = new AirObjectStorage();
            gatherObjects(left, combined);
            gatherObjects(right, combined);
            if (combined.size() > 0 && combined.size() <= 3) {
                LeafNode merged = new LeafNode();
                AirObject[] entries = combined.toArray();
                for (int i = 0; i < entries.length; i++) {
                    merged.add(entries[i]);
                }
                if (!merged.shouldSplit(region, depth)) {
                    return merged;
                }
            }
            return this;
        }


        @Override
        public void print(StringBuilder builder, int depth,
            BoundingBox region, Counter counter) {
            appendIndent(builder, depth);
            builder.append("I ").append(region.format()).append(" ")
                .append(depth).append("\r\n");
            counter.increment();
            BoundingBox[] children = splitRegion(region, axis);
            left.print(builder, depth + 1, children[0], counter);
            right.print(builder, depth + 1, children[1], counter);
        }


        @Override
        public void collectCollisions(StringBuilder builder,
            BoundingBox region, int depth) {
            BoundingBox[] children = splitRegion(region, axis);
            left.collectCollisions(builder, children[0], depth + 1);
            right.collectCollisions(builder, children[1], depth + 1);
        }


        @Override
        public void intersect(BoundingBox query, BoundingBox region, int depth,
            StringBuilder builder, Counter counter) {
            counter.increment();
            builder.append("In Internal node ").append(region.format()).append(
                " ").append(depth).append("\r\n");
            BoundingBox[] children = splitRegion(region, axis);
            if (children[0].intersects(query)) {
                left.intersect(query, children[0], depth + 1, builder,
                    counter);
            }
            if (children[1].intersects(query)) {
                right.intersect(query, children[1], depth + 1, builder,
                    counter);
            }
        }


        @Override
        public boolean isFlyweight() {
            return false;
        }


        private void gatherObjects(BintreeNode node,
            AirObjectStorage storage) {
            if (node.isFlyweight()) {
                return;
            }
            if (node instanceof LeafNode) {
                AirObject[] entries = ((LeafNode)node).snapshot();
                for (int i = 0; i < entries.length; i++) {
                    storage.add(entries[i]);
                }
            }
            else if (node instanceof InternalNode) {
                ((InternalNode)node).gatherObjects(storage);
            }
        }


        private void gatherObjects(AirObjectStorage storage) {
            gatherObjects(left, storage);
            gatherObjects(right, storage);
        }
    }


    /**
     * Simple storage helper to avoid using ArrayList.
     */
    private static class AirObjectStorage {
        private AirObject[] data;
        private int size;

        AirObjectStorage() {
            data = new AirObject[4];
            size = 0;
        }


        void add(AirObject obj) {
            if (contains(obj)) {
                return;
            }
            ensureCapacity(size + 1);
            int index = size;
            while (index > 0 && compare(obj, data[index - 1]) < 0) {
                data[index] = data[index - 1];
                index--;
            }
            data[index] = obj;
            size++;
        }


        boolean remove(AirObject obj) {
            for (int i = 0; i < size; i++) {
                if (data[i] == obj) {
                    for (int j = i + 1; j < size; j++) {
                        data[j - 1] = data[j];
                    }
                    size--;
                    data[size] = null;
                    return true;
                }
            }
            return false;
        }


        boolean isEmpty() {
            return size == 0;
        }


        int size() {
            return size;
        }


        AirObject get(int index) {
            return data[index];
        }


        AirObject[] toArray() {
            AirObject[] copy = new AirObject[size];
            for (int i = 0; i < size; i++) {
                copy[i] = data[i];
            }
            return copy;
        }


        void copyFrom(AirObjectStorage other) {
            for (int i = 0; i < other.size; i++) {
                add(other.data[i]);
            }
        }


        private boolean contains(AirObject obj) {
            for (int i = 0; i < size; i++) {
                if (data[i] == obj) {
                    return true;
                }
            }
            return false;
        }


        private void ensureCapacity(int required) {
            if (required <= data.length) {
                return;
            }
            int newCapacity = data.length * 2;
            while (newCapacity < required) {
                newCapacity *= 2;
            }
            AirObject[] newData = new AirObject[newCapacity];
            for (int i = 0; i < size; i++) {
                newData[i] = data[i];
            }
            data = newData;
        }


        private int compare(AirObject first, AirObject second) {
            if (first == null && second == null) {
                return 0;
            }
            if (first == null) {
                return -1;
            }
            if (second == null) {
                return 1;
            }
            String left = first.getName();
            String right = second.getName();
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return -1;
            }
            if (right == null) {
                return 1;
            }
            return left.compareTo(right);
        }
    }


    static BoundingBox[] splitRegionForTest(BoundingBox region, int axis) {
        return splitRegion(region, axis);
    }


    static int compareAirObjectsForTest(AirObject first, AirObject second) {
        AirObjectStorage storage = new AirObjectStorage();
        return storage.compare(first, second);
    }


    private static BoundingBox[] splitRegion(BoundingBox region, int axis) {
        BoundingBox[] children = new BoundingBox[2];
        if (axis == 0) {
            int half = region.getXWidth() / 2;
            if (half == 0) {
                half = 1;
            }
            int remainder = region.getXWidth() - half;
            children[0] = new BoundingBox(region.getX(), region.getY(),
                region.getZ(), half, region.getYWidth(), region.getZWidth());
            children[1] = new BoundingBox(region.getX() + half, region.getY(),
                region.getZ(), remainder, region.getYWidth(),
                region.getZWidth());
        }
        else if (axis == 1) {
            int half = region.getYWidth() / 2;
            if (half == 0) {
                half = 1;
            }
            int remainder = region.getYWidth() - half;
            children[0] = new BoundingBox(region.getX(), region.getY(),
                region.getZ(), region.getXWidth(), half, region.getZWidth());
            children[1] = new BoundingBox(region.getX(),
                region.getY() + half, region.getZ(), region.getXWidth(),
                remainder, region.getZWidth());
        }
        else {
            int half = region.getZWidth() / 2;
            if (half == 0) {
                half = 1;
            }
            int remainder = region.getZWidth() - half;
            children[0] = new BoundingBox(region.getX(), region.getY(),
                region.getZ(), region.getXWidth(), region.getYWidth(), half);
            children[1] = new BoundingBox(region.getX(), region.getY(),
                region.getZ() + half, region.getXWidth(), region.getYWidth(),
                remainder);
        }
        return children;
    }


    private static void appendIndent(StringBuilder builder, int depth) {
        for (int i = 0; i < depth; i++) {
            builder.append("  ");
        }
    }
}

