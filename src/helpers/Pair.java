package helpers;

/**
 * Record class for storing a pair of objects.
 * Helps to associate two players with a game, in a map.
 */
public record Pair<T, U>(T first, U second) {
}