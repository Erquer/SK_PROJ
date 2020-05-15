package app;

import javafx.collections.FXCollections;

import java.util.List;

/**
 * Klasa przechowująca dane nt. aktualnego pytania otrzymywane z serwera.
 * Fields:
 *  question - pytanie w danej rundzie.
 *  answers - Lista przechowująca odpowiedzi do pytania.
 *  yourAnswer - numer odp. na które dany gracz odpowiedział pyatnia
 */
public class Question {
    private  String question;
    private final List<String> answers;

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public Question(String question, String a, String b, String c, String d, int correctAnswer) {
        this.answers = FXCollections.observableArrayList();
        this.question = question;
        this.answers.addAll(List.of(a,b,c,d));
        this.correctAnswer = correctAnswer;
    }

    private int correctAnswer;

    public Question() {
        answers = FXCollections.observableArrayList();
    }

    @Override
    public String toString() {
        return this.question + " " + correctAnswer;
    }
}
