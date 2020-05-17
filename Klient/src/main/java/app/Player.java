package app;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa implementująca reakcje na odp. serwera dla gracza.
 */
public class Player {

    private String nick;
    private int points;
    private Map<SimpleStringProperty,SimpleStringProperty > yourAnswers;
    private List<Integer> answers;
    //private int actualRound;

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

    public Player(String nick, int points) {
        yourAnswers = new HashMap<>();
        answers = new ArrayList<>();
        this.nick = nick;
        this.points = points;
    }


    public void handleResponse(String response, AnswerPanelController controller){
        System.out.println(response);
        String splitResponse[] = response.split(":");

        if(response.equals("cancel\n")){
            //tworzenie gry ktoś przerwał.
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Game");
                    alert.setHeaderText("GameOwner opóścił tworzenie gry.");
                    alert.showAndWait();
                }
            });
        }else if(splitResponse[0].equals("round")){
            //nowa runda.
            //wiadomość pytania i odpowiedzi podobnie jak wysyłanie ich na serwer. header:pytanie;poprawnaOdp;ansA;ansB;ansC;ansD
            String question[] = splitResponse[1].split(";");
            Question question1 = new Question(question[1],question[3],question[4],question[5],question[6],Integer.parseInt(question[2]));
            Platform.runLater(()->{
                //dodaj pytanie, ustaw pytanie w oknie. nowa runda.
                controller.getGame().addQuestion(question1);
                controller.setQuestion(question1);
                controller.setRound(Integer.parseInt(question[0]));
            });
        }else if(splitResponse[0].equals("points")){
            //przyszła odpowiedź po rundzie z wynikiem.
            this.points = Integer.parseInt(splitResponse[1]);
        }else {
            System.out.println(response);
        }
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Map<SimpleStringProperty, SimpleStringProperty> getYourAnswers() {
        return yourAnswers;
    }

    //public void setYourAnswers(List<Integer> yourAnswers) {
   //     this.yourAnswers = (Map<String, String>) yourAnswers;
    //}

    @Override
    public String toString() {
        return this.nick + " " + this.points;
    }
}
