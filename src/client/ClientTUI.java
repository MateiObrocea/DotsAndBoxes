package client;

import java.util.List;

/**
 * Interface for the ClientTUI class.
 * Some javadoc is in the ClientTUI classes, as they detail the methods more.
 */
public interface ClientTUI {
    /**
     * Creates a new game with the given names.
     *
     * @param name1 the name of the first player
     * @param name2 the name of the second player
     */
    void createGame(String name1, String name2);

    /**
     * Runs the respective instantiation of the TUI.
     */
    void runTUI();

    /**
     * Sends a queue command to the server.
     */
    void doSendQueueCommand();

    /**
     * Sends an error command to the server.
     */
    void sendError(String error);

    /**
     * Sends a login command to the server.
     */
    void doSendLogInCommand();

    /**
     * Sends a hello command to the server.
     */
    void doSendHelloCommand();

    /**
     * Sends a move command to the server, if the client is in a game, and it is the client's turn.
     *
     * @param location the location of the move to be made.
     */
    void doSendMoveCommand(int location);

    /**
     * Sends a list command to the server.
     */
    void doSendListCommand();

    /**
     * Receives a game over message from the server.
     */
    void receiveGameOver(String reason, String winner);

    /**
     * Receives a move from the server.
     *
     * @param location the location of the move
     */
    void receiveMove(int location);

    /**
     * Receives a login message from the server.
     */
    void receiveLogin();

    /**
     * Receives an error message from the server.
     */
    void receiveError();

    /**
     * Receives the already logged in message from the server.
     */
    void receiveAlreadyLoggedIn();

    /**
     * Receives a list of players from the server.
     *
     * @param players the list of players
     */
    void receiveList(List<String> players);

    /**
     * Receives a hello message from the server.
     */
    void receiveHello();
}