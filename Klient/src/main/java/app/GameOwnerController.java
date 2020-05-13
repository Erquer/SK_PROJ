package app;

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
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GameOwnerController implements Runnable {
    // ==== Private Fields ====
    private Connection connection;
    private final GameOwner gameOwner = new GameOwner();

    // ==== FXML Fields ====
    @FXML
    public Button startButton;
    @FXML
    public Button getPlayersButton;
    @FXML
    public Button addQuestionButton;
    @FXML
    public ListView<Player> playerList;
    @FXML
    public ListView<Question> questionList;

    private ObservableList<Question> questions = FXCollections.observableArrayList();

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
    }



    public void onButtonClicked(Event event) {
        //System.out.println("dzialam");
        try {
            connection.sendMessage("dzialam");

        } catch (IOException e) {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CONNECTION");
            alert.setHeaderText("Connection problems");
            alert.showAndWait();
        }
    }

    @FXML
    public void addQuestion(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("addQuestion.fxml"));
        Parent parent = fxmlLoader.load();
        AddQuestionController dialogController = fxmlLoader.getController();
        dialogController.setMainList(questions);

        Scene scene = new Scene(parent, 300, 200);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // ==== Getters & Setters ====
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection");
            alert.setHeaderText("Utracono połączenie");
            alert.setContentText("Nastąpi próba połączenia, jeżeli się nie powiedzie, aplikacja zostanie zamknięta");
            alert.showAndWait();
            try {
                connection = new Connection(connection.getIp(),connection.getPort());
            } catch (IOException ioException) {
                System.exit(0);
            }
            e.printStackTrace();
        }

    }
}
