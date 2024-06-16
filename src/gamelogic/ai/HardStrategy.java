package gamelogic.ai;

import gamelogic.model.Board;
import gamelogic.model.Box;
import gamelogic.model.DotsAndBoxesGame;
import gamelogic.model.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class that represents the hard strategy.
 */
public class HardStrategy implements Strategy {
    private final String strategyName;

    /**
     * Constructor for HardStrategy.
     *
     * @param strategyName name of the Strategy.
     */
    public HardStrategy(String strategyName) {
        this.strategyName = strategyName;
    }

    /**
     * Method that returns list of all boxes that are one line away from being complete.
     *
     * @param game that is currently being played
     * @return List of boxes
     */
    /*@
        requires game != null;
        ensures \result != null;
    */
    public List<Box> boxesOneFromFull(DotsAndBoxesGame game) {
        List<Box> boxesOneFromFull = new ArrayList<>();
        for (Box box : game.board.getBoxes()) {
            Line[] lines = box.getLines();
            int counter = 0;
            for (int i = 0; i < 4; i++) {
                if (lines[i] != null) {
                    counter++;
                }
            }
            if (counter == 3) {
                boxesOneFromFull.add(box);
            }
        }
        return boxesOneFromFull;
    }

    /**
     * Method that returns the index of the side that is still empty, given that a box has 1 empty
     * side.
     *
     * @param box with 1 empty side
     * @return index of side
     */
    /*@
        requires box != null;
        ensures \result >= -1 && \result < 4;
    */
    public int getIndexEmptyLineBox(Box box) {
        int index = -1;
        Line[] lines = box.getLines();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Method that returns the index of the line based on the index of box and the index of the
     * side.
     *
     * @param boxLocation index of the box
     * @param index       of the side
     * @return index of the line
     */
    /*@
        requires boxLocation >= 0 && boxLocation < Board.DIM*Board.DIM && index >= 0 && index < 4;
        ensures \result >= 0;
    */
    public int getLocationLine(int boxLocation, int index) {
        int offset = boxLocation / Board.DIM;
        int lineLocation = 0;
        if (index == 0) {
            lineLocation = boxLocation + offset * (Board.DIM + 1);
        } else if (index == 1) {
            lineLocation = boxLocation + offset * (Board.DIM + 1) + 5;
        } else if (index == 2) {
            lineLocation = boxLocation + offset * (Board.DIM + 1) + 6;
        } else if (index == 3) {
            lineLocation = boxLocation + offset * (Board.DIM + 1) + 11;
        }
        return lineLocation;
    }

    /**
     * Method that returns the name of the strategy.
     *
     * @return name of strategy
     */
    /*@
        ensures \result != null;
        pure;
    */
    @Override
    public String getName() {
        return this.strategyName;
    }

    /**
     * Method that computes a next move. If a box can be captured this location will be given, if
     * not a random location will be given.
     *
     * @param game that is currently being played
     * @return a location of a valid move
     */
    /*@
        requires game != null;
        ensures game.getValidLocations().contains(\result);
        pure;
    */
    @Override
    public int computeLocation(DotsAndBoxesGame game) {
        List<Box> boxesOneFromFull = boxesOneFromFull(game);
        if (!boxesOneFromFull.isEmpty()) {
            Random random = new Random();
            Box box = boxesOneFromFull.get(random.nextInt(boxesOneFromFull.size()));
            return getLocationLine(box.getLocation(), getIndexEmptyLineBox(box));
        } else {
            List<Integer> validLocations = game.getValidLocations();
            Random random = new Random();
            return validLocations.get(random.nextInt(validLocations.size()));
        }
    }
}
