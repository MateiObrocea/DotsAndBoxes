package helpers;

/**
 * Protocol class with constants for creating protocol messages.
 * Behaves like an enum.
 * Inspired by the Protocol class from Software Systems, week 7.
 */
public final class ServerProtocol {
    public static final String HELLO = "HELLO";
    public static final String LOGIN = "LOGIN";
    public static final String ALREADY_LOGGED_IN = "ALREADYLOGGEDIN";
    public static final String LIST = "LIST";
    public static final String NEW_GAME = "NEWGAME";
    public static final String MOVE = "MOVE";
    public static final String GAME_OVER = "GAMEOVER";
    public static final String VICTORY = "VICTORY";
    public static final String DRAW = "DRAW";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String ERROR = "ERROR";
    public static final String SEPARATOR = "~";

    private ServerProtocol() {
        // Private constructor to prevent instantiation
    }
}
