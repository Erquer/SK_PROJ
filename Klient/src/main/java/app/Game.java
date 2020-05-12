package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Game {
    //lista zapisująca pytania w danych rundach.
    private List<Question> questionList;
    //lista wypełniania na koniec gry dla graczy, u których pokazywane są 3 najlepsze miejsca.
    private ObservableList<String> bestScores;

    public Game() {
        this.questionList = new ArrayList<>();
        this.bestScores = FXCollections.observableArrayList();
    }
}
