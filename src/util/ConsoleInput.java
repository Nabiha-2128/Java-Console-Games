package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/** The only System.in reader. A daemon reader lets a game expire while input is idle. */
public final class ConsoleInput {
    private static final ConsoleInput INSTANCE = new ConsoleInput();
    private final BlockingQueue<String> lines = new LinkedBlockingQueue<>();
    private volatile boolean ended;

    public static ConsoleInput getInstance() { return INSTANCE; }

    private ConsoleInput() {
        Thread reader = new Thread(() -> {
            try {
                BufferedReader input = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8));
                String line;
                while ((line = input.readLine()) != null) lines.put(line);
            } catch (IOException e) {
                System.out.println("Input unavailable: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally { ended = true; }
        }, "console-input");
        reader.setDaemon(true);
        reader.start();
    }

    public String read(String prompt) { return readWhile(prompt, () -> true); }

    /** Returns null when the game stops. EOF is distinct from a timeout. */
    public String readWhile(String prompt, BooleanSupplier active) {
        System.out.print(prompt);
        System.out.flush();
        while (active.getAsBoolean()) {
            try {
                String line = lines.poll(50, TimeUnit.MILLISECONDS);
                if (!active.getAsBoolean()) return null;
                if (line != null) return line.trim();
                if (ended && lines.isEmpty()) throw new EndOfInput();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new EndOfInput();
            }
        }
        return null;
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            try { return Validation.number(read(prompt), min, max); }
            catch (InvalidChoiceException e) { System.out.println(e.getMessage()); }
        }
    }

    public String readName(String prompt) {
        while (true) {
            String name = read(prompt);
            if (!name.isEmpty() && name.length() <= 40) return name;
            System.out.println("Enter a name containing 1 to 40 characters.");
        }
    }

    public static final class EndOfInput extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
