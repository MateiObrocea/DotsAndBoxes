package gamelogic.model;

/**
 * Simple class for a line to be drawn on the board.
 * In this project, a move is represented by a line and referred to as a line.
 * TODO: split into horizontal and vertical line?
 */
public class Line {
    private final int location;


    public Line(int location) {
        this.location = location;
    }

    /**
     * Gets the location of the line.
     *
     * @return the location of the line.
     */
    //@ pure
    public int getLocation() {
        return location;
    }
}
