package app;

import javafx.application.Platform;
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
    private int lastRound;
    private Game game;
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

    public void setRun_prog(boolean run_prog) {
        this.run_prog = run_prog;
    }

    private boolean run_prog = true;


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
        lastRound = 0;
        game = new Game();
        setButtons(true);

    }

    @FXML
    public void odpButtonClicked(MouseEvent event){
        String header = "Pa+" + round + ":";
        try {
            if (odpA.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź A");
                header += "0";
                player.getYourAnswers().put(questionLabel.getText(),odpA.getText());
                connection.sendMessage(header);
            } else if (odpB.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź B");
                header += "1";
                player.getYourAnswers().put(questionLabel.getText(),odpB.getText());
                connection.sendMessage(header);
            } else if (odpC.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź C");
                header += "2";
                player.getYourAnswers().put(questionLabel.getText(),odpC.getText());
                connection.sendMessage(header);
            } else if (odpD.equals(event.getSource())) {
                System.out.println("Kliknieto odpowiedź D");
                header += "3";
                player.getYourAnswers().put(questionLabel.getText(),odpD.getText());
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
        roundLabel.setText(String.valueOf(round));
    }
    @Override
    public void run() {
        while (run_prog) {
            try {
                String response = connection.read();
                //System.out.println("Przyszła odpowiedź dla gracza");
                player.handleResponse(response, this);
                if(response.equals("new\n")) break;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (lastRound < round) {
                            setButtons(false);
                            lastRound = round;
                        }
                        //  roundLabel.setText(String.valueOf(round));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println("PlayerThread stop working.");
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
