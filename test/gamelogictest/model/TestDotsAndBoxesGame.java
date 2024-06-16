package gamelogictest.model;

import gamelogic.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the game class of Dots and Boxes.
 * Tests the methods of the class.
 * Also tests whether a game can be played from start to finish and that the states of the game are correct
 * throughout the entire run.
 */
public class TestDotsAndBoxesGame {
    BasicPlayer player1;
    BasicPlayer player2;
    DotsAndBoxesGame game;

    /**
     * Creates 2 humans 2 players to play the game.
     * Create a new game before each test, with the 2 tests.
     */
    @BeforeEach
    void setUp() {
        player1 = new BasicPlayer("Matei", Mark.X);
        player2 = new BasicPlayer("Dillon", Mark.O);
        game = new DotsAndBoxesGame(player1, player2);
    }

    /**
     * Tests the setup of the game.
     * Tests if the initial states of the game are correct.
     */
    @Test
    void testSetup() {
        assertEquals("Matei", player1.getName());
        assertEquals("Dillon", player2.getName());
        assertEquals(Mark.X, player1.getMark());
        assertEquals(Mark.O, player2.getMark());
        assertEquals(0, player1.getScore());
        assertEquals(0, player2.getScore());
        assertTrue(game.board.isEmpty());
    }

    /**
     * Testing a game over condition.
     * After all 60 lines are drawn, the game should be over.
     */
    @Test
    void testGameOver() {
        for (int i = 0; i < game.board.calculateNrOfLines(Board.DIM); i++) {
            assertFalse(game.isGameOver());
            game.drawLine(new Line(i));
        }
        assertTrue(game.isGameOver());
    }

    /**
     * Tests the deep copy method.
     * Tests if the states of game have been correctly copied to the copy.
     * Tests if the copy is not the same object as the original.
     */
    @Test
    void testDeepCopy() {
        game.drawLine(new Line(0));
        game.drawLine(new Line(5));
        game.drawLine(new Line(6));
        game.drawLine(new Line(11));
        DotsAndBoxesGame game2 = game.deepCopy();
        assertEquals(game.getTurn(), game2.getTurn());
        assertEquals(game.getOther(), game2.getOther());
        assertEquals(game.getTurn().getScore(), game2.getTurn().getScore());
        assertEquals(game.getOther().getScore(), game2.getOther().getScore());
        assertEquals(game.getValidLocations(), game2.getValidLocations());
        for (Box box : game2.board.getBoxes()) {
            assertEquals(box.getOwner(), game.board.getBoxes()[box.getLocation()].getOwner());
        }
        assertNotSame(game, game2);
    }

    /**
     * Tests if the game correctly toggles the turns of the players.
     */
    @Test
    void testTurns() {
        assertEquals(player1, game.getTurn());
        assertEquals(player2, game.getOther());
        game.drawLine(new Line(0));
        assertEquals(player2, game.getTurn());
        assertEquals(player1, game.getOther());
        game.drawLine(new Line(0));
        game.drawLine(new Line(0));
        assertEquals(player2, game.getTurn());
        assertEquals(player1, game.getOther());
    }

    /**
     * Tests if the game correctly updates the scores of the players.
     * The score of a player should increase only by 1 with each completed box.
     */
    @Test
    void testScores() {
        int boxLocation1 = 0;
        game.drawLine(new Line(boxLocation1));
        game.drawLine(new Line(boxLocation1 + Board.DIM));
        game.drawLine(new Line(boxLocation1 + Board.DIM + 1));
        game.drawLine(new Line(boxLocation1 + 2 * Board.DIM + 1));
        assertEquals(1, player2.getScore());
        assertEquals(0, player1.getScore());
        int boxLocation2 = 3;
        game.drawLine(new Line(boxLocation2));
        game.drawLine(new Line(boxLocation2 + Board.DIM));
        game.drawLine(new Line(boxLocation2 + Board.DIM + 1));
        game.drawLine(new Line(boxLocation2 + 2 * Board.DIM + 1));
        assertEquals(1, player2.getScore());
        assertEquals(1, player1.getScore());
    }

    /**
     * Tests if the game correctly determines the winner.
     * The winner is the player with the highest score.
     */
    @Test
    void testWinner() {
        for (int i = 0; i < game.board.calculateNrOfLines(Board.DIM); i++) {
            assertFalse(game.isGameOver());
            game.drawLine(new Line(i));
        }
        // With this for loop, player 2 will end up with a higher score.
        assertEquals(player2, game.getWinner());
    }

