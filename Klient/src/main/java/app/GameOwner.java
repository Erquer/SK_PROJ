package app;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

/**
 * Klasa implementująca reakcje na zaptania Właściciela gry.
 */
public class GameOwner {

    private ObservableList<Player> players;
    private Game game;
    private final GameOwnerController controller;

    public GameOwner(GameOwnerController controller) {
        this.players = FXCollections.observableArrayList();
        game = new Game();
        this.controller = controller;
    }

    void handleResponse(String response){
        String header[] = response.split(":");
        System.out.println(response);
        if(response.equals("empty\n")){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Players");
                    alert.setHeaderText("There are no connected players yet");
                    alert.showAndWait();
                }
            });
        }else if(response.equals("no players\n")){
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Game");
                alert.setHeaderText("No players waiting, can't start the game");
                alert.showAndWait();
            });
        }else if(response.equals("no questions\n")){
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Game");
                alert.setHeaderText("There are no questions in game");
                alert.setContentText("Create some, to unlock the game");
            });

        }
        else if(header[0].equals("players")){
            String players[] = header[1].split(";");
            ObservableList<Player> observableList = FXCollections.observableArrayList();
            for(String player : players){
                String pl[] = player.split(",");
                observableList.add(new Player(pl[0],Integer.parseInt(pl[1].trim())));
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    controller.fillPlayerList(observableList);
                }
            });
        }else if(response.equals("started\n")){
            System.out.println(response);
        }

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
