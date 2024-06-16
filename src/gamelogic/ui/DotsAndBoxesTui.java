package gamelogic.ui;

import gamelogic.model.BasicPlayer;
import gamelogic.model.DotsAndBoxesGame;
import gamelogic.model.Line;
import gamelogic.model.Mark;

import java.util.Scanner;

/**
 * TUI class for the game.
 * Implemented for testing purposes.
 * It's done such that it can demonstrate that the game works.
 */
public class DotsAndBoxesTui {
    /**
     * Run method that starts a game and takes moves.
     */
    public static void run() {
        System.out.println("Give the name of the first player: ");
        Scanner input1 = new Scanner(System.in);
        String name1 = input1.nextLine();
        BasicPlayer player1;
        BasicPlayer player2;
        System.out.println("Give the name of the second player:");
        String name2 = input1.nextLine();
        player1 = new BasicPlayer(name1, Mark.X);
        player2 = new BasicPlayer(name2, Mark.O);
        DotsAndBoxesGame game = new DotsAndBoxesGame(player1, player2);
        System.out.println(game);
        while (!game.isGameOver()) {
            System.out.println(
                    "\nScore: " + game.getPlayers().get(0).getName() + " " + game.getPlayers()
                            .get(0).getScore() + " - " + game.getPlayers().get(1)
                            .getScore() + " " + game.getPlayers().get(1).getName());
            System.out.println("\nIt is " + game.getTurn()
                    .toString() + "'s turn \nEnter location of the next move: ");
            int location = input1.nextInt();
            Line nextLine = game.getTurn().determineLine(location);
            game.drawLine(nextLine);
            System.out.println(game);
        }
        System.out.println(
                "\nScore: " + game.getPlayers().get(0).getName() + " " + game.getPlayers().get(0)
                        .getScore() + " - " + game.getPlayers().get(1)
                        .getScore() + " " + game.getPlayers().get(1).getName());
        System.out.println("The winner of this game is: " + game.getWinner());
    }

    /**
     * Minimalistic main method to invoke the run method.
     */
    public static void main(String[] args) {
        run();
    }
}
