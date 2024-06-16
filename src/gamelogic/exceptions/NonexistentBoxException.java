package gamelogic.exceptions;

/**
 * Exception checking if the box index is valid, i.e. if the box exists.
 */
public class NonexistentBoxException extends Exception {
    public NonexistentBoxException() {
        super("This box does not exist");
    }
}