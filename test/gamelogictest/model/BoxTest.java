package gamelogictest.model;

import gamelogic.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test class for Box.
 */
public class BoxTest {
    Box[] boxes = new Box[Board.DIM * Board.DIM];

    /**
     * Create a new array of boxes before each test.
     */
    @BeforeEach
    void setUp() {
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new Box(i);
        }
    }

    /**
     * Tests that all the boxes are incomplete and have the correct location.
     */
    @Test
    void testSetup() {
        for (int i = 0; i < boxes.length; i++) {
            assertEquals(i, boxes[i].getLocation());    // test location
            assertFalse(boxes[i].isComplete());
        }
    }

    /**
     * Tests that the owner of the box is assigned correctly.
     */
    @Test
    void testSetAndGetOwner() {
        BasicPlayer player = new BasicPlayer("test", Mark.EMPTY);
        boxes[0].setOwner(player);
        assertEquals(player, boxes[0].getOwner());
        boxes[boxes.length - 1].setOwner(player);
        assertEquals(player, boxes[boxes.length - 1].getOwner());
    }

    /**
     * Tests that the 4 lines can be assigned to a box.
     * Tailored to the 5x5 board.
     */
    @Test
    void testSetAndGetLines() {
        Line[] lines = new Line[4];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new Line(i);
        }
        boxes[0].setLines(lines);
        boxes[1].setLines(lines);
        boxes[boxes.length - 1].setLines(lines);
        assertEquals(lines, boxes[0].getLines());
        assertEquals(lines, boxes[1].getLines());
        assertEquals(lines, boxes[boxes.length - 1].getLines());
    }
}
