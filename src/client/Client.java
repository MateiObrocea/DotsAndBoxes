package client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for the client.
 * Responsible for sending and receiving messages from the server.
 */
public class Client {
    private final ClientConnection clientConnection;
    String username;
    private final Set<ClientListener> clients = new HashSet<>(); // set of listeners for the observer pattern.

    private boolean isQueued;

    public Client(InetAddress address, int port) throws IOException {
        clientConnection = new ClientConnection(address, port);
        clientConnection.setChatClient(this);
        clientConnection.start();
        isQueued = false;
    }

    /**
     * Returns whether the client is queued or not.
     *
     * @return true if the client is queued, false otherwise.
     */
    public boolean isQueued() {
        return isQueued;
    }

    /**
     * Setter for the isQueued variable.
     *
     * @param queued the value to be set.
     */
    public void setQueued(boolean queued) {
        isQueued = queued;
    }

    /**
     * Closes the connection.
     */
    public void close() {
        clientConnection.close();
    }

    /**
     * Delegates to the clientConnection to send the list command.
     */
    public void sendListCommand() {
        clientConnection.sendListCommand();
    }

    /**
     * Delegates to the clientConnection to send the queue command.
     */
    public void sendQueueCommand() {
        clientConnection.sendQueueCommand();
    }

    /**
     * Delegates to the clientConnection to send the move command.
     *
     * @param location the location to be sent.
     */
    public void sendMoveCommand(int location) {
        clientConnection.sendMoveCommand(location);
    }

    /**
     * Delegates to the clientConnection to send the login command.
     *
     * @param sentUsername the username to be sent.
     */
    public void sendLogInCommand(String sentUsername) {
        clientConnection.sendLogInCommand(sentUsername);
    }

    public void sendHelloCommand(String description) {
        clientConnection.sendHelloCommand(description);
    }

    /**
     * Delegates to the clientConnection to send an error command.
     *
     * @param error the error message to be sent.
     */
    public void sendErrorCommand(String error) {
        clientConnection.sendErrorCommand(error);
    }

    /**
     * Adds a listener to the client set.
     * This adheres to the observer pattern.
     *
     * @param clientListener the listener to be added to the list.
     */
    synchronized public void addListener(ClientListener clientListener) {
        clients.add(clientListener);
    }

    /**
     * removes the client listener.
     *
     * @param clientListener to be removed.
     */
    synchronized public void removeListener(ClientListener clientListener) {
        clients.remove(clientListener);
    }

    /**
     * Handles the create game event in the listener.
     *
     * @param name1 the name of the first player.
     * @param name2 the name of the second player.
     */
    public void receiveNewGame(String name1, String name2) {
        // Explanation of the for loop:
        // This was done in an attempt to apply the observer pattern.
        for (ClientListener c : clients) {
            c.createGame(name1, name2);
        }
    }

    /**
     * Handles the move event. in the listener.
     *
     * @param location the location of the move.
     */
    public void receiveMove(int location) {
        for (ClientListener c : clients) {
            c.receiveMove(location);
        }
    }

    /**
     * Handles the error event in the listener.
     */
    public void receiveError() {
        for (ClientListener c : clients) {
            c.receiveError();
        }
    }

    /**
     * Handles the game over event in the listener.
     *
     * @param reason the reason for the game over.
     * @param winner the winner of the game.
     */
    public void receiveGameOver(String reason, String winner) {
        for (ClientListener c : clients) {
            c.receiveGameOver(reason, winner);
        }
    }

    /**
     * Handles the list event in the listener.
     *
     * @param players the list of players.
     */
    public void receiveList(List<String> players) {
        for (ClientListener c : clients) {
            c.receiveList(players);
        }
    }

    /**
     * Handles the login event in the listener.
     */
    public void receiveLogin() {
        for (ClientListener c : clients) {
            c.receiveLogin();
        }
    }

    /**
     * Handles the already logged in event in the listener.
     */
    public void receiveAlreadyLoggedIn() {
        for (ClientListener c : clients) {
            c.receiveAlreadyLoggedIn();
        }
    }

    /**
     * Handles the hello event in the listener.
     */
    public void receiveHello() {
        for (ClientListener c : clients) {
            c.receiveHello();
        }
    }

    /**
     * Handles the disconnect event.
     * Gracefully closes the connection.
     */
    public void handleDisconnect() {
        System.out.println("Disconnected from the server.");
    }

    /**
     * Sets the username of the client.
     * This is used to identify the client on the client side.
     * On the server side the login command is used to set the username instead.
     *
     * @param username the username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the client.
     *
     * @return the username of the client.
     */
    public String getUsername() {
        return username;
    }
}
