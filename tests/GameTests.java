import model.*;
import repository.QuestionRepository;
import util.*;
import java.nio.file.*;

/** Dependency-free regression tests; throws AssertionError even without -ea. */
public class GameTests {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    public static void main(String[] args) throws Exception {
        SnakeBoard board = new SnakeBoard();
        check(board.move(0, 4) == 14, "Ladder");
        check(board.move(11, 6) == 7, "Snake");
        check(board.move(98, 3) == 98, "Overshoot stays put");
        check(board.move(98, 2) == 100, "Exact finish");
        check(board.move(0, 1) == 1, "Ordinary move");
        try { board.move(0, 7); throw new AssertionError("Invalid dice accepted"); }
        catch (IllegalArgumentException expected) { checks++; }

        SurvivalState state = new SurvivalState();
        state.heal();
        check(state.getMedkits() == 3, "Full health does not waste medkit");
        state.attack(false);
        check(state.getScore() == 0 && state.getZombieCount() == 1, "Bat requires two hits");
        state.attack(false);
        check(state.getScore() == 10 && state.getZombieCount() == 0, "Bat kill scores once");
        state.attack(true);
        check(state.getAmmunition() == 6, "Empty target does not consume ammo");
        for (int i = 0; i < 6; i++) { state.spawnAndAttack(); state.attack(true); }
        check(state.getAmmunition() == 0 && state.getScore() == 70, "Pistol ammo and score");
        state.spawnAndAttack(); state.attack(true);
        check(state.getZombieCount() == 1 && state.getAmmunition() == 0, "No negative ammo");
        state.heal();
        check(state.getHealth() == 95 && state.getMedkits() == 2, "Healing respects quantities");
        for (int i = 0; i < 30; i++) state.spawnAndAttack();
        check(state.getHealth() == 0 && !state.isAlive(), "Death clamps health");
        state.heal();
        check(state.getHealth() == 0, "Cannot resurrect after death");

        check(Validation.answer("b") == 'B', "Lowercase answer");
        for (String invalid : new String[]{"", "AB", "apple", "8"}) {
            try { Validation.answer(invalid); throw new AssertionError("Invalid answer accepted"); }
            catch (InvalidChoiceException expected) { checks++; }
        }
        try { Validation.number("9999999999999999", 1, 8); throw new AssertionError("Overflow accepted"); }
        catch (InvalidChoiceException expected) { checks++; }

        Path file = Files.createTempFile("quiz-test", ".txt");
        try {
            Files.writeString(file, "# comment\n\nLegacy?|A) one|B) two|C) three|D) four|B\n"
                + "HARD|Hard?|one|two|three|four|D\n"
                + "EASY|Bad?|one|two|three|four|\n"
                + "garbage\nUNKNOWN|Bad level?|a|b|c|d|A\n");
            QuestionRepository repository = new QuestionRepository(file);
            check(repository.getQuestions().size() == 2, "Malformed rows skipped, legacy supported");
            check(repository.getQuestions("HARD").size() == 1, "Difficulty filter");
            check(repository.getQuestions("EASY").get(0).getOptionA().equals("one"), "Legacy option labels stripped");
        } finally { Files.delete(file); }
        for (String level : new String[]{"EASY", "MEDIUM", "HARD"})
            check(new QuestionRepository().getQuestions(level).size() == 5, "Five questions for " + level);
        check(new QuestionRepository(Path.of("missing-question-file.txt")).getQuestions().isEmpty(), "Missing file safe");

        try (GameTimer timer = new GameTimer(1)) {
            check(timer.isActive(), "Timer starts active");
            Thread.sleep(1150);
            check(!timer.isActive() && timer.remainingSeconds() == 0, "Timer expires");
        }
        Thread.sleep(100);
        check(Thread.getAllStackTraces().keySet().stream().noneMatch(t -> t.isAlive() && t.getName().equals("game-countdown")), "Timer thread cleaned up");
        System.out.println("PASS: " + checks + " checks");
    }
}
