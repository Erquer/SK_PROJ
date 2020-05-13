package app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AnswerPanelController implements Runnable {

    //private Fields
    private Player player;
    private Connection connection;
    //FXML Fields
    @FXML
    public AnchorPane rootPane;
    @FXML
    public Button but;





    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            String response = connection.read();
            System.out.println("Przyszła odpowiedź dla gracza");
            player.handleResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
