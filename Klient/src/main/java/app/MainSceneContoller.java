package app;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class MainSceneContoller {
    // ==== Private Fields ====
    private Connection connection;

    // ==== FXML Fields ====
    @FXML
    public Button createButton;
    public Button playButton;

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
                    String confirm =  "";
                    String PIN = "";
                    while (! confirm.equals("accepted\n")){
                        var opPIN = pinDialog.showAndWait();
                        if(opPIN.isPresent()){
                            PIN = opPIN.get();
                        }else {
                            System.exit(0);
                        }

                        connection.sendMessage("Cn+" + PIN);
                        confirm = connection.read();
                        //przypadek, kiedy ktoś już tworzy grę
                        if(confirm.equals("creating\n")){
                            var alert = new Alert(Alert.AlertType.WARNING);
                            alert.setHeaderText("Ktoś już tworzy grę!");
                            alert.setTitle("Tworzenie gry");
                            alert.showAndWait();
                            break;
                        }
                    }
                    //przejdzie do sceny tworzenia gry.
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });
    }

    // ==== Getters & Setters ====
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
