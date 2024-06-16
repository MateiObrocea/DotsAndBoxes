package server;

import helpers.ServerProtocol;
import networking.SocketServer;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

/**
 * Implements a server for the Dots and Boxes game which accepts connection from clients.
 * Inspired from the classes from Software Systems, week 7.
 */
public class Server extends SocketServer {

    private final Set<ClientHandler> clients = new HashSet<>();
    private final GameHandler gameHandler;

    /**
     * Constructs a new ChatServer.
     *
     * @param port the port to listen on
     * @throws IOException if the server socket cannot be created, for example, because the port is already bound.
     */
    public Server(int port) throws IOException {
        super(port);
        gameHandler = new GameHandler();
    }

    /**
     * Returns the port on which this server is listening for connections.
     *
     * @return the port on which this server is listening for connections
     */
    @Override
    public int getPort() {
        return super.getPort();
    }

    /**
     * Accepts connections and starts a new thread for each connection.
     * This method will block until the server socket is closed, for example by invoking closeServerSocket.
     *
     * @throws IOException if an I/O error occurs when waiting for a connection
     */
    @Override
    public void acceptConnections() throws IOException {
        super.acceptConnections();
    }

    /**
     * Closes the server socket. This will cause the server to stop accepting new connections.
     * If called from a different thread than the one running acceptConnections, then that thread will return from
     * acceptConnections.
     */
    @Override
    public synchronized void close() {
        super.close();
    }

    /**
     * Creates a new connection handler for the given socket.
     *
     * @param socket the socket for the connection
     */
    @Override
    protected void handleConnection(Socket socket) throws IOException {
        ServerConnection serverConnection = new ServerConnection(socket);
        ClientHandler clientHandler = new ClientHandler(serverConnection, this);
        serverConnection.setClientHandler(clientHandler);
        serverConnection.start();
    }


    /**
     * Adds a client to the list of clients.
     *
     * @param clientHandler which corresponds to the client.
     */
    synchronized void addClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    /**
     * Removes a client from the list of clients.
     * Removes the player from the queue.
     * Also ends the game if the player is in a game.
     *
     * @param clientHandler which corresponds to the client.
     */
    public synchronized void removeClient(ClientHandler clientHandler) {
        gameHandler.endByDisconnect(clientHandler); //ends the game if the player is in a game.
        gameHandler.removePlayer(clientHandler); //if this is not called, a game can be created
        // with a disconnected player.
        clients.remove(clientHandler);
        System.out.println(clientHandler.getUsername() + " disconnected.");
    }

    /**
     * Handles the hello command.
     * Can only be called once at the start of the connection.
     * Replies with a hello command back to the client.
     * Synchronized to prevent multiple clients from sending a hello command at the same time.
     *
     * @param clientHandler, the client sending the Hello.
     */
    public synchronized void handleHello(ClientHandler clientHandler) {
        //if the client is not in the new state, it has already sent a hello command.
        if (clientHandler.getState() != ClientState.NEW) {
            clientHandler.sendError("Hello command already received!");
            return;
        }
        clientHandler.sendHello();
        //the client is now in the connected state.
        clientHandler.setState(ClientState.CONNECTED);
    }

    /**
     * Handles the login command, adding the client to the list of clients.
     * Can only be called once, if the client is in the connected state, i.e. the client has sent a hello command.
     * Prevents the client from logging in with a username that is already logged in.
     * Synchronized to prevent multiple clients from logging in at the same time.
     *
     * @param clientHandler, the client sending the login command.
     * @param username,      the username the client wants to log in with.
     */
    public synchronized void handleLogin(ClientHandler clientHandler, String username) {
        //if the client is not in the connected state, it has not sent a hello command.
        if (clientHandler.getState() != ClientState.CONNECTED) {
            clientHandler.sendError("Cannot log in at this time!");
            return;
        }
        //checks if the username is already logged in. This needs to be done to prevent username duplicates.
        for (ClientHandler c : clients) {
            if (c != clientHandler) {
                if (c.getUsername().equals(username)) {
                    clientHandler.sendAlreadyLoggedIn();
                    return;
                }
            }
        }
        clientHandler.setUsername(username);
        addClient(clientHandler);
        clientHandler.sendLogin(); //sends a login command back to the client.
        clientHandler.setState(ClientState.LOGGED_IN); //the client is now in the logged in state.
    }

