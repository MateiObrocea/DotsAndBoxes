package client;

import gamelogic.ai.EasyStrategy;
import gamelogic.ai.ExpertStrategy;
import gamelogic.ai.HardStrategy;
import gamelogic.ai.Strategy;
import gamelogic.model.BasicPlayer;
import gamelogic.model.DotsAndBoxesGame;
import gamelogic.model.Mark;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class represents the client's TUI for the AI.
 * The user can choose a strategy and the client will play the game using that strategy.
 * The AI needs to be manually queued, by typing "queue" in the console.
 * Allows for dynamic changing of the difficulty level, by typing "1", "2" or "3" in the console.
 */

public class ClientAITUI implements ClientTUI {
    protected DotsAndBoxesGame dotsAndBoxesGame;
    private Client client;
    // Locks to ensure that the program can continue only after the hello command is received.
    private final Lock helloLock = new ReentrantLock();
    // Locks to ensure that the program can continue only after the login command is received.
    private final Lock loginLock = new ReentrantLock();
    private boolean handShakeCompleted = false; // true if the login command is received.
    private Strategy strategy;

    /**
     * Sets the strategy based on the user's input.
     *
     * @param input a scanner object reading the user's input.
     */
    public void setStrategy(Scanner input) {
        String choice = input.nextLine();
        switch (choice) {
            case "1":
                strategy = new EasyStrategy("Easy Strategy");
                System.out.println("Easy Strategy selected");
                break;
            case "2":
                strategy = new HardStrategy("Hard Strategy");
                System.out.println("Hard Strategy selected");
                break;
            case "3":
                strategy = new ExpertStrategy("Expert Strategy");
                System.out.println("Expert Strategy selected");
                break;
            default:
                System.out.println("Not a correct option");
        }
    }

