package menu;

import model.Game;
import service.*;
import util.ConsoleInput;

public class Menu {
    private final ConsoleInput input = ConsoleInput.getInstance();
    public void displayMenu() {
        Game[] games = {new TreasureService(), new QuizService(), new ZombieService(), new SnakeLadderService()};
        try {
            while (true) {
                System.out.println("\n================================\n     JAVA GAME COLLECTION\n================================");
                System.out.println("1. Treasure Hunt\n2. Quiz Battle\n3. Zombie Survival\n4. Snake & Ladder\n5. Exit");
                int choice = input.readInt("Enter your choice: ", 1, 5);
                if (choice == 5) break;
                games[choice - 1].startGame(); // Polymorphism: every game implements Game.
            }
        } catch (ConsoleInput.EndOfInput e) {
            System.out.println("\nInput closed. Ending the session.");
        }
        System.out.println("Thank you for playing!");
    }
}
