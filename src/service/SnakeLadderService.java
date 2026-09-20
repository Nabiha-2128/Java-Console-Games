package service;

import java.util.ArrayList;
import java.util.Random;
import model.Game;
import model.Player;
import model.SnakeBoard;
import util.ConsoleInput;

public class SnakeLadderService extends Game {
    private final Random random;
    public SnakeLadderService() { this(new Random()); }
    public SnakeLadderService(Random random) { this.random = random; }
    @Override public void startGame() {
        ConsoleInput input = ConsoleInput.getInstance();
        System.out.println("\nSNAKE & LADDER");
        int count = input.readInt("Number of players (2-4): ", 2, 4);
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++) players.add(new Player(input.readName("Player " + (i + 1) + " name: ")));
        int[] positions = new int[count];
        SnakeBoard board = new SnakeBoard();
        System.out.println("Start at 0. Reach exactly 100 to win. One roll per turn, including a six. Q returns to menu.");
        for (int turn = 0; ; turn = (turn + 1) % count) {
            Player player = players.get(turn);
            String command = input.read(player.getName() + " at " + positions[turn] + ": press Enter to roll (Q to quit): ");
            if (command.equalsIgnoreCase("q")) { System.out.println("Board game cancelled."); return; }
            while (!command.isEmpty()) {
                command = input.read("Press Enter to roll, or Q to quit: ");
                if (command.equalsIgnoreCase("q")) { System.out.println("Board game cancelled."); return; }
            }
            int roll = random.nextInt(6) + 1;
            int landing = positions[turn] + roll;
            int next = board.move(positions[turn], roll);
            System.out.println(player.getName() + " rolled " + roll + ".");
            if (landing > 100) System.out.println("Exact roll required. Stay at " + next + ".");
            else if (next > landing) System.out.println("Ladder! " + landing + " -> " + next);
            else if (next < landing) System.out.println("Snake! " + landing + " -> " + next);
            positions[turn] = next;
            for (int i = 0; i < count; i++) System.out.print(players.get(i).getName() + ": " + positions[i] + "  ");
            System.out.println();
            if (next == 100) { player.increaseScore(); System.out.println(player.getName() + " wins!"); return; }
        }
    }
}
