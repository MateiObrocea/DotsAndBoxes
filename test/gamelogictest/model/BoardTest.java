package gamelogictest.model;

import gamelogic.exceptions.InvalidMoveException;
import gamelogic.exceptions.NonexistentBoxException;
import gamelogic.model.Board;
import gamelogic.model.Box;
import gamelogic.model.Line;
import gamelogic.model.Mark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Board.
 */
public class BoardTest {
    private Board board;

    /**
     * Create a new board before each test.
     */
    @BeforeEach
    void setup() {
        board = new Board();
    }

    /**
     * Test the number of lines on a board, based on the constant DIM.
     */
    @Test
    void testNumberOfLines() {
        assertEquals(24, board.calculateNrOfLines(3));
        assertEquals(40, board.calculateNrOfLines(4));
        assertEquals(60, board.calculateNrOfLines(5));
        assertEquals(84, board.calculateNrOfLines(6));
    }

    /**
     * Tests if the board is empty after setup.
     */
    @Test
    void testEmptyBoardSetup() {
        assertTrue(board.isEmpty());
    }

    /**
     * Tests if the board can set and get marks.
     */
    @Test
    void testSetAndGetMark() {
        board.setMark(0, Mark.X);
        board.setMark(5, Mark.O);
        board.setMark(2, Mark.EMPTY);
        board.setMark(board.calculateNrOfLines(Board.DIM) - 1, Mark.X);
        assertEquals(Mark.X, board.getMark(0));
        assertEquals(Mark.O, board.getMark(5));
        assertEquals(Mark.EMPTY, board.getMark(2));
        assertEquals(Mark.X, board.getMark(board.calculateNrOfLines(Board.DIM) - 1));
    }

    /**
     * Tests if the lines are null after reset, i.e, the board is empty.
     */
    @Test
    public void testReset() {
        board.drawLine(new Line(0));
        board.drawLine(new Line(board.calculateNrOfLines(Board.DIM) - 1));
        board.reset();
        assertNull(board.getLine(0));
        assertNull(board.getLine(board.calculateNrOfLines(Board.DIM) - 1));
        for (Box box : board.getBoxes()) {
            assertNull(box.getOwner());
        }
    }

    /**
     * Tests if the board can draw lines.
     */
    @Test
    public void testDrawLine() {
        board.drawLine(new Line(0));
        board.drawLine(new Line(5));
        board.drawLine(new Line(6));
        board.drawLine(new Line(7));
        assertEquals(0, board.getLine(0).getLocation());
        assertEquals(5, board.getLine(5).getLocation());
        assertEquals(6, board.getLine(6).getLocation());
        assertEquals(7, board.getLine(7).getLocation());
    }

    /**
     * Tests if after drawing lines in all possible locations, the board is full.
     */
    @Test
    public void testIsFull() {
        for (int i = 0; i < board.calculateNrOfLines(Board.DIM); i++) {
            board.drawLine(new Line(i));
        }
        assertTrue(board.isFull());
    }

    /**
     * Tests if isEmpty() works correctly.
     */
    @Test
    public void testIsEmpty() {
        boolean manualEmpty = true;
        for (int i = 0; i < board.calculateNrOfLines(Board.DIM); i++) {
            assertNull(board.getLine(i));
            if (board.getLine(i) != null) {
                manualEmpty = false;
            }
        }
        assertEquals(board.isEmpty(), manualEmpty);
    }

    /**
     * Tests if the board can determine whether a box is completed.
     * This test considers a dimension of size 5.
     */
    @Test
    public void testDetermineBox() throws NonexistentBoxException {
        int boxLocation1 = 0;
        assertFalse(board.determineBox(0));
        board.drawLine(new Line(boxLocation1));
        board.drawLine(new Line(boxLocation1 + Board.DIM));
        board.drawLine(new Line(boxLocation1 + Board.DIM + 1));
        board.drawLine(new Line(boxLocation1 + 2 * Board.DIM + 1));
        assertTrue(board.determineBox(0));
    }

    /**
     * Tests if the allowed indexes are correct.
     */
    @Test
    public void testIsField() {
        assertTrue(board.isField(0));
        assertTrue(board.isField(1));
        assertFalse(board.isField(-1));
        assertFalse(board.isField(1000));
        assertFalse(board.isField(6600));
    }

