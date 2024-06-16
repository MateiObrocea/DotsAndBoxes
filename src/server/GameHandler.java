package server;

import gamelogic.model.BasicPlayer;
import gamelogic.model.DotsAndBoxesGame;
import gamelogic.model.Mark;
import helpers.Pair;
import helpers.ServerProtocol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * A class which represents the handler for a game.
 * The class is responsible for pairing players and hosting games
 * The class can create and host multiple independent games.
 */
public class GameHandler {
    // The queue of players waiting to be paired with another player
    private final Queue<ClientHandler> playerQueue = new LinkedList<>();
    // A map of pairs of players and the game they are playing. The game is the value, the pair is the key.
    // The pair is a helper record. See helpers package.
    private final Map<Pair<ClientHandler, ClientHandler>, DotsAndBoxesGame> playerGameMap = new HashMap<>();


    public void addPlayer(ClientHandler player) {
        playerQueue.add(player);
    }

    public void removePlayer(ClientHandler player) {
        playerQueue.remove(player);
    }

    /**
     * Creates a new game for the first two players in the queue.
     * If there are not enough players, nothing happens.
     * Removes the players from the queue and sets them to in game state.
     */
    public void createGame() {
        if (playerQueue.size() < 2) {
            return; // Not enough players to start a game
        }
        // Removing them is important to prevent them from being paired again.
        ClientHandler client1 = playerQueue.remove();
        ClientHandler client2 = playerQueue.remove();
        //both players are human, because the server does not distinguish between human and computer players.
        BasicPlayer player1 = new BasicPlayer(client1.getUsername(), Mark.X);
        BasicPlayer player2 = new BasicPlayer(client2.getUsername(), Mark.O);
        // Create a new game for the pair of players
        DotsAndBoxesGame game = new DotsAndBoxesGame(player1, player2);

        // Add the pair and the game to the map
        playerGameMap.put(new Pair<>(client1, client2), game);

        client1.sendNewGame(client1.getUsername(), client2.getUsername());
        client2.sendNewGame(client1.getUsername(), client2.getUsername());

        client1.setState(ClientState.IN_GAME);
        client2.setState(ClientState.IN_GAME);
    }

    /**
     * Makes a move for the client in the game it is in.
     * Can only be called when the client is in a game.
     * Checks for validity of the move and sens the move to both clients.
     * Checks the game for game over and sends a game over message to both clients if the game is over.
     *
     * @param clientHandler the client making the move.
     * @param location      the location the client wants to make a move at.
     */
    public void makeMove(ClientHandler clientHandler, int location) {
        DotsAndBoxesGame currentGame = null;
        ClientHandler client1 = null;
        ClientHandler client2 = null;
        // retrieve the game the client is in.
        for (Map.Entry<Pair<ClientHandler, ClientHandler>, DotsAndBoxesGame> entry : playerGameMap.entrySet()) {
            Pair<ClientHandler, ClientHandler> pair = entry.getKey();
            if (pair.first().equals(clientHandler) || pair.second().equals(clientHandler)) {
                currentGame = entry.getValue();
                client1 = pair.first();
                client2 = pair.second();
                break;
            }
        }

        // Cannot make a move in a null game.
        if (currentGame == null) {
            clientHandler.sendError("not in game yet!");
            return;
        }

        // The game does not check if the move is valid WHEN SENDING, so we have to do it here.
        if (currentGame.getValidLocations().contains(location)) {
            if (currentGame.getTurn().getName().equals(clientHandler.getUsername())) {
                client1.sendMove(location);
                client2.sendMove(location);
                currentGame.drawLine(currentGame.getTurn().determineLine(location));
            } else {
                clientHandler.sendError("not your turn!");
            }
        } else {
            clientHandler.sendError("invalid move!");
        }

        // Check if the game is over, e.g. if there are no more valid moves.
        if (currentGame.isGameOver()) {
            client1.sendGameOver(ServerProtocol.VICTORY, currentGame.getWinner().getName());
            client2.sendGameOver(ServerProtocol.VICTORY, currentGame.getWinner().getName());
            currentGame.reset();
            // Remove the game from the map.
            playerGameMap.remove(new Pair<>(client1, client2));
            // Set the clients back to logged in state.
            client1.setState(ClientState.LOGGED_IN);
            client2.setState(ClientState.LOGGED_IN);
        }
    }

    /**
     * Returns the queue of players.
     *
     * @return the queue of players.
     */
    public Queue<ClientHandler> getPlayerQueue() {
        return playerQueue;
    }

    /**
     * Ends the game the client is in by disconnecting the client.
     * The disconnection is game over condition handled in a different manner, namely here.
     *
     * @param clientHandler the client to be disconnected.
     */
    public void endByDisconnect(ClientHandler clientHandler) {

        DotsAndBoxesGame currentGame = null;
        ClientHandler client1 = null;
        ClientHandler client2 = null;

        // Retrieve the game the client is in.
        String winner = "";
        for (Map.Entry<Pair<ClientHandler, ClientHandler>, DotsAndBoxesGame> entry : playerGameMap.entrySet()) {
            Pair<ClientHandler, ClientHandler> pair = entry.getKey();
            if (pair.first().equals(clientHandler)) {
                currentGame = entry.getValue();
                client1 = pair.first();
                client2 = pair.second();
                winner = client2.getUsername();
                break;
            } else if (pair.second().equals(clientHandler)) {
                currentGame = entry.getValue();
                client2 = pair.second();
                client1 = pair.first();
                winner = client1.getUsername();
                break;
            }
        }
        // Cannot end a null game.
        if (currentGame == null) {
            return;
        }

        // Needs to update the fields of the clients to terminate gracefully.
        client1.setState(ClientState.LOGGED_IN);
        client2.setState(ClientState.LOGGED_IN);
        client1.sendGameOver(ServerProtocol.DISCONNECT, winner);
        client2.sendGameOver(ServerProtocol.DISCONNECT, winner);
        // Reset the game and remove it from the map.
        currentGame.reset();
        playerGameMap.remove(new Pair<>(client1, client2));
    }
}
