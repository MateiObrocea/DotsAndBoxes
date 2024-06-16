package gamelogic.model;

/**
 * Interface for a player of a game.
 * Inspired from Player from the TicTacToe project of Software Systems, week 5.
 */
public interface Player {

    Line determineLine(int location);

}