    /**
     * Handles the list command, sending a list of all the clients to the client.
     *
     * @param clientHandler the client sending the list command.
     */
    public synchronized void handleList(ClientHandler clientHandler) {
        /*
        If the client is not in the logged in state, it has not logged in yet.
        This method can only be called after login
        */
        if (clientHandler.getState() != ClientState.LOGGED_IN) {
            clientHandler.sendError("not logged in yet!");
            return;
        }
        // Creates a list of all the clients, separated by the separator.
        StringBuilder list = new StringBuilder();
        for (ClientHandler c : clients) {
            list.append(ServerProtocol.SEPARATOR).append(c.getUsername());
        }
        clientHandler.sendList(list.toString());
    }

    /**
     * Handles the queue command, adding the client to the queue, or removing the client from the queue.
     *
     * @param clientHandler, the client sending the queue command.
     * Synchronized to prevent multiple clients from queueing at the same time, creating erroneous games.
     */
    public synchronized void handleQueue(ClientHandler clientHandler) {
        /*
        Can only be called after login and when the client is not in a game.
        */
        if (clientHandler.getState() == ClientState.NEW || clientHandler.getState() == ClientState.CONNECTED) {
            clientHandler.sendError("not logged in yet!");
            return;
        }
        if (clientHandler.getState() == ClientState.IN_GAME) {
            clientHandler.sendError("already in game!");
            return;
        }

        //every subsequent queue command will toggle the player in the queue.
        if (!gameHandler.getPlayerQueue().contains(clientHandler)) {
            gameHandler.addPlayer(clientHandler);
            gameHandler.createGame();
        } else {
            gameHandler.removePlayer(clientHandler);
        }
    }

    /**
     * Handles the move command, performing the move in the game.
     * Can only be called when the client is in the in game state.
     * Synchronized to prevent multiple clients from making a move at the same time.
     *
     * @param clientHandler the client sending the move command.
     * @param location      the location the client wants to make a move at.
     */
    public synchronized void handleMove(ClientHandler clientHandler, int location) {
        // Can only be called when the client is in the in game state.
        if (clientHandler.getState() != ClientState.IN_GAME) {
            clientHandler.sendError("not in game yet!");
            return;
        }
        gameHandler.makeMove(clientHandler, location);
    }

    /**
     * Handles the error command, printing an error message to the server (mostly debugging purposes).
     */
    public void handleError() {
        System.out.println("Error!");
    }

    /**
     * Starts the server.
     * If the port is in use, the user will be prompted to enter another port.
     */
    public static void main(String[] args) {
        Scanner input1 = new Scanner(System.in);
        int port;
        while (true) {
            System.out.println("Enter port number:");
            try {
                int inputInt = input1.nextInt();
                if (inputInt > 1023 && inputInt < 65535) {
                    try {
                        new Server(inputInt).close(); // Try to open and close a server socket
                        port = inputInt;
                        break;
                    } catch (IOException e) {
                        System.out.println("Port " + inputInt + " is in use. Try another one.");
                    }
                } else {
                    System.out.println("Invalid port, try again");
                }
            } catch (InputMismatchException e) { // In case the user enters a non-integer.
                System.out.println("Invalid input. Please enter an integer.");
                input1.next();
            }
        }
        try {
            Server chatServer = new Server(port);
            chatServer.acceptConnections();
            System.out.println(chatServer.getPort());
        } catch (IOException e) {
            System.out.println("Failed to start the server: " + e.getMessage());
        }
    }
}
