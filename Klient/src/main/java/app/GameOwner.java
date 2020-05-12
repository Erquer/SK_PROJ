package app;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Klasa implementująca reakcje na zaptania Właściciela gry.
 */
public class GameOwner {

    private ObservableList<Player> players;
    private Game game;

    public GameOwner() {
        this.players = FXCollections.observableArrayList();
        game = new Game();
    }

    void handleResponse(String response){
        System.out.println(response);
    }



    // ==== Getters & Setters ====
    public ObservableList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ObservableList<Player> players) {
        this.players = players;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
