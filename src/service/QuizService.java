package service;

import java.util.ArrayList;
import java.util.Scanner;

import model.Game;
import model.Player;
import model.Question;
import repository.QuestionRepository;
import util.InvalidChoiceException;

public class QuizService extends Game {

    Scanner sc = new Scanner(System.in);

    @Override
    public void startGame() {

        System.out.println("\n==================================");
        System.out.println("      WELCOME TO QUIZ BATTLE");
        System.out.println("==================================");

        System.out.print("Enter Player Name: ");
        String name = sc.nextLine();

        Player player = new Player(name);

        QuestionRepository repository = new QuestionRepository();
        ArrayList<Question> questions = repository.getQuestions();

        for (Question q : questions) {

            System.out.println("\n" + q.getQuestion());
            System.out.println("A. " + q.getOptionA());
            System.out.println("B. " + q.getOptionB());
            System.out.println("C. " + q.getOptionC());
            System.out.println("D. " + q.getOptionD());

            try {

                System.out.print("Enter your answer (A/B/C/D): ");
                char answer = Character.toUpperCase(sc.next().charAt(0));

                if (answer != 'A' && answer != 'B' &&
                    answer != 'C' && answer != 'D') {

                    throw new InvalidChoiceException(
                        "Invalid Choice! Please enter only A, B, C or D."
                    );
                }

                if (answer == q.getCorrectAnswer()) {
                    System.out.println("Correct Answer!");
                    player.increaseScore();
                } else {
                    System.out.println("Wrong Answer!");
                    System.out.println("Correct Answer : " + q.getCorrectAnswer());
                }

            } catch (InvalidChoiceException e) {

                System.out.println(e.getMessage());

            }

        }

        System.out.println("\n==================================");
        System.out.println("Quiz Completed!");
        System.out.println("Player Name : " + player.getName());
        System.out.println("Final Score : " + player.getScore()
                           + " / " + questions.size());

        if (player.getScore() >= 4) {
            System.out.println("Excellent Performance!");
        } else if (player.getScore() >= 2) {
            System.out.println("Good Job!");
        } else {
            System.out.println("Better Luck Next Time!");
        }

        System.out.println("==================================");
    }
}