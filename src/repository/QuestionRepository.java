package repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import model.Question;

public class QuestionRepository {
    private final Path path;
    public QuestionRepository() { this(Path.of("data", "questions.txt")); }
    public QuestionRepository(Path path) { this.path = path; }
    public ArrayList<Question> getQuestions() { return getQuestions("ALL"); }
    public ArrayList<Question> getQuestions(String difficulty) {
        ArrayList<Question> questions = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line; int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] parts = line.split("\\|", -1);
                // Preserve compatibility with the original six-column question files.
                String level = parts.length == 7 ? parts[0].trim().toUpperCase(java.util.Locale.ROOT) : "EASY";
                int offset = parts.length == 7 ? 1 : 0;
                boolean valid = (parts.length == 6 || parts.length == 7)
                    && (level.equals("EASY") || level.equals("MEDIUM") || level.equals("HARD"));
                if (valid) {
                    for (String part : parts) if (part.isBlank()) valid = false;
                    valid = valid && parts[offset + 5].trim().matches("(?i)[ABCD]");
                }
                if (!valid) { System.out.println("Skipping malformed question at line " + number + "."); continue; }
                if (!difficulty.equalsIgnoreCase("ALL") && !difficulty.equalsIgnoreCase(level)) continue;
                questions.add(new Question(parts[offset].trim(), option(parts[offset + 1]), option(parts[offset + 2]),
                    option(parts[offset + 3]), option(parts[offset + 4]), parts[offset + 5].trim().charAt(0)));
            }
        } catch (IOException e) { System.out.println("Cannot read " + path + ": " + e.getMessage()); }
        return questions;
    }
    private String option(String text) { return text.trim().replaceFirst("^[A-Da-d][).]\\s*", ""); }
}