    /**
     * Sends a hello command to the server.
     * The hello command is sent with no extensions.
     */
    public void doSendHelloCommand() {
        client.sendHelloCommand("Minor14's AI"); // hello with no extensions.
        // Hello lock to ensure that the program can continue only after the hello command is received.
        synchronized (helloLock) {
            try {
                helloLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Sends a list command to the server.
     */
    @Override
    public void doSendListCommand() {
        client.sendListCommand();
    }

    /**
     * Sends a queue command to the server, if the client is not in a game.
     */
    @Override
    public void doSendQueueCommand() {
        if (dotsAndBoxesGame == null) {
            client.sendQueueCommand();
        }
    }

    /**
     * Sends an error command to the server via the client.
     *
     * @param error the error message to be sent.
     */
    @Override
    public void sendError(String error) {
        client.sendErrorCommand(error);
    }

    /**
     * Determines the move to be made by the AI strategy.
     *
     * @return the location of the move to be made.
     */
    public int determineMove() {
        return strategy.computeLocation(dotsAndBoxesGame);
    }

    @Override
    public void doSendMoveCommand(int location) {
        if (dotsAndBoxesGame != null) {
            if (dotsAndBoxesGame.getTurn().getName().equals(client.getUsername())) {
                if (dotsAndBoxesGame.getValidLocations().contains(location)) {
                    client.sendMoveCommand(location);
                }
            }
        }
    }

    /**
     * Sends a login command to the server.
     * Ensures that the program can continue only after the login command is received.
     */
    @Override
    public void doSendLogInCommand() {
        Scanner input1 = new Scanner(System.in);
        while (!handShakeCompleted) { // while the login command is not received.
            System.out.println("Enter username:");
            String username = input1.nextLine();
            client.sendLogInCommand(username);
            client.setUsername(username);
            // See need for synchronization in the ClientHumanTUI class, under this method.
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
     * Creates a new game, when it receives a new game command from the server.
     *
     * @param name1 the name of the first player.
     * @param name2 the name of the second player.
     */
    @Override
    public void createGame(String name1, String name2) {
        if (dotsAndBoxesGame != null) {
            sendError("Untimely new game"); //Sends an error command to the server when the client is already in a game.
            return;
        }
        BasicPlayer player1 = new BasicPlayer(name1, Mark.X);
        BasicPlayer player2 = new BasicPlayer(name2, Mark.O);
        dotsAndBoxesGame = new DotsAndBoxesGame(player1, player2);
        System.out.println(dotsAndBoxesGame);
    }

    /**
     * Receives and prints the game over command from the server.
     *
     * @param reason the reason for the game over.
     * @param winner the name of the winner.
     */
    @Override
    public void receiveGameOver(String reason, String winner) {
        dotsAndBoxesGame = null;
        System.out.println("Game over: " + reason + " " + winner);
    }

    /**
     * Receives and performs the move command from the server.
     *
     * @param location the location of the move to be made.
     */
    @Override
    public void receiveMove(int location) {
        dotsAndBoxesGame.drawLine(dotsAndBoxesGame.getTurn().determineLine(location));
        System.out.println(dotsAndBoxesGame);
    }

    /**
     * Receives the login command from the server.
     * The program can now continue.
     */
    @Override
    public void receiveLogin() {
        handShakeCompleted = true;
        synchronized (loginLock) {
            loginLock.notifyAll(); // the login is received, the client can continue.
        }
    }

    /**
     * Receives the alreadyLoggedIn command from the server.
     */
    @Override
    public void receiveAlreadyLoggedIn() {
        System.out.println("Somebody with this username already exists.");
        synchronized (loginLock) {
            loginLock.notifyAll(); //the alreadyLoggedIn is received, the client can ask for another username.
        }
    }

    /**
     * Receives the new game command from the server.
     */
    @Override
    public void receiveError() {
        System.out.println("An error has occurred while processing your request.");
    }

    /**
     * Receives the list command from the server.
     * Does not do anything, because the AI does not need to know the list of players.
     */
    @Override
    public void receiveList(List<String> players) {
    }

    /**
     * Receives the hello command from the server.
     * The user can now log the AI in.
     */
    @Override
    public void receiveHello() {
        synchronized (helloLock) {
            helloLock.notifyAll(); // the program can continue only after the hello command is received.
        }
    }

    /**
     * Generates a port from the user input.
     * Throws an exception if the input is not an integer.
     */
    public int createPort() {
        Scanner input1 = new Scanner(System.in);
        int port;
        while (true) {
            try {
                System.out.println("Enter port: ");
                port = input1.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid port. Please enter a valid port.");
                input1.next();
            }
        }
        return port;
    }

    /**
     * Runs a new thread to handle user input for changing the difficulty level.
     * Or for queueing the AI.
     * This runs in parallel with the game.
     *
     * @param input, a scanner object reading the user's input.
     */
    public void runCommandThread(Scanner input) {
        new Thread(() -> {
            while (true) { // run forever to handle user input
                String command = input.nextLine();
                if (command.equals("queue")) {
                    doSendQueueCommand();
                }
                setStrategy(input); // change the difficulty level
            }
        }).start();
    }

    /**
     * Runs the client's TUI.
     */
    @Override
    public void runTUI() {
        Scanner input1 = new Scanner(System.in);
        try (input1) {
            System.out.println("Enter IP address: ");
            String address = input1.nextLine();
            int port = createPort();
            client = new Client(InetAddress.getByName(address), port);
            ClientListener clientListener = new BasicClientListener(client, this);
            client.addListener(clientListener);
            // establishes the handshake with the server
            doSendHelloCommand();
            doSendLogInCommand();

            // set strategy once at the start of the game
            System.out.println("Choose strategy: [1] easy [2] hard [3] expert");
            setStrategy(input1);
            // Start a new thread to handle user input for changing the difficulty level
            runCommandThread(input1); //options: queue, 1,2,3
            // Continue with the game in the main thread
            while (true) {
                if (dotsAndBoxesGame != null) {
                    if (dotsAndBoxesGame.getTurn().getName().equals(client.getUsername())) {
                        doSendMoveCommand(determineMove());
                    }
                }
                try {
                    // Prevents the client from sending too many commands at once.
                    // This is needed to prevent sending untimely commands.
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Invalid address. Please enter a valid IP address."); //invalid address
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientAITUI clientAITUI = new ClientAITUI();
        clientAITUI.runTUI();
    }

}