    /**
     * Tests if the game correctly determines the valid locations.
     * The valid locations are the locations of the lines that have not been drawn yet.
     * After a line is drawn, it should no longer be a valid location.
     */
    @Test
    void testValidMoves() {
        for (int i = 0; i < game.board.calculateNrOfLines(Board.DIM); i++) {
            assertTrue(game.getValidLocations().contains(i));
            game.drawLine(new Line(i));
            assertEquals(game.board.calculateNrOfLines(Board.DIM) - i - 1, game.getValidLocations().size());
        }
    }

    /**
     * Test if a move is performed correctly, i.e., the line is drawn.
     */
    @Test
    void testDrawLine() {
        game.drawLine(new Line(0));
        game.drawLine(new Line(5));
        game.drawLine(new Line(6));
        game.drawLine(new Line(11));
        assertEquals(0, game.board.getLine(0).getLocation());
        assertEquals(5, game.board.getLine(5).getLocation());
        assertEquals(6, game.board.getLine(6).getLocation());
        assertEquals(11, game.board.getLine(11).getLocation());
    }

    /**
     * Tests if the game correctly resets the game.
     * After a reset, the game and its fields should be in the same state as when it was created.
     * This test considers a dimension of size 5.
     */
    @Test
    void testReset() {
        game.drawLine(new Line(0));
        game.drawLine(new Line(Board.DIM));
        game.drawLine(new Line(Board.DIM + 1));
        game.drawLine(new Line(2 * Board.DIM + 1));
        assertEquals(player2, game.board.getBoxes()[0].getOwner());
        game.reset();
        assertNull(game.board.getBoxes()[0].getOwner());
        assertEquals(0, player1.getScore());
        assertEquals(0, player2.getScore());
        for (int i = 0; i < game.board.calculateNrOfLines(Board.DIM); i++) {
            assertNull(game.board.getLine(i));
        }
        assertTrue(game.board.isEmpty());
    }

    @Test
    void testBoxesAndScores() {
        List<Box> completedBoxes = new ArrayList<>();
        for (int i = 0; i < game.board.calculateNrOfLines(Board.DIM); i++) {
            //the total score should be equal to the number of completed boxes, which indicate
            //that the score is correctly linked to the boxes.
            assertEquals(game.getTurn().getScore() + game.getOther().getScore(), completedBoxes.size());
            game.drawLine(new Line(i));
            for (Box box : game.board.getBoxes()) {
                if (box.isComplete() && !completedBoxes.contains(box)) {
                    completedBoxes.add(box);
                }
            }
        }
    }

    /**
     * Integration test (of most methods), testing a random play of a full game from start to
     * finish including checking the game over condition.
     * At each step, test the following conditions:
     * - the game is over
     * - once a valid line is drawn, it is no longer valid.
     * - the game is over when all lines are drawn.
     * - test that the scores of the players
     * - test that the boxes are correctly linked.
     */
    @Test
    void testFullRun() {
        Random random = new Random(); //to generate a random number
        int randomInt;
        List<Box> completedBoxes = new ArrayList<>(); //to be populated with the completed boxes.
        while (!game.isGameOver()) {
            //generate a random location between 0 and the number of lines on the board.
            randomInt = random.nextInt(game.board.calculateNrOfLines(Board.DIM));
            if (game.getValidLocations().contains(randomInt)) {
                Line l = game.getTurn().determineLine(randomInt);
                //Check if the completed boxes and scores are correctly linked.
                assertEquals(game.getTurn().getScore() + game.getOther().getScore(), completedBoxes.size());
                game.drawLine(l);//draw the valid line
                //Check if the location is no longer valid at each step.
                assertFalse(game.isValidLocation(randomInt));
                for (Box box : game.board.getBoxes()) {
                    if (box.isComplete() && !completedBoxes.contains(box)) {
                        completedBoxes.add(box);
                    }
                }
            }
        }
        //Check if the game is over when all the lines are drawn.
        assertTrue(game.isGameOver());
        //Check if the scores of the players amount to the maximum number of boxes.
        assertEquals(game.getTurn().getScore() + game.getOther().getScore(), game.board.getBoxes().length);
        System.out.println(game);
    }
}
