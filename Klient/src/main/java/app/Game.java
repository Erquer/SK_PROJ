package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Game {
    //lista zapisująca pytania w danych rundach.
    private ObservableList<Question> questionList;
    //lista wypełniania na koniec gry dla graczy, u których pokazywane są 3 najlepsze miejsca.
    private ObservableList<String> bestScores;

    public Game() {
        this.questionList = FXCollections.observableArrayList();
        this.bestScores = FXCollections.observableArrayList();
    }

    public void addQuestion(Question question){
        questionList.add(question);
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(ObservableList<Question> questionList) {
        this.questionList = questionList;
    }

    public ObservableList<String> getBestScores() {
        return bestScores;
    }

    public void setBestScores(ObservableList<String> bestScores) {
        this.bestScores = bestScores;
    }
}
