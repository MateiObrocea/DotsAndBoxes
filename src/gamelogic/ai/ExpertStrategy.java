package gamelogic.ai;

import gamelogic.model.Box;
import gamelogic.model.DotsAndBoxesGame;
import gamelogic.model.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class that represents the expert strategy.
 */
public class ExpertStrategy extends HardStrategy implements Strategy {
    private final String strategyName;

    /**
     * Constructor for HardStrategy.
     *
     * @param strategyName name of the Strategy.
     */
    public ExpertStrategy(String strategyName) {
        super(strategyName);
        this.strategyName = strategyName;
    }

    /**
     * Method that returns list of all boxes that are three line away from being complete. Then the
     * AI will use this to decide if he has to make a move that will give the opponent a point.
     *
     * @param game that is currently being played
     * @return List of boxes
     */
    /*@
        requires game != null;
        ensures \result != null;
    */
    public List<Box> boxesThreeFromFull(DotsAndBoxesGame game) {
        List<Box> boxesThreeFromFull = new ArrayList<>();
        for (Box box : game.board.getBoxes()) {
            Line[] lines = box.getLines();
            int counter = 0;
            for (int i = 0; i < 4; i++) {
                if (lines[i] != null) {
                    counter++;
                }
            }
            if (counter == 1) {
                boxesThreeFromFull.add(box);
            }
        }
        return boxesThreeFromFull;
    }

    /**
     * Method that returns list of all boxes that are four line away from being complete. Then the
     * AI will use this to decide if he has to make a move that will give the opponent a point.
     *
     * @param game that is currently being played
     * @return List of boxes
     */
    /*@
        requires game != null;
        ensures \result != null;
    */
    public List<Box> boxesFourFromFull(DotsAndBoxesGame game) {
        List<Box> boxesFourFromFull = new ArrayList<>();
        for (Box box : game.board.getBoxes()) {
            Line[] lines = box.getLines();
            int counter = 0;
            for (int i = 0; i < 4; i++) {
                if (lines[i] != null) {
                    counter++;
                }
            }
            if (counter == 0) {
                boxesFourFromFull.add(box);
            }
        }
        return boxesFourFromFull;
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
     * not a box that is the least full will be done as a move. If nothing is possible it will do a
     * random move.
     *
     * @param game that is currently being played
     * @return a location of a valid move
     */
    /*@
        requires game != null;
        ensures game.getValidLocations().contains(\result);
    */
    @Override
    public int computeLocation(DotsAndBoxesGame game) {
        Random random = new Random();
        List<Box> boxesOneFromFull = boxesOneFromFull(game);
        List<Box> boxesThreeFromFull = boxesThreeFromFull(game);
        List<Box> boxesFourFromFull = boxesFourFromFull(game);
        List<Integer> validLocations = game.getValidLocations();
        if (!boxesOneFromFull.isEmpty()) {
            Box box = boxesOneFromFull.get(random.nextInt(boxesOneFromFull.size()));
            return getLocationLine(box.getLocation(), getIndexEmptyLineBox(box));
        } else if (!boxesFourFromFull.isEmpty()) {
            Box box = boxesFourFromFull.get(random.nextInt(boxesFourFromFull.size()));
            return getLocationLine(box.getLocation(), getIndexEmptyLineBox(box));
        } else if (!boxesThreeFromFull.isEmpty()) {
            Box box = boxesThreeFromFull.get(random.nextInt(boxesThreeFromFull.size()));
            return getLocationLine(box.getLocation(), getIndexEmptyLineBox(box));
        } else {
            return validLocations.get(random.nextInt(validLocations.size()));
        }
    }
}
