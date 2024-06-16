package gamelogic.model;

/**
 * A player of a game.
 * Inspired from AbstractPlayer from the TicTacToe project of Software Systems, week 5.
 */
public class BasicPlayer implements Player {
    protected String name;
    protected Mark mark;
    private int score = 0;

    /**
     * Creates a new Player object.
     */
    public BasicPlayer(String name, Mark mark) {
        this.name = name;
        this.mark = mark;
    }

    /**
     * Returns the name of the player.
     *
     * @return the name of the player
     */
    //@ pure
    public String getName() {
        return name;
    }

    /**
     * A way of printing the player.
     *
     * @return the name of the player
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the score of the player.
     *
     * @return the score of the player.
     */
    public int getScore() {
        return score;
    }

    /**
     * Increases the score of the player by 1.
     * This should happen when a player draws a box.
     */
    public void increaseScore() {
        this.score++;
    }

    /**
     * Sets the score of the player.
     * This should happen when the game is restarted, i.e., the score should be set to 0.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets the mark of the player.
     *
     * @return the player's mark.
     */
    //@ pure
    public Mark getMark() {
        return mark;
    }

    @Override
    public Line determineLine(int location) {
        return new Line(location);
    }
}
