package gamelogic.model;

import gamelogic.exceptions.InvalidMoveException;
import gamelogic.exceptions.NonexistentBoxException;

import java.util.Arrays;

/**
 * Board of the Dots and Boxes game.
 */
public class Board {
    public static final int DIM = 5;
    private /*@ spec_public */ Box[] boxes = new Box[DIM * DIM];
    private /*@ spec_public */ Mark[] marks;
    private /*@ spec_public */ Line[] lines = new Line[calculateNrOfLines(DIM)];
    private BoardVisualization boardVisualization;
    /*@
     public invariant lines.length == calculateNrOfLines(DIM);
     public invariant (\num_of int i; 0 <= i && i < lines.length; (lines[i] != null)) <= calculateNrOfLines(DIM);
     public invariant (\num_of int i; 0 <= i && i < boxes.length; (boxes[i].getOwner() != null)) <= DIM * DIM;
     public invariant (\num_of int i; 0 <= i && i < lines.length; (marks[i] == Mark.O)) <= calculateNrOfLines(DIM) / 2;
     public invariant (\num_of int i; 0 <= i && i < lines.length; (marks[i] == Mark.X)) <= calculateNrOfLines(DIM) / 2;
     @*/

    // -- Constructor -----------------------------------------------

    /**
     * Creates an empty board.
     */
    //@ ensures (\forall int i; (i >= 0 && i < lines.length); lines[i] == null);
    //@ ensures (\forall int i; (i >= 0 && i < boxes.length); boxes[i].getOwner() == null);
    public Board() {
        marks = new Mark[calculateNrOfLines(DIM)];
        Arrays.fill(marks, Mark.EMPTY);
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new Box(i);
        }
        boardVisualization = new BoardVisualization(this);
        this.setBoardVisualization(boardVisualization);
    }

    // -- Methods -----------------------------------------------

    /**
     * Retrieves the boxes of the board.
     *
     * @return the array of boxes.
     */
    public Box[] getBoxes() {
        return this.boxes;
    }

    /**
     * Retrieves the marks of the board.
     *
     * @return the array of marks.
     */
    public Mark[] getMarks() {
        return marks;
    }

    /**
     * Associates the board with a board visualization.
     *
     * @param boardVisualization, the board visualizer.
     */
    public void setBoardVisualization(BoardVisualization boardVisualization) {
        this.boardVisualization = boardVisualization;
    }

    /**
     * Establishes when a game is over.
     *
     * @return the game over state.
     */
    public boolean gameOver() {
        return isFull();
    }

    /**
     * Associates the lines with the boxes.
     * There are 25 boxes and 60 lines, and this method associates 4 lines with each box.
     * For example, box 0 has lines 0, 5, 6, 11; box 1 has lines 1, 6, 7, 12;box 2 has lines 2, 7, 8, 13; etc.
     */
    public void associateLinesWithBoxes() {
        int counter = 0;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) { // splits the boxes into a 5 by 5 grid of indices.
                /*
                Explanation:
                Each box should have 4 lines associated with it as follows:
                    - the top line is at the index x
                    - the left line is at the index x + DIM (5)
                    - the right line is at the index x + DIM + 1 (6)
                    - and the bottom line is at the index x + 2 * DIM + 1 (11)
                    - Abstractly, x is 2 * the row of the box + the column of the box +
                    a counter. The counter is necessary because per row there are DIM horizontal lines
                                                                              and DIM + 1 vertical lines.
                */
                Line[] fourLines = new Line[4];
                fourLines[0] = lines[2 * i * DIM + j + counter];
                fourLines[1] = lines[2 * i * DIM + j + counter + DIM];
                fourLines[2] = lines[2 * i * DIM + j + counter + DIM + 1];
                fourLines[3] = lines[2 * i * DIM + j + counter + 2 * DIM + 1];
                boxes[i * DIM + j].setLines(fourLines);
            }
            counter++;
        }
    }

    /**
     * Calculates the number of lines based on a given dimension.
     *
     * @param dim the given dimension.
     * @return the resulted number of possible lines to be drawn.
     */
    //@ requires dim > 0;
    //@ ensures \result == 2 * dim * (dim + 1);
    //@ pure
    public int calculateNrOfLines(int dim) { //Added a parameter instead of using the DIM constant directly for testing
        // because there are dim horizontal lines for each row and dim+1 rows: so (dim+1) * dim for the horizontal lines
        // and dim vertical lines for each column and dim+1 columns, so dim * dim+1 vertical lines
        // so in total dim * (dim+1) lines
        return 2 * dim * (dim + 1);
    }

    /**
     * Performs a deep copy of the board, with all its fields.
     */
    //@ ensures \result != this;
    //@ ensures (\forall int i; (i >= 0 && i < lines.length); \result.lines[i] != this.lines[i]);
    //@ ensures (\forall int i; (i >= 0 && i < boxes.length); \result.boxes[i] != this.boxes[i]);
    //@ ensures (\forall int i; (i >= 0 && i < marks.length); \result.marks[i] != this.marks[i]);
    //@ pure
    public Board deepCopy() {
        Line[] copiedLines = new Line[lines.length];
        Box[] copiedBoxes = new Box[boxes.length];
        Mark[] copiedMarks = new Mark[marks.length];
        for (int i = 0; i < copiedLines.length; i++) {
            if (lines[i] != null) {
                copiedLines[i] = new Line(lines[i].getLocation());
            }
        }
        for (int i = 0; i < copiedBoxes.length; i++) {
            if (boxes[i] != null) {
                copiedBoxes[i] = new Box(i);
            }
        }
        Board copyBoard = new Board();
        copyBoard.lines = copiedLines;
        copyBoard.boxes = copiedBoxes;
        copyBoard.marks = copiedMarks;
        return copyBoard;
    }

    /**
     * Checks whether an index is valid.
     *
     * @param index the index in the array.
     */
    //@ pure
    public boolean isField(int index) {
        return index >= 0 && index < lines.length;
    }

    /**
     * Checks whether there is a line at a specific index.
     *
     * @param index the index of the line.
     * @throws InvalidMoveException if the index is not valid.
     */
    //@ requires isField(index);
    //@ ensures (lines[index] != null) ==> \result == true;
    //@ ensures (lines[index] == null) ==> \result == false;
    public boolean isLine(int index) throws InvalidMoveException {
        if (!isField(index)) {
            throw new InvalidMoveException();
        }
        return lines[index] != null;
    }

    /**
     * Sets a specific mark at a specific location.
     *
     * @param index of the mark, corresponding with the line.
     * @param mark  the mark at that specific location.
     */
    public void setMark(int index, Mark mark) {
        marks[index] = mark;
    }

    /**
     * Draws a line at a specific index.
     * It also associates the line with the boxes every time a line is drawn.
     *
     * @param l the line to be drawn.
     */
    //@ requires l != null;
    //@ requires isField(l.getLocation());
    // ensures that each lines is associated with a certain box.
    //@ ensures ((\forall int i; (i >= 0 && i < boxes.length); (\exists int j ; j >= 0 && j <= boxes[i].getLines().length; boxes[i].getLines()[j] == l)));
    public void drawLine(Line l) {
        //every time a line is drawn, a new line object (constructed with the caller) will be passed as parameter.
        //and the line object will be added to the lines array at the index of the location of the line
        lines[l.getLocation()] = l;
        associateLinesWithBoxes(); //important to do this after drawing a line.
    }

    /**
     * Query for getting a line at a specific index.
     *
     * @param index of the line to be retrieved.
     * @return the line.
     */
    //@ requires isField(index);
    public Line getLine(int index) {
        return lines[index];
    }

    /**
     * Query for getting a mark at a specific index.
     *
     * @param index of the mark to be retrieved.
     * @return the mark.
     */
    //@ requires isField(index);
    public Mark getMark(int index) {
        return marks[index];
    }

    /**
     * Checks whether the board is full.
     *
     * @return the full condition of the board.
     */
    //@ ensures (\forall int i; (i >= 0 && i < lines.length); lines[i] != null) ==> \result == true;
    //@ ensures (\exists int i; (i >= 0 && i < lines.length); lines[i] == null) ==> \result == false;
    public boolean isFull() {
        for (Line line : lines) {
            if (line == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the board is empty.
     *
     * @return the empty condition of the board.
     */
    //@ ensures (\forall int i; (i >= 0 && i < lines.length); lines[i] == null) ==> \result == true;
    //@ ensures (\exists int i; (i >= 0 && i < lines.length); lines[i] != null) ==> \result == false;
    public boolean isEmpty() {
        for (Line line : lines) {
            if (line != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a box is completed, i.e., all its 4 lines are drawn.
     *
     * @param boxIndex the index of the box.
     * @throws NonexistentBoxException if the index of the box is not valid.
     */
    //@ requires isField(boxIndex);
    //@ ensures boxes[boxIndex].isComplete() ==> \result == true;
    public boolean determineBox(int boxIndex) throws NonexistentBoxException {
        if (boxIndex < 0 || boxIndex >= boxes.length) {
            throw new NonexistentBoxException(); // in case the box does not exist
        }
        return boxes[boxIndex].isComplete();
    }

    /**
     * Resets the board to its initial state, e.g. no lines, no marks, no box owners.
     */
    //@ ensures (\forall int i; (i >= 0 && i < lines.length); lines[i] == null);
    //@ ensures (\forall int i; (i >= 0 && i < marks.length); marks[i] == Mark.EMPTY);
    //@ ensures (\forall int i; (i >= 0 && i < boxes.length); boxes[i].getOwner() == null);
    public void reset() {
        Arrays.fill(marks, Mark.EMPTY);
        Arrays.fill(lines, null);
        for (Box box : boxes) {
            box.setOwner(null);
        }
    }

    /**
     * Displays the board in the TUI.
     *
     * @return a string of the board;
     */
    public String toString() {
        return boardVisualization.toString();
    }

}
