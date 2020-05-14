package app;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.List;

/**
 * Klasa implementująca reakcje na odp. serwera dla gracza.
 */
public class Player {

    private String nick;
    private int points;
    private List<Integer> yourAnswers;
    private int actualRound;

    public Player(String nick, int points) {
        this.nick = nick;
        this.points = points;
    }


    public void handleResponse(String response, AnswerPanelController controller){
        System.out.println(response);

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

    public List<Integer> getYourAnswers() {
        return yourAnswers;
    }

    public void setYourAnswers(List<Integer> yourAnswers) {
        this.yourAnswers = yourAnswers;
    }

    @Override
    public String toString() {
        return this.nick + " " + this.points;
    }
}
