package gamelogic.model;

public class BoardVisualization {
    public Board board;
    private final int size;
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String BOLD = "\u001B[1m";
    public static final String V_LINE = "|";
    public static final String H_LINE = "--";
    public static final String FIELD_CORNER = "•";
    public static final String FIELD_EMPTY_SPACE = " ";

    /**
     * Constructor of the board visualizer that will be used in the TUI of the Human Player.
     *
     * @param board of the game currently being played. Used as reference.
     */
    public BoardVisualization(Board board) {
        this.board = board;
        this.size = Board.DIM * 4 + 1;
    }

    /**
     * Method that makes an empty array for all the Strings that are presents in the field.
     *
     * @return empty two-dimensional array
     */
    public String[][] createEmptyArray() {
        String[][] emptyBoard = new String[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                emptyBoard[row][column] = String.format("%3s", FIELD_EMPTY_SPACE);
            }
        }
        return emptyBoard;
    }

    /**
     * Method that fills the empty array with Strings, given the current state of the game. The main
     * approach of this method it that every box in the field has 5x5 space in the two-dimensional
     * board array. The border of this 5x5 field overlaps with an adjacent 5x5 field. See the
     * Example below for a playing board of DIM = 2:
     * ---------------------------
     * 0  1  2  3  4  5  6  7  8
     * 0 •  -  N  -  •  -  N  -  •
     * 1 |           |           |
     * 2 N     B     N     B     N
     * 3 |           |           |
     * 4 •  -  N  -  •  -  N  -  •
     * 5 |           |           |
     * 6 N     B     N     B     N
     * 7 |           |           |
     * 8 •  -  N  -  •  -  N  -  •
     * ---------------------------
     * N = the index of the line
     * B = the index of the box
     * • = the corner of a field
     * - = the horizontal line of a field
     * | = the vertical line of a field.
     * ---------------------------
     * The coordinates correspond to a location in the two-dimensional array. To build the board
     * systematically this method iterates through all coordinates in the two-dimensional array. For
     * every symbol there is a rule for where it should be placed in the field.
     * ---------------------------
     * "•" must be placed on the coordinates where the rows and columns index are even and the
     * column and row index is not congruent to 2 modulo 4
     * ---------------------------
     * "N" and "-" must be placed on the coordinates where the row index is even, the row index is
     * not congruent to 2 modulo 4 and column index is congruent to 2 modulo 4.
     * ---------------------------
     * "N" and "|" must be placed on the coordinates where the column index is even, the row index
     * is congruent to 2 modulo 4 and the column index is not congruent to 2 modulo 4.
     * ---------------------------
     * "B" must be placed on the coordinates where the row index is congruent to 2 modulo 4, the
     * column index is congruent to 2 modulo 4.
     * ---------------------------
     * Other fields are left empty.
     * ---------------------------
     * It is important that these symbols are checked in this order and these specific rules, since
     * these rules are checked using an if-else statement. Some rules overlap for symbols, but are
     * not placed, since the if-else statement is broken out of before this can be done.
     * ---------------------------
     *
     * @param emptyArray of board
     * @return filled array of board
     */
    public String[][] fillArray(String[][] emptyArray) {
        //Local fields for the current state of the board
        Box[] boxes = board.getBoxes();
        Mark[] marks = board.getMarks();
        //Counter to keep track of number the boxes and lines
        int counterLines = 0;
        int counterBoxes = 0;
        //Iterate through all coordinates.
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                // Place the field corner
                if (row % 2 == 0 && column % 2 == 0 && row % 4 != 2 && column % 4 != 2) {
                    emptyArray[row][column] = String.format("%3s", FIELD_CORNER);
                    // Place the horizontal lines
                } else if (row % 2 == 0 && row % 4 != 2 && column % 4 == 2) {
                    // If not captured, no color will be applied.
                    if (marks[counterLines] == Mark.EMPTY) {
                        emptyArray[row][column - 1] = String.format("%3s", H_LINE);
                        emptyArray[row][column] = String.format("%3s", counterLines);
                        emptyArray[row][column + 1] = String.format("%3s", H_LINE);
                    } else {
                        if (marks[counterLines] == Mark.X) {
                            // If captured by is X, red color is applied.
                            emptyArray[row][column - 1] = BOLD + RED + String.format("%3s",
                                    H_LINE) + RESET;
                            emptyArray[row][column] = BOLD + RED + String.format("%3s", H_LINE) + RESET;
                            emptyArray[row][column + 1] = BOLD + RED + String.format("%3s",
                                    H_LINE) + RESET;
                        } else {
                            // If captured by is O, blue color is applied.
                            emptyArray[row][column - 1] = BOLD + BLUE + String.format("%3s",
                                    H_LINE) + RESET;
                            emptyArray[row][column] = BOLD + BLUE + String.format("%3s", H_LINE) + RESET;
                            emptyArray[row][column + 1] = BOLD + BLUE + String.format("%3s",
                                    H_LINE) + RESET;
                        }
                    }
                    counterLines++;
                    // Place the horizontal lines
                } else if (column % 2 == 0 && row % 4 == 2 && column % 4 != 2) {
                    // If not captured, no color will be applied.
                    if (marks[counterLines] == Mark.EMPTY) {
                        emptyArray[row - 1][column] = String.format("%3s", V_LINE);
                        emptyArray[row][column] = String.format("%3s", counterLines);
                        emptyArray[row + 1][column] = String.format("%3s", V_LINE);
                    } else {
                        // If captured by is X, red color is applied.
                        if (marks[counterLines] == Mark.X) {
                            emptyArray[row - 1][column] = BOLD + RED + String.format("%3s",
                                    V_LINE) + RESET;
                            emptyArray[row][column] = BOLD + RED + String.format("%3s", V_LINE) + RESET;
                            emptyArray[row + 1][column] = BOLD + RED + String.format("%3s",
                                    V_LINE) + RESET;
                            // If captured by is O, blue color is applied.
                        } else {
                            emptyArray[row - 1][column] = BOLD + BLUE + String.format("%3s",
                                    V_LINE) + RESET;
                            emptyArray[row][column] = BOLD + BLUE + String.format("%3s", V_LINE) + RESET;
                            emptyArray[row + 1][column] = BOLD + BLUE + String.format("%3s",
                                    V_LINE) + RESET;
                        }
                    }
                    counterLines++;
                    // Place mark of the box.
                } else if (row % 4 == 2 && column % 4 == 2) {
                    if (boxes[counterBoxes].getOwner() != null) {
                        // If captured by is X, red color is applied.
                        if (boxes[counterBoxes].getOwner().mark == Mark.X) {
                            emptyArray[row][column] = BOLD + RED + String.format("%3s",
                                    boxes[counterBoxes].getOwner().mark) + RESET;
                            // If captured by is O, blue color is applied.
                        } else {
                            emptyArray[row][column] = BOLD + BLUE + String.format("%3s",
                                    boxes[counterBoxes].getOwner().mark) + RESET;
                        }
                        // If empty, place empty field space
                    } else {
                        emptyArray[row][column] = String.format("%3s", FIELD_EMPTY_SPACE);
                    }
                    counterBoxes++;
                }
            }
        }
        return emptyArray;
    }

    /**
     * Method that converts the filled array to a string with correct format and equal spacing.
     *
     * @param filledArray of the current state of the game
     * @return string of the current state of the game
     */
    public String arrayToSting(String[][] filledArray) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < filledArray.length; i++) {
            for (int j = 0; j < filledArray[i].length; j++) {
                result.append(filledArray[i][j]);
            }
            if (i < filledArray.length - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    /**
     * Method that returns the current state of the board as a string.
     *
     * @return string of current state of the board
     */
    public String toString() {
        return arrayToSting(fillArray(createEmptyArray()));
    }
}