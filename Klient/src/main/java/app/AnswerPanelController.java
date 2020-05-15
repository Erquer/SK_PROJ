package app;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AnswerPanelController implements Runnable {


    //private Fields
    private Player player;
    private Connection connection;
    private int round;
    private Game game;
    //FXML Fields
    @FXML
    public TableView answersTable;
    @FXML
    public TableColumn questionColumn;
    @FXML
    public TableColumn answerColumn;
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


    public Label getNickLabel() {
        return nickLabel;
    }

    public Label getPointLabel() {
        return pointLabel;
    }

    @FXML
    void initialize(){
        odpA.setText("Odpowiedź A");
        odpB.setText("Odpowiedź B");
        odpC.setText("Odpowiedź C");
        odpD.setText("Odpowiedź D");
        roundLabel.setText("0");
        pointLabel.setText("0");
        round = 0;
        game = new Game();

    }

    @FXML
    public void odpButtonClicked(MouseEvent event){
        String header = "Pa+" + round + ":";
        try {
            if (odpA.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź A");
                header += "0";
                player.getYourAnswers().put(new SimpleStringProperty(questionLabel.getText()),new SimpleStringProperty(odpA.getText()));
                connection.sendMessage(header);
            } else if (odpB.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź B");
                header += "1";
                player.getYourAnswers().put(new SimpleStringProperty(questionLabel.getText()),new SimpleStringProperty(odpB.getText()));
                connection.sendMessage(header);
            } else if (odpC.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź C");
                header += "2";
                player.getYourAnswers().put(new SimpleStringProperty(questionLabel.getText()),new SimpleStringProperty(odpC.getText()));
                connection.sendMessage(header);
            } else if (odpD.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź D");
                header += "3";
                player.getYourAnswers().put(new SimpleStringProperty(questionLabel.getText()),new SimpleStringProperty(odpD.getText()));
                connection.sendMessage(header);
            }
            //answersTable.getItems().add(player.getYourAnswers().get(questionLabel.getText()));
            setButtons(true);
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
    @Override
    public void run() {
        try {
            String response = connection.read();
            System.out.println("Przyszła odpowiedź dla gracza");
            player.handleResponse(response,this);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(Integer.parseInt(roundLabel.getText()) < round){
                        setButtons(false);
                    }
                    roundLabel.setText(String.valueOf(round));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setButtons(boolean state){
        odpA.setDisable(state);
        odpB.setDisable(state);
        odpC.setDisable(state);
        odpD.setDisable(state);
    }

    public void setQuestion(Question questio){
        questionLabel.setText(questio.getQuestion() + '?');
        odpA.setText(questio.getAnswers().get(0));
        odpB.setText(questio.getAnswers().get(1));
        odpC.setText(questio.getAnswers().get(2));
        odpD.setText(questio.getAnswers().get(3));

    }
}
