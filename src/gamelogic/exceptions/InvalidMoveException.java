package gamelogic.exceptions;

/**
 * Exception thrown when a player tries to make a move that is not valid.
 */
public class InvalidMoveException extends Exception {
    public InvalidMoveException() {
        super("This move is not allowed.");
    }
}