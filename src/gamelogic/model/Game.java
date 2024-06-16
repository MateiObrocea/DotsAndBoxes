package gamelogic.model;

import java.util.List;

/**
 * Interface for a game.
 * Inspired from the Tic-Tac-Toe game from Software Systems, in week 5.
 */
public interface Game {
    //@ instance invariant !isGameOver() ==> getValidLocations().size() > 0;
    //@ instance invariant !isGameOver() ==> getWinner() == null;
    //@ instance invariant !isGameOver() ==> getTurn() != null;

    /**
     * Check if the game is over, i.e., there is a winner or no more moves are available.
     *
     * @return whether the game is over
     */
    //@ pure;
    boolean isGameOver();

    /**
     * Query whose turn it is.
     *
     * @return the player whose turn it is
     */
    //@ pure;
    Player getTurn();

    /**
     * Query whose turn it is not.
     *
     * @return the other player
     */
    Player getOther();

    /**
     * Get the winner of the game. If the game is a draw, then this method returns null (e.g., in case of a 4*4 board).
     *
     * @return the winner, or null if no player is the winner or the game is not over
     */
    //@ pure;
    Player getWinner();

    /**
     * Return all locations where a line can be drawn in the current state of the game.
     *
     * @return the list of currently valid moves
     */
    //@ ensures (\forall int l; \result.contains(l); isValidLocation(l));
    //@ pure
    List<Integer> getValidLocations();

    /**
     * Check if a location is a valid location.
     *
     * @return true if the move is a valid move.
     */
    //@ ensures \result <==> (\exists int l; getValidLocations().contains(l); l == location);
    //@ pure;
    boolean isValidLocation(int location);

    /**
     * Performs the move, i.e., draws a line, assuming it is a valid move.
     */
    //@ requires isValidLocation(line.getLocation());
    void drawLine(Line line);

    /**
     * Updates the scores of the players when boxes have been drawn.
     * A completed box corresponds to a score increase of 1.
     */
    void updateScores();

    /**
     * Performs a deep copy of the game.
     *
     * @return the entire game at the current state.
     */
    Game deepCopy();
}
