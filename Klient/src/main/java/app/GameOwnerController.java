package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
    public void addQuestion(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addQuestion.fxml"));
        Parent parent = fxmlLoader.load();
        AddQuestionController dialogController = fxmlLoader.<AddQuestionController>getController();
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
            e.printStackTrace();
        }

    }
}
