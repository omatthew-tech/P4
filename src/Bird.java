/**
 * Bird object.
 */
public class Bird extends AirObject {
    private final String species;
    private final int number;

    /**
     * Creates a new bird record.
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
     * @param birdSpecies
     *            species
     * @param count
     *            number of birds
     */
    public Bird(String name, int x, int y, int z, int xWidth, int yWidth,
        int zWidth, String birdSpecies, int count) {
        super(name, x, y, z, xWidth, yWidth, zWidth);
        species = birdSpecies;
        number = count;
    }


    public String getSpecies() {
        return species;
    }


    public int getNumber() {
        return number;
    }


    @Override
    public String toString() {
        return baseInfo() + " " + species + " " + number;
    }


    @Override
    protected String typePrefix() {
        return "Bird";
    }
}

