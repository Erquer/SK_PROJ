package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;



public class AddQuestionController {

    // ==== FXML Fields ====
    @FXML
    public Group radioGroup;
    @FXML
    public TextField question;
    @FXML
    public TextField aAnswer;
    @FXML
    public TextField bAnswer;
    @FXML
    public TextField cAnswer;
    @FXML
    public Button addButton;
    @FXML
    public Button cancelButton;
    @FXML
    public TextField dAnswer;
    @FXML
    public RadioButton d;
    @FXML
    public RadioButton c;
    @FXML
    public RadioButton b;
    @FXML
    public RadioButton a;

    ToggleGroup group = new ToggleGroup();

    private ObservableList<Question> mainList= FXCollections.observableArrayList();

    public void setMainList(ObservableList<Question> mainList) {
        this.mainList = mainList;
    }

    @FXML
    void initialize(){
        addList(question,99);
        addList(aAnswer,20);
        addList(bAnswer,20);
        addList(cAnswer,20);
        addList(dAnswer,20);
        a.setToggleGroup(group);
        b.setToggleGroup(group);
        c.setToggleGroup(group);
        d.setToggleGroup(group);
        a.setSelected(true);
    }
    @FXML
    public void addQuestionClicker(MouseEvent event){
        System.out.println("Adding new Question");
        if(!question.getText().isBlank() && !aAnswer.getText().isBlank() && !bAnswer.getText().isBlank() && !cAnswer.getText().isBlank() && !dAnswer.getText().isBlank()){
            //wszystkie są jakoś zapełnione, teoretycznie są poprawne.
            String st = ((RadioButton)group.getSelectedToggle()).getText();
            char ch = st.charAt(0);
            int ans = ch - 65;
            Question temp = new Question(question.getText().trim(),aAnswer.getText().trim(),bAnswer.getText().trim(),cAnswer.getText().trim(),dAnswer.getText().trim(),ans);
            mainList.add(temp);

            closeStage(event);

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Puste Pole");
            alert.setHeaderText("Żadne polenie może być puste, proszę uzupełnić brakujące.");
        }

    }

    private void addList(TextField textField, int limit){
        textField.textProperty().addListener((ov,odlValue,newValue)-> {
            String temp = textField.getText();

            if (temp.length() > limit) {
                String n = textField.getText().substring(0, limit);
                textField.setText(n);
            } else {
                if (!newValue.matches("[a-zA-Z0-9 ]")) { // zabokowanie znaków niechcianych
                    textField.setText(newValue.replaceAll("[^a-zA-Z0-9 ]", ""));
                }
            }
        });
    }
    private void closeStage(MouseEvent event){
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