    /**
     * Test if the board can determine whether a specific field is a line.
     * This test considers a dimension of size 5.
     */
    @Test
    public void testIsLine() throws InvalidMoveException {
        board.drawLine(new Line(0));
        board.drawLine(new Line(board.calculateNrOfLines(Board.DIM) - 1));
        board.drawLine(new Line(2));
        assertTrue(board.isLine(0));
        assertTrue(board.isLine(board.calculateNrOfLines(Board.DIM) - 1));
        assertTrue(board.isLine(2));
        assertFalse(board.isLine(3));
        assertFalse(board.isLine(4));
    }

    /**
     * Tests if the association between boxes and lines is correct.
     * For example, box 0 has lines 0, 5, 6, 11; box 1 has lines 1, 6, 7, 12; box 2 has lines 2, 7, 8, 13; etc.
     */
    @Test
    public void testAssociateLinesWithBoxes() {
        int boxLocation1 = 0;
        board.drawLine(new Line(boxLocation1));
        board.drawLine(new Line(boxLocation1 + Board.DIM));
        board.drawLine(new Line(boxLocation1 + Board.DIM + 1));
        board.drawLine(new Line(boxLocation1 + 2 * Board.DIM + 1));
        int boxLocation2 = 3;
        board.drawLine(new Line(boxLocation2));
        board.drawLine(new Line(boxLocation2 + Board.DIM));
        board.drawLine(new Line(boxLocation2 + Board.DIM + 1));
        board.drawLine(new Line(boxLocation2 + 2 * Board.DIM + 1));
        assertEquals(boxLocation1, board.getBoxes()[boxLocation1].getLines()[0].getLocation());
        assertEquals(boxLocation1 + Board.DIM, board.getBoxes()[boxLocation1].getLines()[1].getLocation());
        assertEquals(boxLocation1 + Board.DIM + 1, board.getBoxes()[boxLocation1].getLines()[2].getLocation());
        assertEquals(boxLocation1 + 2 * Board.DIM + 1, board.getBoxes()[boxLocation1].getLines()[3].getLocation());
        assertEquals(boxLocation2, board.getBoxes()[boxLocation2].getLines()[0].getLocation());
        assertEquals(boxLocation2 + Board.DIM, board.getBoxes()[boxLocation2].getLines()[1].getLocation());
        assertEquals(boxLocation2 + Board.DIM + 1, board.getBoxes()[boxLocation2].getLines()[2].getLocation());
        assertEquals(boxLocation2 + 2 * Board.DIM + 1, board.getBoxes()[boxLocation2].getLines()[3].getLocation());
    }

    /**
     *  Tests if the board can determine whether a box is complete.
     *  This tests the method in the Box class, rather than the Board class.
     */
    @Test
    void testCompleteBox() {
        int boxLocation1 = 0;
        board.drawLine(new Line(boxLocation1));
        board.drawLine(new Line(boxLocation1 + Board.DIM));
        board.drawLine(new Line(boxLocation1 + Board.DIM + 1));
        board.drawLine(new Line(boxLocation1 + 2 * Board.DIM + 1));
        int boxLocation2 = 3;
        board.drawLine(new Line(boxLocation2));
        board.drawLine(new Line(boxLocation2 + Board.DIM));
        board.drawLine(new Line(boxLocation2 + Board.DIM + 1));
        board.drawLine(new Line(boxLocation2 + 2 * Board.DIM + 1));
        assertTrue(board.getBoxes()[boxLocation1].isComplete());
        assertTrue(board.getBoxes()[boxLocation2].isComplete());
    }

    /**
     * Tests that a board is copied properly.
     * Tests if the copy is not the same object as the original.
     */
    @Test
    public void testDeepCopy() {
        board.drawLine(new Line(0));
        board.drawLine(new Line(5));
        Board deepCopy = board.deepCopy();
        assertEquals(board.getLine(0).getLocation(), deepCopy.getLine(0).getLocation());
        assertEquals(board.getLine(5).getLocation(), deepCopy.getLine(5).getLocation());
        assertNotSame(board, deepCopy);
    }
}
