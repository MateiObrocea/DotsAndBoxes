package server;

import helpers.ClientProtocol;
import helpers.ServerProtocol;
import networking.SocketConnection;

import java.io.IOException;
import java.net.Socket;

import static java.lang.Integer.parseInt;

/**
 * Class which establishes a connection with a client and handles the communication with that client.
 */

public class ServerConnection extends SocketConnection {

    ClientHandler clientHandler;

    protected ServerConnection(Socket socket) throws IOException {
        super(socket);
    }

    /**
     * Handles a message received from the client.
     * This method is called by the receiving thread.
     * Sends errors to the client if the command is not valid (i.e, not in the protocol or untimely).
     *
     * @param message the message received from the connection
     */
    @Override
    protected void handleMessage(String message) {
        System.out.println("Received message: " + message); // for debugging purposes
        String[] tokens = message.split(ServerProtocol.SEPARATOR);
        if (tokens.length > 0) { //if message non-empty
            String command = tokens[0];
            // handle the command, based on the protocol. The first token is the command.
            // Delegates the handling of the commands to the clientHandler.
            switch (command) {
                case ClientProtocol.HELLO:
                    clientHandler.receiveHello();
                    break;
                case ClientProtocol.LOGIN:
                    if (tokens.length > 1) {
                        clientHandler.receiveLogin(tokens[1]);
                    } else {
                        //Send error to prevent a null username.
                        clientHandler.sendError("no username provided");
                    }
                    break;
                case ClientProtocol.LIST:
                    clientHandler.receiveList();
                    break;
                case ClientProtocol.QUEUE:
                    clientHandler.receiveQueue();
                    break;
                case ClientProtocol.MOVE:
                    if (tokens.length == 2) {
                        try {
                            int location = parseInt(tokens[1]);
                            clientHandler.receiveMove(location);
                        } catch (NumberFormatException e) {
                            // Prevents the server from crashing when the client sends a non-integer move. Important!
                            clientHandler.sendError("HEHE GET COUNTERED----" + e.getMessage());
                            break;
                        }
                    } else {
                        clientHandler.sendError("move.");
                    }
                    break;
                case ClientProtocol.ERROR:
                    clientHandler.receiveError();
                    break;
                default:
                    clientHandler.sendError("Unknown command: " + command);
                    break;
            }
        }
    }

    // -- Sending commands to the client -------------------------------

    /**
     * Sends a hello message to the client.
     */
    public void sendHello() {
        super.sendMessage(ServerProtocol.HELLO + ServerProtocol.SEPARATOR + "Minor 14 - Server");
    }

    /**
     * Sends a login message to the client.
     */
    public void sendLogin() {
        super.sendMessage(ServerProtocol.LOGIN);
    }

    /**
     * Sends an already logged in message to the client.
     */
    public void sendAlreadyLoggedIn() {
        super.sendMessage(ServerProtocol.ALREADY_LOGGED_IN);
    }

    /**
     * Sends a list of all the clients to the client.
     *
     * @param list, the list of all the clients.
     */
    public void sendList(String list) {
        super.sendMessage(ServerProtocol.LIST + list);
    }

    /**
     * Sends a new game message to the client.
     *
     * @param player1, the name of the first player.
     * @param player2, the name of the second player.
     */
    public void sendNewGame(String player1, String player2) {
        super.sendMessage(ServerProtocol.NEW_GAME + ServerProtocol.SEPARATOR +
                player1 + ServerProtocol.SEPARATOR + player2);
    }

    /**
     * Sends a move message to the client.
     * Should be sent to both clients in the game.
     *
     * @param location, the location of the move.
     */
    public void sendMove(int location) {
        super.sendMessage(ServerProtocol.MOVE + ServerProtocol.SEPARATOR + location);
    }

    /**
     * Sends a game over message to the client.
     *
     * @param reason the reason for the game over, either victory or draw, as defined in the protocol.
     * @param winner the name of the winner, or null if the game is a draw.
     */
    public void sendGameOver(String reason, String winner) {
        super.sendMessage(ServerProtocol.GAME_OVER + ServerProtocol.SEPARATOR
                + reason + ServerProtocol.SEPARATOR + winner);
    }

    /**
     * Sends an error message to the client. Does NOT disconnect the client if the error is fatal.
     * Just informs the client of the error.
     *
     * @param errorMessage, the error message to be sent.
     */
    public void sendError(String errorMessage) {
        super.sendMessage(ServerProtocol.ERROR + ServerProtocol.SEPARATOR + errorMessage);
    }

    /**
     * Starts the connection.
     * This method is called by the receiving thread.
     */
    public void start() {
        super.start();
    }

    /**
     * Sets the clientHandler of this connection.
     *
     * @param clientHandler the clientHandler to be set.
     */
    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * Removes the clientHandler of this connection.
     */
    @Override
    protected void handleDisconnect() {
        clientHandler.handleDisconnect();
    }
}