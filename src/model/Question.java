package model;

public class Question {

    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private char correctAnswer;

    public Question(String question, String optionA, String optionB,
                    String optionC, String optionD, char correctAnswer) {

        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = Character.toUpperCase(correctAnswer);
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public char getCorrectAnswer() {
        return correctAnswer;
    }
}