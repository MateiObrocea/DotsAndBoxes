package server;

/**
 * Represents the state of a client.
 * Created to facilitate the handling of commands at the right time.
 */
public enum ClientState {
    NEW, CONNECTED, LOGGED_IN, IN_GAME
}
