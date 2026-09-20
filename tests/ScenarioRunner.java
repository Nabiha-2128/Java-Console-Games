import java.util.Random;
import service.*;

/** Short deterministic sessions for the Python integration runner. */
public class ScenarioRunner {
    public static void main(String[] args) throws Exception {
        switch (args[0]) {
            case "treasure-timeout": new TreasureService(new Random(1), 1).startGame(); break;
            case "quiz-timeout": new QuizService(1).startGame(); break;
            case "zombie-timeout": new ZombieService(1, 4000).startGame(); break;
            case "zombie-death": new ZombieService(10, 100).startGame(); break;
            case "treasure-win":
                new TreasureService(new Random() {
                    @Override public int nextInt(int bound) { return 3; }
                }, 30).startGame(); break;
            case "snake-win": new SnakeLadderService(new Random(7)).startGame(); break;
            default: throw new IllegalArgumentException("Unknown scenario");
        }
        Thread.sleep(150);
        boolean leaked = Thread.getAllStackTraces().keySet().stream().anyMatch(t -> t.isAlive()
            && (t.getName().equals("game-countdown") || t.getName().equals("zombie-spawner")));
        if (leaked) throw new AssertionError("Game worker leaked");
        System.out.println("SCENARIO COMPLETE; WORKERS STOPPED");
    }
}
