package server;

/**
 * A class which represents the handler for a client.
 */
public class ClientHandler {
    private final ServerConnection serverConnection;
    private String username;
    private final Server server;

    private ClientState state;

    public ClientHandler(ServerConnection serverConnection, Server chatServer) {
        this.serverConnection = serverConnection;
        this.server = chatServer;
        chatServer.addClient(this);
        this.state = ClientState.NEW;
    }

    // -- Receiving commands from the client -------------------------------

    /**
     * Receives a hello command from the client.
     */
    public void receiveHello() {
        server.handleHello(this);
    }

    /**
     * Receives a login command from the client and sets the username.
     *
     * @param receivedUsername the username received from the client.
     */
    public void receiveLogin(String receivedUsername) {
        server.handleLogin(this, receivedUsername);
        if (this.username != null) {
            this.username = receivedUsername;
        }
    }

    /**
     * Receives a list command from the client.
     */
    public void receiveList() {
        server.handleList(this);
    }

    /**
     * Receives a queue command from the client.
     */
    public void receiveQueue() {
        server.handleQueue(this);
    }

    /**
     * Receives a move command from the client.
     *
     * @param location the location of the move.
     */
    public void receiveMove(int location) {
        server.handleMove(this, location);

    }

    /**
     * Receives an error command from the client.
     */
    public void receiveError() {
        server.handleError();
    }

    /**
     * Sends a hello message to the client.
     */
    public void sendHello() {
        serverConnection.sendHello();
    }

    /**
     * Sends a login message to the client.
     */
    public void sendLogin() {
        serverConnection.sendLogin();
    }

    /**
     * Sends an already logged in message to the client.
     */
    public void sendAlreadyLoggedIn() {
        serverConnection.sendAlreadyLoggedIn();
    }

    /**
     * Sends a list of all the clients to the client.
     *
     * @param list the list of clients.
     */
    public void sendList(String list) {
        serverConnection.sendList(list);
    }

    /**
     * Sends a new game message to the client.
     *
     * @param player1 the name of the first player.
     * @param player2 the name of the second player.
     */
    public void sendNewGame(String player1, String player2) {
        serverConnection.sendNewGame(player1, player2);
    }

    /**
     * Sends a move message to the client.
     *
     * @param location the location of the move.
     */
    public void sendMove(int location) {
        serverConnection.sendMove(location);
    }

    /**
     * Sends a game over message to the client.
     *
     * @param reason the reason for the game over.
     * @param winner the winner of the game.
     */
    public void sendGameOver(String reason, String winner) {
        serverConnection.sendGameOver(reason, winner);
    }

    /**
     * Sends an error message to the client.
     * Disconnects the client from the server in case of an error.
     * The disconnection is done to prevent malicious clients from sending erroneous commands.
     *
     * @param errorMessage the error message.
     */
    public void sendError(String errorMessage) {
        serverConnection.sendError(errorMessage);
        handleDisconnect();
    }


    // other methods...

    /**
     * Sets the state of the client.
     *
     * @param state the state to be set.
     */
    public void setState(ClientState state) {
        this.state = state;
    }

    /**
     * Returns the state of the client.
     *
     * @return the state of the client.
     */
    public ClientState getState() {
        return state;
    }

    /**
     * @return this username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username of this client.
     *
     * @param username the username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Removes the client from the server.
     */
    public void handleDisconnect() {
        server.removeClient(this);
    }

}

