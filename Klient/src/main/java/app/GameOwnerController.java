package app;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GameOwnerController implements Runnable {


    // ==== Private Fields ====
    private Connection connection;
    private final GameOwner gameOwner = new GameOwner(this);

    // ==== FXML Fields ====
    @FXML
    public Label stateLabel;
    @FXML
    public Button startButton;
    @FXML
    public Button getPlayersButton;
    @FXML
    public Button addQuestionButton;
    @FXML
    public Button readyButton;
    @FXML
    public ListView<Player> playerList;
    @FXML
    public ListView<Question> questionList;

    private ObservableList<Question> questions = FXCollections.observableArrayList();
    private ObservableList<Player> players = FXCollections.observableArrayList();

    @FXML
    void initialize(){
        questions.addListener(new ListChangeListener<Question>() {
            @Override
            public void onChanged(Change<? extends Question> c) {
                if(c.next()){
                    if(c.wasAdded()){
                        if(!questionList.getItems().isEmpty())
                            questionList.getItems().remove(0,questionList.getItems().size());
                        questionList.getItems().addAll(c.getList());

                        Question question = questions.get(questions.size()-1);
                        gameOwner.getGame().addQuestion(question);
                        String header = "Gq+";
                        String sendQuestionString = header + question.getQuestion();
                        for(String ans:question.getAnswers()){
                            sendQuestionString += ';' + ans;
                        }
                        sendQuestionString += ';' + String.valueOf(question.getCorrectAnswer());
                        System.out.println(sendQuestionString);
                        try {
                            connection.sendMessage(sendQuestionString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        players.addListener(new ListChangeListener<Player>() {
            @Override
            public void onChanged(Change<? extends Player> c) {
                if(c.next()){
                    if(c.wasAdded()){
                        if(!playerList.getItems().isEmpty()){
                            playerList.getItems().remove(0,playerList.getItems().size());
                        }
                        playerList.getItems().addAll(c.getList());
                    }
                }
            }
        });
        stateLabel.setText("Creation");
        startButton.setDisable(true);
    }


    @FXML
    public void onButtonClicked(Event event) {
        //System.out.println("dzialam");
        try {
            //wysłanie sygnału, że gra oczekuje na graczy i po ustalonym czasie się zacznie.
            connection.sendMessage("Gs+start");
        } catch (IOException e) {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CONNECTION");
            alert.setHeaderText("Connection problems");
            alert.showAndWait();
        }
    }
    @FXML
    public void onReadyButtonClicked(Event event){
        //wysyła znak, że gra jest gotowa i może oczekiwać na graczy.
        try {
            connection.sendMessage("Gs+ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addQuestion(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("addQuestion.fxml"));
        Parent parent = fxmlLoader.load();
        AddQuestionController dialogController = fxmlLoader.getController();
        dialogController.setMainList(questions);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }
    @FXML
    public void getPlayers(MouseEvent event){
        try {
            connection.sendMessage("Gp+");
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection");
            alert.setHeaderText("Utracono połączenie");
            alert.setContentText("Aplikacja zostanie zamknięta");
            alert.showAndWait();
            System.exit(0);
        }


    }

    // ==== Getters & Setters ====
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    void fillPlayerList(ObservableList<Player> list){
        if(!players.isEmpty()){
            players.remove(0,players.size());
        }
        players.addAll(list);
    }

    @Override
    public void run() {

        try {
            while (true){
                String response = connection.read();
                System.out.println("Przyszła odpowiedź");
                gameOwner.handleResponse(response);

            }
        }catch (IOException e){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection");
                    alert.setHeaderText("Utracono połączenie");
                    alert.showAndWait();
                }
            });

        }

    }

    public ObservableList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ObservableList<Player> players) {
        this.players = players;
    }
}
