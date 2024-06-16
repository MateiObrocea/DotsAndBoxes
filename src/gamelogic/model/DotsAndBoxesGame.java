package gamelogic.model;


import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a game of Dots and Boxes.
 * Inspired from TicTacToeGame from the TicTacToe project of Software Systems, week 5.
 */
public class DotsAndBoxesGame implements Game {

    public Board board;
    private final BasicPlayer player1;
    private final BasicPlayer player2;
    private BasicPlayer currentPlayer;

    // -- Constructor -----------------------------------------------

    public DotsAndBoxesGame(BasicPlayer player1, BasicPlayer player2) {
        this.board = new Board(); //because the game should create a board as soon as it is created i.e.,
        //the game needs a board to be playable
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
    }

    // -- Methods ---------------------------------------------------


    //@ pure;
    public boolean isGameOver() {
        return board.gameOver();
    }

    //@ pure;
    public BasicPlayer getTurn() {
        return currentPlayer;
    }

    //@ pure;
    public BasicPlayer getOther() {
        if (currentPlayer == player1) {
            return player2;
        } else {
            return player1;
        }
    }

    //@ pure;
    public BasicPlayer getWinner() {
        if (board.isFull()) {
            if (player1.getScore() > player2.getScore()) {
                return player1;
            } else {
                return player2;
            }
        } else {
            return null;
        }
    }

    //@ pure
    public List<Integer> getValidLocations() {
        List<Integer> validLocations = new ArrayList<>();
        for (int i = 0; i < board.calculateNrOfLines(Board.DIM); i++) {
            if (isValidLocation(i)) {
                validLocations.add(i);
            }
        }
        return validLocations;
    }

    /**
     * Reset the game to its initial state.
     */
    public void reset() {
        board.reset();
        player1.setScore(0);
        player2.setScore(0);
        currentPlayer = player1;
    }

    //@ pure
    public boolean isValidLocation(int location) {
        return board.getLine(location) == null;
    }

    //@ requires isValidLocation(l.getLocation());
    public void drawLine(Line l) {
        if (getValidLocations().contains(l.getLocation())) {
            board.drawLine(l);
            board.setMark(l.getLocation(), currentPlayer.getMark()); //TODO: check if this is necessary
            toggleTurn();
        } else {
            System.out.println("Invalid location"); //TODO: add exception
        }
    }

    /**
     * Toggles the turns of the players whenever a line is drawn.
     * If a player completes a box, they get another turn.
     */
    public void toggleTurn() {
        int currentScore = getTurn().getScore() + getOther().getScore();
        updateScores();
        int updatedScore = getTurn().getScore() + getOther().getScore();
        if (updatedScore == currentScore) {
            if (this.currentPlayer == player1) {
                currentPlayer = player2;
            } else {
                currentPlayer = player1;
            }
        }
    }

    public void updateScores() {
        for (int i = 0; i < board.getBoxes().length; i++) {
            if (board.getBoxes()[i].isComplete() && board.getBoxes()[i].getOwner() == null) {
                board.getBoxes()[i].setOwner(currentPlayer);
                currentPlayer.increaseScore();
            }
        }
    }

    /**
     * Retrieves the 2 players of the game.
     *
     * @return a list of the 2 players of the game.
     */
    public List<BasicPlayer> getPlayers() {
        List<BasicPlayer> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        return players;
    }

    public String toString() {
        return board.toString();
    }

    public DotsAndBoxesGame deepCopy() {
        DotsAndBoxesGame copy = new DotsAndBoxesGame(player1, player2);
        copy.board = board.deepCopy();
        copy.currentPlayer = currentPlayer;
        for (Box box : copy.board.getBoxes()) {
            box.setOwner(board.getBoxes()[box.getLocation()].getOwner());
        }
        return copy;
    }
}