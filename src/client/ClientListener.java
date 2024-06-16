package client;

import java.util.List;

/**
 * Interface for the client listener.
 * The client listener is responsible for handling the messages received from the server.
 */
public interface ClientListener {
    /**
     * Handles the connection loss event in the listener.
     * Removes the listener from the client.
     */
    void connectionLost();

    /**
     * Delegates to the TUI to create a game.
     *
     * @param name1 the name of the first player.
     * @param name2 the name of the second player.
     */
    void createGame(String name1, String name2);

    /**
     * Delegates to the TUI to receive a move.
     *
     * @param location the location of the move, received from the server.
     */
    void receiveMove(int location);

    /**
     * Delegates to the TUI to handle an error received from the server.
     * Virtually just prints the error, because the server removes the client anyway.
     */
    void receiveError();

    /**
     * Delegates to the TUI to handle the game over command.
     *
     * @param reason the reason for the game over, received from the server.
     * @param winner the winner of the game, received from the server.
     */
    void receiveGameOver(String reason, String winner);

    /**
     * Delegates to the TUI to handle the list command.
     *
     * @param players the list of players to be enlisted, received from the server.
     */
    void receiveList(List<String> players);

    /**
     * Delegates to the TUI to handle the login command.
     */
    void receiveLogin();

    /**
     * Delegates to the TUI to handle the already logged in command.
     */
    void receiveAlreadyLoggedIn();

    /**
     * Delegates to the TUI to handle the hello command.
     */
    void receiveHello();
}

