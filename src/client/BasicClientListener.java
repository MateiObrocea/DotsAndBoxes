package client;

import java.util.List;

/**
 * Implementation of the ClientListener interface.
 * This class is responsible for connecting the client to the TUI.
 */
public class BasicClientListener implements ClientListener {
    private final Client client;
    private final ClientTUI clientTUI;

    public BasicClientListener(Client client, ClientTUI clientTUI) {
        this.client = client;
        this.clientTUI = clientTUI;
    }


    @Override
    public void receiveList(List<String> players) {
        clientTUI.receiveList(players);
    }


    @Override
    public void receiveHello() {
        clientTUI.receiveHello();
    }

    @Override
    public void receiveAlreadyLoggedIn() {
        clientTUI.receiveAlreadyLoggedIn();
    }

    @Override
    public void receiveLogin() {
        clientTUI.receiveLogin();
    }

    @Override
    public void receiveError() {
        clientTUI.receiveError();
    }


    @Override
    public void receiveGameOver(String reason, String winner) { // called from the server
        clientTUI.receiveGameOver(reason, winner);
    }

    @Override
    public void connectionLost() {
        client.close();
        client.removeListener(this);
    }


    @Override
    public void createGame(String name1, String name2) {//called from the server
        clientTUI.createGame(name1, name2);
    }


    @Override
    public void receiveMove(int location) { //called from the server
        clientTUI.receiveMove(location);
    }
}