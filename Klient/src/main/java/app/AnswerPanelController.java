package app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AnswerPanelController implements Runnable {

    //private Fields
    private Player player;
    private Connection connection;
    private int round;
    //FXML Fields
    @FXML
    public AnchorPane rootPane;
    @FXML
    public Button but;
    @FXML
    public Button odpA;
    @FXML
    public Button odpC;
    @FXML
    public Button odpB;
    @FXML
    public Button odpD;
    @FXML
    public Label questionLabel;
    @FXML
    public Label nickLabel;
    @FXML
    public Label pointLabel;
    @FXML
    public Label roundLabel;

    @FXML
    void initialize(){
        odpA.setText("Odpowiedź A");
        odpB.setText("Odpowiedź B");
        odpC.setText("Odpowiedź C");
        odpD.setText("Odpowiedź D");
        roundLabel.setText("0");
        round = 0;
        nickLabel.setText(player.getNick());
        pointLabel.setText(String.valueOf(player.getPoints()));
    }

    @FXML
    public void odpButtonClicked(MouseEvent event){
        String header = "Pa+";
        try {
            if (odpA.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź A");
                header += "0";
                connection.sendMessage(header);
            } else if (odpB.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź B");
                header += "1";
                connection.sendMessage(header);
            } else if (odpC.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź C");
                header += "2";
                connection.sendMessage(header);
            } else if (odpD.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź D");
                header += "3";
                connection.sendMessage(header);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }





    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public  Connection getConnection() {
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
            player.handleResponse(response,this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
