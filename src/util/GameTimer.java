package util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Monotonic deadline plus a scheduled countdown, always closed at the end of a game. */
public final class GameTimer implements AutoCloseable {
    private final long deadline;
    private final ScheduledExecutorService worker;
    public GameTimer(int seconds) {
        if (seconds < 1) throw new IllegalArgumentException("Duration must be positive");
        deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
        worker = Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "game-countdown");
            thread.setDaemon(true);
            return thread;
        });
        worker.scheduleAtFixedRate(() -> {
            long remaining = remainingSeconds();
            if (remaining > 0 && (remaining <= 5 || remaining % 10 == 0))
                System.out.println("\n[Timer: " + remaining + " seconds remaining]");
        }, 1, 1, TimeUnit.SECONDS);
    }
    public boolean isActive() { return System.nanoTime() < deadline; }
    public long remainingSeconds() {
        return Math.max(0, (deadline - System.nanoTime() + 999_999_999L) / 1_000_000_000L);
    }
    @Override public void close() { worker.shutdownNow(); }
}
