package gamelogictest.ai;

import gamelogic.ai.HardStrategy;
import gamelogic.model.BasicPlayer;
import gamelogic.model.DotsAndBoxesGame;
import gamelogic.model.Line;
import gamelogic.model.Mark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for HardStrategy.
 */
public class HardStrategyTest {
    DotsAndBoxesGame game;
    HardStrategy strategy;

    @BeforeEach
    public void setUp() {
        game = new DotsAndBoxesGame(new BasicPlayer("Dillon", Mark.X),
                new BasicPlayer("Matei", Mark.O));
        strategy = new HardStrategy("Hard");
    }

    /**
     * Test the name of the strategy.
     */
    @Test
    void testSetup() {
        assertEquals("Hard", strategy.getName());
    }

    /**
     * Test the empty state of the strategy.
     */
    @Test
    void testBoxesOneFromFullEmpty() {
        assertTrue(strategy.boxesOneFromFull(game).isEmpty());
    }

    /**
     * Test that the algorithm can detect when a box is one line away from being full.
     */
    @Test
    void testBoxesOneFromFull() {
        game.drawLine(new Line(0));
        game.drawLine(new Line(5));
        game.drawLine(new Line(6));
        assertEquals(1, strategy.boxesOneFromFull(game).size());
        assertEquals(0, strategy.boxesOneFromFull(game).get(0).getLocation());
    }

    /**
     * Test that the algorithm can detect which line to draw to complete a box.
     */
    @Test
    void testGetIndexEmptyLineBox() {
        game.drawLine(new Line(13));
        game.drawLine(new Line(19));
        game.drawLine(new Line(24));
        assertEquals(1, strategy.getIndexEmptyLineBox(strategy.boxesOneFromFull(game).get(0)));
    }


}
