package menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import service.QuizService;

public class Menu {

    Scanner sc = new Scanner(System.in);

    public void displayMenu() {

        while (true) {

            try {

                System.out.println("\n================================");
                System.out.println("     JAVA GAME COLLECTION");
                System.out.println("================================");
                System.out.println("1. Treasure Hunt");
                System.out.println("2. Quiz Battle");
                System.out.println("3. Zombie Survival");
                System.out.println("4. Snake & Ladder");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                int choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        System.out.println("Treasure Hunt - Coming Soon!");
                        break;

                    case 2:
                        QuizService quiz = new QuizService();
                        quiz.startGame();
                        break;

                    case 3:
                        System.out.println("Zombie Survival - Coming Soon!");
                        break;

                    case 4:
                        System.out.println("Snake & Ladder - Coming Soon!");
                        break;

                    case 5:
                        System.out.println("Thank you for playing!");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid Menu Choice!");

                }

            } catch (InputMismatchException e) {

                System.out.println("Please enter numbers only.");
                sc.nextLine(); // Clear invalid input

            }

        }

    }
}