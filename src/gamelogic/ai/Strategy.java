package gamelogic.ai;

import gamelogic.model.DotsAndBoxesGame;

/**
 * Interface that represents a strategy.
 */
public interface Strategy {
    /**
     * Method returns name of the strategy.
     *
     * @return The name of the strategy
     */
    /*@
        ensures \result != null;
        pure;
    */
    String getName();

    /**
     * Method that returns a valid line, given the current state of the game.
     *
     * @param game that is currently being played
     * @return a next legal line
     */
    /*@
        requires game != null;
        ensures game.isValidLocation(\result);
        pure
    */
    int computeLocation(DotsAndBoxesGame game);
}
