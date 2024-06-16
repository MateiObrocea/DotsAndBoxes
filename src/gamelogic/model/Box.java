package gamelogic.model;

/**
 * A class of boxes for the Dots and Boxes game.
 * The location of each box must be a valid location for any box.
 */
public class Box {
    private final int location;

    private BasicPlayer owner;
    private Line[] lines = new Line[4];

    /**
     * Creates a new Box object.
     */
    public Box(int location) {
        this.location = location;
    }

    /**
     * Determines if a box is completed.
     */
    //@pure
    public boolean isComplete() {
        return lines[0] != null && lines[1] != null && lines[2] != null && lines[3] != null;
    }

    /**
     * Retrieves the 4 lines associated with this box.
     *
     * @return the lines as an array. Note: the elements can be null (if a line has not been drawn yet).
     */
    //@ pure
    public Line[] getLines() {
        return lines;
    }

    /**
     * Sets the owner of the box, depending on who completed the box.
     *
     * @param owner the player who completed the box.
     */
    public void setOwner(BasicPlayer owner) {
        this.owner = owner;
    }

    /**
     * Gets the owner of the box.
     *
     * @return the owner of the box.
     */
    //@ pure
    public BasicPlayer getOwner() {
        return owner;
    }

    /**
     * Gets the location of the box.
     *
     * @return the location of the box.
     */
    public int getLocation() {
        return location;
    }

    /**
     * Establishes an array of 4 lines.
     * It is not necessary that the array is populated.
     *
     * @param lines the lines of the box.
     */
    public void setLines(Line[] lines) {
        this.lines = lines;
    }
}
