import java.util.Random;

/**
 * Skip list specialized for AirObject instances keyed by name.
 */
public class AirObjectSkipList {
    private static final int MAX_LEVEL = 32;
    private final Random random;
    private final Node head;
    private int currentLevel;
    private int size;

    /**
     * Node definition.
     */
    private static class Node {
        private final AirObject value;
        private final Node[] forward;
        private final int level;

        Node(int nodeLevel, AirObject obj) {
            level = nodeLevel;
            value = obj;
            forward = new Node[nodeLevel + 1];
        }


        String key() {
            return value == null ? null : value.getName();
        }
    }


    /**
     * Constructs a skip list using the provided random number generator.
     *
     * @param rnd
     *            random source (non-null)
     */
    public AirObjectSkipList(Random rnd) {
        random = rnd;
        head = new Node(MAX_LEVEL - 1, null);
        currentLevel = 0;
        size = 0;
    }


    /**
     * Removes all entries from the skip list.
     */
    public void clear() {
        for (int i = 0; i < head.forward.length; i++) {
            head.forward[i] = null;
        }
        currentLevel = 0;
        size = 0;
    }


    /**
     * Inserts a new AirObject.
     *
     * @param obj
     *            object to insert
     * @return true if inserted, false if duplicate or invalid
     */
    public boolean insert(AirObject obj) {
        if (obj == null || obj.getName() == null) {
            return false;
        }
        Node[] update = new Node[MAX_LEVEL];
        Node current = head;
        String key = obj.getName();
        for (int i = currentLevel; i >= 0; i--) {
            current = walkForward(current, i, key);
            update[i] = current;
        }
        current = current.forward[0];
        if (current != null && key.equals(current.key())) {
            return false;
        }
        int nodeLevel = randomLevel();
        if (nodeLevel > currentLevel) {
            for (int i = currentLevel + 1; i <= nodeLevel; i++) {
                update[i] = head;
            }
            currentLevel = nodeLevel;
        }
        Node fresh = new Node(nodeLevel, obj);
        for (int i = 0; i <= nodeLevel; i++) {
            fresh.forward[i] = update[i].forward[i];
            update[i].forward[i] = fresh;
        }
        size++;
        return true;
    }


    /**
     * Searches for an object by name.
     *
     * @param name
     *            target name
     * @return the matching object or null
     */
    public AirObject search(String name) {
        Node node = findNode(name);
        return node == null ? null : node.value;
    }


    /**
     * Removes and returns the AirObject with the given name.
     *
     * @param name
     *            name to delete
     * @return removed object, or null if not found
     */
    public AirObject remove(String name) {
        if (name == null) {
            return null;
        }
        Node[] update = new Node[MAX_LEVEL];
        Node current = head;
        for (int i = currentLevel; i >= 0; i--) {
            current = walkForward(current, i, name);
            update[i] = current;
        }
        current = current.forward[0];
        if (current == null || !name.equals(current.key())) {
            return null;
        }
        for (int i = 0; i <= current.level; i++) {
            if (update[i].forward[i] != current) {
                break;
            }
            update[i].forward[i] = current.forward[i];
        }
        while (currentLevel > 0 && head.forward[currentLevel] == null) {
            currentLevel--;
        }
        size--;
        return current.value;
    }


    /**
     * Traverses all nodes in ascending order.
     *
     * @param visitor
     *            visitor invoked for each object
     */
    public void traverse(Visitor visitor) {
        Node current = head.forward[0];
        while (current != null) {
            visitor.visit(current.value);
            current = current.forward[0];
        }
    }


    /**
     * Traverses all nodes whose keys are within [start, end].
     *
     * @param start
     *            start key (inclusive)
     * @param end
     *            end key (inclusive)
     * @param visitor
     *            visitor invoked for each match
     */
    public void traverseRange(String start, String end, Visitor visitor) {
        if (start == null || end == null) {
            return;
        }
        Node current = head;
        for (int i = currentLevel; i >= 0; i--) {
            current = walkForward(current, i, start);
        }
        current = current.forward[0];
        while (current != null && current.key().compareTo(end) <= 0) {
            visitor.visit(current.value);
            current = current.forward[0];
        }
    }


    /**
     * Prints the skip list in the assignment format.
     *
     * @return formatted string
     */
    public String formatStructure() {
        if (size == 0) {
            return "SkipList is empty";
        }
        StringBuilder builder = new StringBuilder();
        appendNode(builder, head);
        Node current = head.forward[0];
        while (current != null) {
            appendNode(builder, current);
            current = current.forward[0];
        }
        builder.append(size).append(" skiplist nodes printed\r\n");
        return builder.toString();
    }


    public int size() {
        return size;
    }


    public boolean isEmpty() {
        return size == 0;
    }


    /**
     * Visible for testing to inspect the current maximum level.
     *
     * @return the highest level currently in the list
     */
    int currentLevel() {
        return currentLevel;
    }


    private Node findNode(String name) {
        if (name == null) {
            return null;
        }
        Node current = head;
        for (int i = currentLevel; i >= 0; i--) {
            current = walkForward(current, i, name);
        }
        current = current.forward[0];
        if (current != null && name.equals(current.key())) {
            return current;
        }
        return null;
    }


    private void appendNode(StringBuilder builder, Node node) {
        int depth = node == head ? currentLevel : node.level;
        builder.append("Node has depth ").append(depth + 1).append(", Value (");
        builder.append(node.value == null ? "null" : node.value.toString());
        builder.append(")\r\n");
    }


    private Node walkForward(Node current, int level, String key) {
        Node next = current.forward[level];
        while (next != null && next.key().compareTo(key) < 0) {
            current = next;
            next = current.forward[level];
        }
        return current;
    }


    private int randomLevel() {
        int lvl = 0;
        while (lvl < MAX_LEVEL - 1 && (random.nextInt() & 1) == 0) {
            lvl++;
        }
        return lvl;
    }


    /**
     * Simple visitor interface to avoid using java.util collections.
     */
    public interface Visitor {
        /**
         * Called for each AirObject encountered.
         *
         * @param obj
         *            current object
         */
        void visit(AirObject obj);
    }
}

