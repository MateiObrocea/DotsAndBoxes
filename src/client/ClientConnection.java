package client;

import helpers.ClientProtocol;
import helpers.ServerProtocol;
import networking.SocketConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ClientConnection class.
 * Responsible for receiving and sending the commands to the server in the background.
 */
public class ClientConnection extends SocketConnection {

    Client client;

    // -- Constructor -----------------------------------------------

    public ClientConnection(InetAddress address, int port) throws IOException {
        super(new Socket(address, port));
    }

    // -- Networking Commands ---------------------------------------

    /**
     * Setter for the client.
     *
     * @param client the client to be set.
     */
    public void setChatClient(Client client) {
        this.client = client;
    }

    public void start() {
        super.start();
    }

    public void close() {
        super.close();
    }

    @Override
    protected void handleMessage(String message) {
        String[] tokens = message.split(ClientProtocol.SEPARATOR);
        if (tokens.length > 0) {
            String command = tokens[0];
            switch (command) {
                case ServerProtocol.HELLO:
                    client.receiveHello();
                    break;
                case ServerProtocol.LOGIN:
                    client.receiveLogin();
                    break;
                case ServerProtocol.ALREADY_LOGGED_IN:
                    client.receiveAlreadyLoggedIn();
                    break;
                case ServerProtocol.LIST:
                    List<String> players = new ArrayList<>(Arrays.asList(tokens).subList(1, tokens.length));
                    client.receiveList(players);
                    break;
                case ServerProtocol.NEW_GAME:
                    if (tokens.length > 2) {
                        client.receiveNewGame(tokens[1], tokens[2]);
                    } else {
                        client.sendErrorCommand("no username provided");
                    }
                    break;
                case ServerProtocol.MOVE:
                    if (tokens.length > 1) {
                        client.receiveMove(Integer.parseInt(tokens[1]));
                        break;
                    }
                    break;
                case ServerProtocol.GAME_OVER:
                    if (tokens.length > 2) {
                        client.receiveGameOver(tokens[1], tokens[2]);
                    }
                    break;
                case ServerProtocol.ERROR:
                    client.receiveError();
                    break;
                default:
                    client.sendErrorCommand("Unknown command: " + command);
                    break;
            }
        }
    }

    /**
     * Handles the event of the server disconnecting.
     */
    @Override
    protected void handleDisconnect() {
        client.handleDisconnect();
    }

    // -- Commands before handshake ---------------------------------

    /**
     * Sends the login command to the server.
     *
     * @param username the username to be sent and set on the server side.
     */
    public void sendLogInCommand(String username) {
        super.sendMessage(ClientProtocol.LOGIN + ClientProtocol.SEPARATOR + username);
    }

    /**
     * Sends the hello command to the server.
     *
     * @param description the description to be sent to the server.
     */
    public void sendHelloCommand(String description) {
        super.sendMessage(ClientProtocol.HELLO + ClientProtocol.SEPARATOR + description);
    }

    // -- Commands after handshake ----------------------------------


    /**
     * Requests the list of clients from the server.
     * The clients are the already logged-in users.
     */
    public void sendListCommand() {
        super.sendMessage(ClientProtocol.LIST);
    }

    /**
     * Requests the server to queue the client up for a game.
     */
    public void sendQueueCommand() {
        super.sendMessage(ClientProtocol.QUEUE);
    }

    /**
     * Sends the move command to the server.
     *
     * @param location the location to be sent.
     */
    public void sendMoveCommand(int location) {
        super.sendMessage(ClientProtocol.MOVE + ClientProtocol.SEPARATOR + location);
    }

    /**
     * Sends the error command to the server.
     *
     * @param error the error message to be sent.
     */
    public void sendErrorCommand(String error) {
        super.sendMessage(ClientProtocol.ERROR + ClientProtocol.SEPARATOR + error);
    }
}
