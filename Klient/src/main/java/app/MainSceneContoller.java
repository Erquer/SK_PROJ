package app;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Optional;

public class MainSceneContoller {
    // ==== Private Fields ====
    private Connection connection;

    // ==== FXML Fields ====
    @FXML
    public Button createButton;
    public Button playButton;
    public AnchorPane rootPane;

    // ==== Public & FXML Methods ====
    @FXML
    void initialize(){

        // tworzenie gry
        /*
            Sprawdzanie dostępnej gry.
            Otwarcie okna do ustalenia PIN'u.

         */
        createButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                var pinDialog = new TextInputDialog();
                pinDialog.setGraphic(null);
                pinDialog.setHeaderText("Wprowadź PIN do gry");
                pinDialog.setTitle("PIN");

                pinDialog.getEditor().textProperty().addListener((ov,oldValue,newValue) -> {
                    String portNumber = pinDialog.getEditor().getText();

                    if(portNumber.length()>5){
                        String s= pinDialog.getEditor().getText().substring(0,5);
                        pinDialog.getEditor().setText(s);
                    }else{
                        if(!newValue.matches("[0-9]")){ //zapobieganie wprowadzania czegoś innego niż liczby
                            pinDialog.getEditor().setText(newValue.replaceAll("[^0-9]",""));
                        }
                    }
                });

                //wysłanie na serwer wiadomości o
                try {
                    String confirm =  connection.read();
                    String PIN = "";
                    //wejściowa wiadomość, jeżeli jest, znaczy, że mamy dobre połączenie.
                    System.out.println(confirm);
                    int i = 0;
                    while (! confirm.equals("accepted\n")){
                        var opPIN = pinDialog.showAndWait();
                        if(opPIN.isPresent()){
                            PIN = opPIN.get();
                            connection.sendMessage("Cn+" + PIN);
                            confirm = connection.read();
                            //przypadek, kiedy ktoś już tworzy grę
                            if(confirm.equals("creating\n")){
                                var alert = new Alert(Alert.AlertType.WARNING);
                                alert.setHeaderText("Ktoś już tworzy grę!");
                                alert.setTitle("Tworzenie gry");
                                alert.showAndWait();
                                break;
                            }else if(confirm.equals("accepted\n")){
                                break;
                            }
                        }
                        i++;
                        System.out.println(confirm  + " poraz: " + i);
                    }
                    System.out.println("Accepted creation by message: " + confirm);
                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gameOwner.fxml"));
                    AnchorPane root = loader.load();
                    GameOwnerController controller = loader.getController();
                    controller.setConnection(connection);
                    rootPane.getChildren().setAll(root);


//                    Main.setLoader(loader);
//                    Main.getStage().setScene(scene);
                    //przejdzie do sceny tworzenia gry.
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                var nickDialog = new TextInputDialog();
                String welcomeMessage;
                try {
                    //powitalna wiadomość.
                    welcomeMessage = connection.read();
                    System.out.println(welcomeMessage);
                    //wysłanie zapytania o dołączenie do gry
                    Pair<String, String > loginData;
                    //System.out.println(loginData.getKey() + " " + loginData.getValue());
                    if((loginData = showDialog()) != null){
                        // wprowadzono pin i nick
                        String connString = "Cj+";
                        connString += loginData.getKey() + ";" + loginData.getValue();
                        //wysłanie wiadomości połączeniowej: j - join + - separator, nick ;-separator wiadomości, PIN do gry.
                        System.out.println(connString);
                        connection.sendMessage(connString);
                        String response = connection.read();
                        if(response.equals("accepted\n")){
                            //dostałem się do gry. przechodzę do sceny gry.
                        }else{
                            //nie dostałem się do gry.
                            if(response.equals("PIN\n")){
                                //podano zły PIN do gry.
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Wrong PIN");
                                alert.setHeaderText("Podano Zły PIN");
                                alert.showAndWait();
                            }else{
                                System.out.println(response);
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private Pair<String,String> showDialog(){
        Dialog<Pair<String ,String>> dialog= new Dialog<>();
        dialog.setTitle("Connection");

        //buttons on dialog
        ButtonType okButton = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton,cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,10,10));
        TextField nick = new TextField();
        nick.setPromptText("Nick&PIN");

        nick.textProperty().addListener((ov,odlValue,newValue)->{
            String nicks = nick.getText();

            if(nicks.length() > 14){
                String n = nick.getText().substring(0,14);
                nick.setText(n);
            }else{
                if(!newValue.matches("[a-zA-Z0-9]")){ // zabokowanie znaków niechcianych
                    nick.setText(newValue.replaceAll("[^a-zA-Z0-9]", ""));
                }
            }
        });

        TextField PIN = new TextField();
        PIN.setPromptText("PIN");
        PIN.textProperty().addListener((ov,oldValue,newValue) -> {
            String portNumber = PIN.getText();

            if(portNumber.length()>5){
                String s= PIN.getText().substring(0,5);
                PIN.setText(s);
            }else{
                if(!newValue.matches("[0-9]")){ //zapobieganie wprowadzania czegoś innego niż liczby
                    PIN.setText(newValue.replaceAll("[^0-9]",""));
                }
            }
        });

        grid.add(nick,0,0);
        grid.add(PIN,1,0);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> nick.requestFocus());

        dialog.setResultConverter( dialogButton ->
        {
            if(dialogButton == okButton)
                return new Pair<>(nick.getText(),PIN.getText());
            else
                return null;

        });
        Optional<Pair<String ,String >> result = dialog.showAndWait();

        if(result.isPresent()){
            return new Pair<>(nick.getText(),PIN.getText());
        }else{
            return null;
        }

    }
    // ==== Getters & Setters ====
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
