package app;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.util.Duration;

import java.io.IOException;

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

        }else if(response.equals("ready\n")){
            Platform.runLater(() -> {
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        controller.startButton.setDisable(false);
                        controller.stateLabel.setText("Waiting For Players");
                    }
                }));
                timeline.setCycleCount(1);
                timeline.play();
            });
        }else if(response.equals("started\n")){
            Platform.runLater(()->{
                controller.stateLabel.setText("Started");
            });
            System.out.println(response);
        }else if(header[0].equals("players")){
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
        }else if(header[0].equals("round")){
            //wiadomość, że rozpoczęła się dana runda. GameOwner Dostaje wyniki wszystkich graczy.
            Platform.runLater(()->{
                try {
                    controller.getConnection().sendMessage("Gp+");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }else if(header[0].equals("end")){
            //koniec gry, serwer przysyła nam wszystkie dane użytkowników.
            String playersResults[] = header[1].split("\\|");
            ObservableList<Object> playerList = FXCollections.emptyObservableList();
            for(String player:playersResults){
                String playerScore[] = player.split(";");
                Player player1 = new Player(playerScore[0],Integer.parseInt(playerScore[playerScore.length-1]));
                for(int i = 1; i < playerScore.length-1; i++){
                    player1.getAnswers().add(Integer.parseInt(playerScore[i]));
                }
                playerList.add(player1);
                Platform.runLater(()->{

                });

            }

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
