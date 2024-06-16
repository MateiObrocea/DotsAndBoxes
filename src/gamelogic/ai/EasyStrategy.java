package gamelogic.ai;

import gamelogic.model.DotsAndBoxesGame;

import java.util.List;
import java.util.Random;

/**
 * Class that represents the easy strategy.
 */
public class EasyStrategy implements Strategy {
    private final String strategyName;

    /**
     * Constructor for the easy strategy.
     *
     * @param strategyName name of the strategy
     */
    public EasyStrategy(String strategyName) {
        this.strategyName = strategyName;
    }

    /**
     * Method that returns the name of the strategy.
     *
     * @return name of strategy
     */
    @Override
    public String getName() {
        return strategyName;
    }

    /**
     * Method that returns a random available location given the current game.
     *
     * @param game that is currently being played
     * @return random available location
     */
    /*@
        requires game != null;
        ensures game.getValidLocations().contains(\result) == true;
    */
    @Override
    public int computeLocation(DotsAndBoxesGame game) {
        List<Integer> validLocations = game.getValidLocations();
        Random random = new Random();
        return validLocations.get(random.nextInt(validLocations.size()));
    }
}
