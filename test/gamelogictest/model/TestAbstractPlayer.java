package gamelogictest.model;

import gamelogic.model.BasicPlayer;
import gamelogic.model.Mark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the abstract class AbstractPlayer.
 */
public class TestAbstractPlayer {
    BasicPlayer basicPlayer0;

    /**
     * Create a new abstract player before each test.
     */
    @BeforeEach
    public void setUp() {
        basicPlayer0 = new BasicPlayer("test", Mark.O);
    }

    @Test
    void testSetup() {
        assertEquals("test", basicPlayer0.getName());
        assertEquals("test", basicPlayer0.toString());
        assertEquals(Mark.O, basicPlayer0.getMark());
        assertEquals(0, basicPlayer0.getScore());
    }

    @Test
    void testScore() {
        for (int i = 0; i < 10; i++) {
            basicPlayer0.increaseScore();
        }
        assertEquals(10, basicPlayer0.getScore());
        basicPlayer0.increaseScore();
        assertEquals(11, basicPlayer0.getScore());
        basicPlayer0.setScore(2);
        assertEquals(2, basicPlayer0.getScore());
    }

    @Test
    void testMark() {
        assertEquals(Mark.O, basicPlayer0.getMark());
    }
}
