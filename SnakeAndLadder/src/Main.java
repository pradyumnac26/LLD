import models.Board;
import models.Dice;
import models.GameState;
import models.Player;
import service.Game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();

        // head -> tail
        snakes.put(99, 54);
        snakes.put(70, 55);
        snakes.put(52, 42);
        snakes.put(25, 2);

        // bottom -> top
        ladders.put(6, 25);
        ladders.put(11, 40);
        ladders.put(46, 90);
        ladders.put(60, 85);

        Board board = new Board(100, snakes, ladders);

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");

        Dice dice = new Dice();

        Game game = new Game(board, Arrays.asList(p1, p2), dice);

        while (game.getState() == GameState.IN_PROGRESS) {
            System.out.println(game.rollAndMove());
        }

        System.out.println("Winner: " + game.getWinner().getName());
    }
}