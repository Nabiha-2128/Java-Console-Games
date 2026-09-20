package service;

import java.util.ArrayList;
import java.util.Collections;
import model.*;
import repository.QuestionRepository;
import util.*;

public class QuizService extends Game {
    private final int timeOverride;
    public QuizService() { this(0); }
    public QuizService(int timeOverride) { this.timeOverride = timeOverride; }
    @Override public void startGame() {
        ConsoleInput input = ConsoleInput.getInstance();
        System.out.println("\nWELCOME TO QUIZ BATTLE");
        Player player = new Player(input.readName("Enter Player Name: "));
        int level = input.readInt("Difficulty: 1 Easy | 2 Medium | 3 Hard: ", 1, 3);
        String difficulty = new String[]{"EASY", "MEDIUM", "HARD"}[level - 1];
        ArrayList<Question> questions = new QuestionRepository().getQuestions(difficulty);
        if (questions.isEmpty()) { System.out.println("No questions available for " + difficulty + ". Check data/questions.txt."); return; }
        Collections.shuffle(questions);
        int seconds = timeOverride > 0 ? timeOverride : new int[]{90, 75, 60}[level - 1];
        System.out.println("Answer " + questions.size() + " questions in " + seconds + " seconds. +1 per correct answer. Q returns to menu.");
        int answered = 0;
        try (GameTimer timer = new GameTimer(seconds)) {
            for (Question q : questions) {
                if (!timer.isActive()) break;
                System.out.println("\n" + q.getQuestion() + "\nA. " + q.getOptionA() + "\nB. " + q.getOptionB()
                    + "\nC. " + q.getOptionC() + "\nD. " + q.getOptionD());
                while (timer.isActive()) {
                    String raw = input.readWhile("Answer (A/B/C/D or Q): ", timer::isActive);
                    if (raw == null) break;
                    if (raw.equalsIgnoreCase("q")) { System.out.println("Quiz cancelled. Score: " + player.getScore()); return; }
                    try {
                        char answer = Validation.answer(raw);
                        answered++;
                        if (answer == q.getCorrectAnswer()) { player.increaseScore(); System.out.println("Correct Answer!"); }
                        else System.out.println("Wrong Answer! Correct answer: " + q.getCorrectAnswer());
                        break;
                    } catch (InvalidChoiceException e) { System.out.println(e.getMessage()); }
                }
            }
            System.out.println(answered == questions.size() ? "Quiz completed!" : "Time is up!");
        }
        System.out.println("Player: " + player.getName() + " | Answered: " + answered + "/" + questions.size());
        System.out.println("Final Score: " + player.getScore() + " / " + questions.size());
        double ratio = (double) player.getScore() / questions.size();
        System.out.println(ratio >= 0.8 ? "Excellent Performance!" : ratio >= 0.4 ? "Good Job!" : "Keep practising!");
    }
}
