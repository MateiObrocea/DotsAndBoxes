package helpers;

/**
 * Protocol class with constants for creating protocol messages.
 * Behaves like an enum.
 * Inspired by the Protocol class from Software Systems, week 7.
 */
public final class ClientProtocol {
    public static final String HELLO = "HELLO";
    public static final String LOGIN = "LOGIN";
    public static final String LIST = "LIST";
    public static final String QUEUE = "QUEUE";
    public static final String MOVE = "MOVE";
    public static final String ERROR = "ERROR";
    public static final String SEPARATOR = "~";


    private ClientProtocol() {
        // Private constructor to prevent instantiation
    }
}
