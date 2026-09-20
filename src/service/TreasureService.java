package service;

import java.util.ArrayList;
import java.util.Random;
import model.Game;
import model.Player;
import model.Room;
import util.*;

public class TreasureService extends Game {
    private final Random random;
    private final int seconds;
    public TreasureService() { this(new Random(), 30); }
    public TreasureService(Random random, int seconds) { this.random = random; this.seconds = seconds; }
    @Override public void startGame() {
        ConsoleInput input = ConsoleInput.getInstance();
        System.out.println("\nTREASURE HUNT");
        Player player = new Player(input.readName("Player name: "));
        ArrayList<Room> rooms = new ArrayList<>();
        int treasureRoom = random.nextInt(8) + 1;
        for (int i = 1; i <= 8; i++) rooms.add(new Room(i, i == treasureRoom));
        System.out.println("Find the treasure in rooms 1-8 within " + seconds + " seconds. You have 5 searches. Q returns to menu.");
        boolean won = false;
        try (GameTimer timer = new GameTimer(seconds)) {
            int attempts = 0;
            while (timer.isActive() && attempts < 5) {
                String answer = input.readWhile("Search room (1-8 / Q): ", timer::isActive);
                if (answer == null) break;
                if (answer.equalsIgnoreCase("q")) { System.out.println("Treasure hunt cancelled."); return; }
                try {
                    Room room = rooms.get(Validation.number(answer, 1, 8) - 1);
                    if (room.isSearched()) throw new InvalidChoiceException("That room was already searched. Choose another.");
                    room.search();
                    attempts++;
                    if (room.hasTreasure()) { player.increaseScore(); won = true; break; }
                    System.out.println("Empty room. Hint: the treasure is in a "
                        + (treasureRoom > room.getNumber() ? "higher" : "lower") + " numbered room. Searches left: " + (5 - attempts));
                } catch (InvalidChoiceException e) { System.out.println(e.getMessage()); }
            }
            if (won) System.out.println("Treasure found! " + player.getName() + " wins. Score: " + player.getScore());
            else System.out.println((timer.isActive() ? "No searches left." : "Time is up!") + " Treasure was in room " + treasureRoom + ".");
        }
    }
}
