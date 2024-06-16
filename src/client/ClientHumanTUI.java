package client;

import gamelogic.model.BasicPlayer;
import gamelogic.model.DotsAndBoxesGame;
import gamelogic.model.Mark;
import helpers.ServerProtocol;

import java.io.IOException;
import java.net.InetAddress;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class implements the client TUI for the Dots and Boxes game.
 * It prompts the user for the server's IP address and port, establishes a connection to the server and
 * allows the user to interact with the (created) game by entering commands.
 */
public class ClientHumanTUI implements ClientTUI {
    protected DotsAndBoxesGame dotsAndBoxesGame;
    private Client client;
    private ClientListener clientListener;
    public static final String RESET = "\u001B[0m"; // Reset color
    public static final String BOLD = "\u001B[1m";
    // Locks to ensure that the program can continue only after the hello command is received.
    private final Lock helloLock = new ReentrantLock(); // Lock for the hello command
    // Locks to ensure that the program can continue only after the login command is received.
    private final Lock loginLock = new ReentrantLock();
    private boolean handShakeCompleted = false;

    /**
     * Sends a "Hello" command to the server, providing a description.
     * Waits for a response from the server before proceeding.
     */
    public void doSendHelloCommand() {
        client.sendHelloCommand("Minor14's Client"); // our description of the client
        // Hello lock to ensure that the program can continue only after the hello command is received.
        synchronized (helloLock) {
            try {
                helloLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void doSendListCommand() {
        client.sendListCommand();
    }

    /**
     * Sends a "Queue" command to the server.
     * Toggles the client's queued status, depending on the current status.
     * Prints a message to the user.
     */
    @Override
    public void doSendQueueCommand() {
        if (dotsAndBoxesGame == null) {
            if (!client.isQueued()) {
                System.out.println("You have been queued for a game.");
                client.setQueued(true);
            } else {
                System.out.println("Removed from queue.");
                client.setQueued(false);
            }
            client.sendQueueCommand();
        } else {
            System.out.println("You cannot queue while in a game.");
        }
    }

    @Override
    public void sendError(String error) {
        client.sendErrorCommand(error);
    }

    /**
     * Sends a "Move" command to the server, if the client is in a game, and it is the client's turn.
     * Also informs the user if the move is not valid.
     *
     * @param location the location of the move to be made.
     */
    @Override
    public void doSendMoveCommand(int location) {
        if (dotsAndBoxesGame != null) {
            if (dotsAndBoxesGame.getTurn().getName().equals(client.getUsername())) {
                if (dotsAndBoxesGame.getValidLocations().contains(location)) {
                    client.sendMoveCommand(location);
                } else {
                    System.out.println("This is not a valid location, please try a new move or type 'hint'");
                }
            } else {
                System.out.println("Moves when it is not your turn are not possible");
            }
        } else {
            System.out.println("Moves outside of games are not possible");
        }
    }

    /**
     * Sends a "Log in" command to the server.
     * Prompts the user for a username and sends it to the server.
     * Waits for a response from the server before proceeding.
     */
    @Override
    public void doSendLogInCommand() {
        Scanner input1 = new Scanner(System.in);
        while (!handShakeCompleted) {
            System.out.println("Enter username:");
            String username = input1.nextLine();
            client.sendLogInCommand(username);
            client.setUsername(username);

              /*
            Explanation of the need for synchronization:
            In the event of a username that is already taken, there needs to be a mechanism which tells the user
            to input another username until the username is valid. With a simple while loop, the following issue
            will appear:
            The client sends the login command, it is successful, and the server replies with "LOGIN", setting the
            handShakeCompleted to true. However, there may be delay in the server's response, and by the time the
            client receives the LOGIN from server, the while loop has already iterated the second time, sending
            a login command again. This will cause an error in the server, because the client is not allowed to
            log in multiple times.
            ---------------------------------------------------------------------------------------------------
            With this implementation, the client will wait until it receives the login from the server.
            The thread is unlocked in the receiveLogin() method.
            The thread is also unlocked in the receiveAlreadyLoggedIn() method, because otherwise the client will
            wait forever.
            ---------------------------------------------------------------------------------------------------
            Also, this method ensures that the other commands (queue, move, list, etc.) are not called
            before the handshake is completed.
             */
            synchronized (loginLock) {
                try {
                    loginLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Creates a new game between two players and initializes the game state.
     * Prints information about the players and the initial game state.
     *
     * @param name1 The name of the first player.
     * @param name2 The name of the second player.
     */
    @Override
    public void createGame(String name1, String name2) {
        // Check if there is already an ongoing game
        if (dotsAndBoxesGame != null) {
            // If there is, send an error message to the client and return
            sendError("Untimely new game");
            return;
        }

        BasicPlayer player1 = new BasicPlayer(name1, Mark.X);
        BasicPlayer player2 = new BasicPlayer(name2, Mark.O);
        dotsAndBoxesGame = new DotsAndBoxesGame(player1, player2);

        System.out.println("Dots and boxes game: " + BOLD + name1 + " VS " + name2 + RESET);
        System.out.println(dotsAndBoxesGame);

        if (client.getUsername().equals(name1)) {
            // If the client is the first player, prompt them to make the first move
            System.out.println("You are first, do a move: ");
        } else if (client.getUsername().equals(name2)) {
            // If the client is the second player, inform them that they are waiting for the opponent's move
            System.out.println("Waiting for the opponent's move. ");
        }
    }


    /**
     * Receives the game over information from the server and prints the result.
     * Resets the current game state.
     *
     * @param reason The reason for the game over, such as DRAW, VICTORY, or DISCONNECT.
     * @param winner The name of the winner, if applicable.
     */
    @Override
    public void receiveGameOver(String reason, String winner) {
        // Use a switch statement to handle different game over scenarios
        switch (reason) {
            case ServerProtocol.DRAW:
                System.out.println("It's a draw!");
                break;
            case ServerProtocol.VICTORY:
                System.out.println("The winner is " + BOLD + winner + RESET + "!");
                break;
            case ServerProtocol.DISCONNECT:
                System.out.println("The winner is: " + BOLD + winner + RESET +
                        "! (The other player has disconnected.)");
                break;
        }
        client.setQueued(false); // We have to remove the player from the queue, because otherwise it will
        // be queued forever.
        dotsAndBoxesGame = null; // Reset the current game state
    }


    /**
     * Receives a move from the opponent and updates the game state accordingly.
     * Prints information about the move, the current game state, and the scores.
     *
     * @param location The location where the opponent has made a move.
     */
    @Override
    public void receiveMove(int location) {
        // Update the game state by drawing a line at the specified location
        dotsAndBoxesGame.drawLine(dotsAndBoxesGame.getTurn().determineLine(location));
        // Print the current state of the game
        System.out.println(dotsAndBoxesGame);
        // Check if the current turn belongs to the local player or the opponent
        if (dotsAndBoxesGame.getTurn().getName().equals(client.getUsername())) {
            // Opponent's move
            System.out.println(dotsAndBoxesGame.getOther().getName() + " " +
                    "has made a move. The move was " + location + ". ");
            System.out.println(dotsAndBoxesGame.getPlayers().get(0).getName() + " : "
                    + dotsAndBoxesGame.getPlayers().get(1).getName());
            System.out.println(dotsAndBoxesGame.getPlayers().get(0).getScore() + " : "
                    + dotsAndBoxesGame.getPlayers().get(1).getScore());
            System.out.println("Do a move:");
        } else {
            // Local player's move
            System.out.println("You made a move. The move was " + location + ".");
            System.out.println(dotsAndBoxesGame.getPlayers().get(0).getName() + " : "
                    + dotsAndBoxesGame.getPlayers().get(1).getName());
            System.out.println(dotsAndBoxesGame.getPlayers().get(0).getScore() + " : "
                    + dotsAndBoxesGame.getPlayers().get(1).getScore());
            System.out.println("Now wait for the opponent to make a move.");
        }
    }


    @Override
    public void receiveLogin() {
        handShakeCompleted = true;
        synchronized (loginLock) {
            loginLock.notifyAll(); // the login is received, the client can continue.
        }
    }

    @Override
    public void receiveAlreadyLoggedIn() {
        System.out.println("Somebody with this username already exists.");
        synchronized (loginLock) {
            loginLock.notifyAll(); //the alreadyLoggedIn is received, the client can ask for another username.
        }
    }

    @Override
    public void receiveError() {
        System.out.println("An error has occurred while processing your request.");
    }

    /**
     * Method that receives and prints the players currently being online to the server
     * that the user is currently being connected to.
     *
     * @param players currently being connected to the server
     */
    @Override
    public void receiveList(List<String> players) {
        int count = 1;
        System.out.println(BOLD + "Currently online: " + RESET);
        for (String player : players) {
            System.out.println(count + ": " + player);
            count++;
        }
    }


    @Override
    public void receiveHello() {
        synchronized (helloLock) {
            helloLock.notifyAll(); // the program can continue only after the hello command is received.
        }
    }

    /**
     * This method initializes the client by prompting the user for the server's IP address and port.
     * In case the connection fails, the client does not crash. The user is prompted to try again, after a while.
     *
     * @return true if the connection was successful, false otherwise.
     */
    public boolean initializeClient() {
        Scanner input = new Scanner(System.in);
        try {
            System.out.print("Enter IP address: ");
            String address = input.nextLine();
            System.out.print("Enter port: ");
            int port = input.nextInt();
            input.nextLine();
            client = new Client(InetAddress.getByName(address), port);
            clientListener = new BasicClientListener(client, this);
            client.addListener(clientListener);
            doSendHelloCommand();
            doSendLogInCommand();
            return true;
        } catch (IOException e) {
            System.out.println("Could not connect to server.");
        }
        return false;
    }

    /**
     * This method continuously accepts user commands (from the console) for interacting with the game.
     */
    public void cycleThroughCommands() {
        Scanner scanner = new Scanner(System.in);
        String userCommand = scanner.nextLine().toLowerCase();
        // Try to parse the command as an integer (location)
        try {
            int location = Integer.parseInt(userCommand);
            // Send move command to the server
            doSendMoveCommand(location);
        } catch (NumberFormatException e) {
            // If parsing as integer fails, handle non-integer commands
            switch (userCommand) {
                case "help":
                    doHelp();
                    break;
                case "queue":
                    doSendQueueCommand();
                    break;
                case "list":
                    doSendListCommand();
                    break;
                case "exit":
                    // Exit the game and notify the client listener
                    clientListener.connectionLost();
                    break;
                case "hint":
                    // Provide a hint if in a game
                    if (dotsAndBoxesGame != null) {
                        Random random = new Random();
                        int randomIndex = random.nextInt(dotsAndBoxesGame.getValidLocations().size());
                        int hint = dotsAndBoxesGame.getValidLocations().get(randomIndex);
                        System.out.println("Try: " + hint);
                    } else {
                        System.out.println("Hints outside of games are not possible.");
                    }
                    break;
                default:
                    // Handle invalid commands
                    if (dotsAndBoxesGame == null) {
                        System.out.println("Invalid command. Please enter a valid command or type 'help'");
                    } else {
                        System.out.println("Invalid command or integer. Please enter a valid command or " +
                                "type 'help' or 'hint'");
                    }

            }

        }
    }

    /**
     * This method runs the TUI for the game client. It prompts the user for the server's IP address and port,
     * establishes a connection to the server, and continuously accepts user commands for interacting with the game.
     */
    @Override
    public void runTUI() {
        while (true) {
            try {
                if (initializeClient()) {
                    doHelp();
                    while (true) {
                        cycleThroughCommands();
                        // Introduce a short delay
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException error) {
                            error.printStackTrace();
                        }
                    }
                }
            } catch (IllegalArgumentException | InputMismatchException e) {
                System.out.println("Invalid port");
            }
        }

    }


    /**
     * Method that prints the help menu, dependent on if there is currently a game being played or not it
     * prints additional information.
     */
    public void doHelp() {
        System.out.println(BOLD + "WELCOME TO DOTS AND BOXES" + RESET);
        System.out.println("----------------------------------");
        System.out.println("List of commands:");
        System.out.println("----------------------------------");
        System.out.println("list: lists all the logged-in users.");
        System.out.println("queue: queues you for a game.");
        System.out.println("help: list of commands.");
        System.out.println("exit: exit the game.");
        System.out.println("--------------------------------");
        if (dotsAndBoxesGame != null) {
            System.out.println("Enter 1-59 to make an move.");
            System.out.println("hint: to get an hint");
            System.out.println("--------------------------------");
        }
    }

    /**
     * Minimalistic main that calls the run method of the TUI.
     */
    public static void main(String[] args) {
        ClientHumanTUI clientHumanTUI = new ClientHumanTUI();
        clientHumanTUI.runTUI();
    }
}

