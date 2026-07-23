package repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import model.Question;

public class QuestionRepository {

    public ArrayList<Question> getQuestions() {

        ArrayList<Question> questions = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/questions.txt"));

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\|");

                if (parts.length == 6) {

                    questions.add(new Question(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4],
                            parts[5].charAt(0)));
                }
            }

            br.close();

        } catch (IOException e) {
            System.out.println("Error reading questions file.");
        }

        return questions;
    }
}