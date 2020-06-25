package app;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa implementująca reakcje na odp. serwera dla gracza.
 */
public class Player {

    private String nick;
    private int points;
    private Map<String ,String  > yourAnswers;
    private List<Integer> answers;
    //private int actualRound;

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

    public Player(String nick, int points) {
        yourAnswers = new HashMap<>();
        answers = new ArrayList<>();
        this.nick = nick;
        this.points = points;
    }


    public void handleResponse(String response, AnswerPanelController controller){
        System.out.println(response);
        String splitResponse[] = response.split(":");

        if(response.equals("cancel\n")){
            //tworzenie gry ktoś przerwał.
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Game");
                    alert.setHeaderText("GameOwner left game creation.");
                    alert.setContentText("Closing app.");
                    alert.showAndWait();
                    System.exit(0);
                    //TODO: Przejście do panelu głównego.
                }
            });
        }else if(splitResponse[0].equals("round")){
            //nowa runda.
            //wiadomość pytania i odpowiedzi podobnie jak wysyłanie ich na serwer. header:pytanie;poprawnaOdp;ansA;ansB;ansC;ansD
            String question[] = splitResponse[1].split(";");
            Question question1 = new Question(question[1],question[3],question[4],question[5],question[6],Integer.parseInt(question[2]));
            Platform.runLater(()->{
                //dodaj pytanie, ustaw pytanie w oknie. nowa runda.
                controller.getGame().addQuestion(question1);
                controller.setQuestion(question1);
                controller.setRound(Integer.parseInt(question[0]));
            });
        }else if(splitResponse[0].equals("points")){
            //przyszła odpowiedź po rundzie z wynikiem.
            this.points = Integer.parseInt(splitResponse[1].trim());
            Platform.runLater(()->{
                controller.pointLabel.setText(String.valueOf(this.points));
            });
        }else if(splitResponse[0].equals("end")) {
                //koniec gry, otwórz podsumowanie.
            // end:<bestThree> w postaci <nick>;<points>:
            System.out.println("Kończymy grę");
            ArrayList<String > temp = new ArrayList<>();
            Platform.runLater(()->{
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("playerSummary.fxml"));
                try {
                    Parent root = loader.load();

                    for(int i = 1; i < splitResponse.length ; i++){
                        String pl[] = splitResponse[i].split(";");
                        temp.add(new String(pl[0] + ": " + pl[1]));
                    }
                    PlayerSummaryController controller1 = loader.getController();
                    //ustawianie best Three
                    controller1.bestThreeView.getItems().addAll(temp);
                    //wypełnianie pół związanych z pytaniami.
                    controller1.setQuestions(FXCollections.observableArrayList(yourAnswers.keySet()));
                    controller1.setAnswers(FXCollections.observableArrayList(yourAnswers.values()));
                    List<Question> questions = controller.getGame().getQuestionList();
                    ObservableList<String> correct = FXCollections.observableArrayList();
                    for(Question question: questions){
                        correct.add(question.getAnswers().get(question.getCorrectAnswer()));
                    }
                    controller1.setCorrect(correct);
                    controller1.nickLabel.setText(this.getNick());
                    controller1.scoreLabel.setText(String.valueOf(this.getPoints()));
                    controller.rootPane.getChildren().setAll(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


        }else if(response.equals("new\n")) {
            Platform.runLater(() ->{
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainScene.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MainSceneContoller controller1 = loader.getController();
                controller1.setConnection(controller.getConnection());
                controller.setRun_prog(false);
                controller.rootPane.getChildren().setAll(root);


            });

        }else{
            System.out.println(response);
        }
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Map<String, String> getYourAnswers() {
        return yourAnswers;
    }

    //public void setYourAnswers(List<Integer> yourAnswers) {
   //     this.yourAnswers = (Map<String, String>) yourAnswers;
    //}

    @Override
    public String toString() {
        return this.nick + " " + this.points;
    }
}
