package app;

import java.util.List;

/**
 * Klasa implementująca reakcje na odp. serwera dla gracza.
 */
public class Player {

    private String nick;
    private int points;
    private List<Integer> yourAnswers;

    public Player(String nick, int points) {
        this.nick = nick;
        this.points = points;
    }


    public void handleResponse(String response){
        System.out.println(response);
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
