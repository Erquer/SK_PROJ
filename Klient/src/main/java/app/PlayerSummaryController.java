package app;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class PlayerSummaryController {

    @FXML
    public ListView<String> questionView;
    @FXML
    public ListView<String> answerView;
    @FXML
    public ListView<String> correctView;
    @FXML
    public Button exitButton;
    @FXML
    public ListView<String> bestThreeView;
    @FXML
    public Label nickLabel;


    @FXML
    public Label scoreLabel;

    public void setQuestions(ObservableList<String> questions) {
        this.questionView.getItems().addAll(questions);
    }

    public void setAnswers(ObservableList<String> answers) {
        this.answerView.getItems().addAll(answers);
    }

    public void setCorrect(ObservableList<String> correct) {
        this.correctView.getItems().addAll(correct);
    }


    @FXML
    void initialize(){
        exitButton.setOnMouseClicked(event->{
            System.exit(0);
        });
    }


}
