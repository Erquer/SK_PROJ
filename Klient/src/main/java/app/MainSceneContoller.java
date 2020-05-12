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
                    String confirm = "";
                    String PIN = "";
                    //wejściowa wiadomość, jeżeli jest, znaczy, że mamy dobre połączenie.
                    while (true){
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
                                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gameOwner.fxml"));
                                AnchorPane root = loader.load();
                                GameOwnerController controller = loader.getController();
                                controller.setConnection(connection);
                                rootPane.getChildren().setAll(root);
                                System.out.println("Accepted creation by message: " + confirm);
                                break;
                            }
                        }
                    }




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
                try {
                    //wysłanie zapytania o dołączenie do gry
                    Pair<String, String > loginData;
                    String nick, PIN;
                    //System.out.println(loginData.getKey() + " " + loginData.getValue());
                    if((loginData = showDialog()) != null){
                        // wprowadzono pin i nick
                        String header = "Cj+";
                        String connString;
                        nick = loginData.getKey().trim();
                        PIN = loginData.getValue().trim();
                        if(loginData.getKey().trim().isBlank()){
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("NICK");
                            alert.setHeaderText("NICK nie może być pusty");
                            alert.showAndWait();
                        }else if (loginData.getValue().trim().isBlank()){
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("PIN");
                            alert.setHeaderText("PIN nie może być pusty");
                            alert.showAndWait();
                        }else {
                            connString =header + loginData.getKey().trim() + ";" + loginData.getValue().trim();
                            //wysłanie wiadomości połączeniowej: j - join + - separator, nick ;-separator wiadomości, PIN do gry.
                            System.out.println(connString);
                            connection.sendMessage(connString);
                            String response = connection.read();
                            if (response.equals("accepted\n")) {
                                //dostałem się do gry. przechodzę do sceny gry.
                                System.out.println("Zaakceptowano mój request.");
                                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("answerPanel.fxml"));
                                AnchorPane root = loader.load();
                                AnswerPanelController controller = loader.getController();
                                controller.setConnection(connection);
                                rootPane.getChildren().setAll(root);
                            } else if (response.equals("PIN\n")) {
                                //podano zły PIN do gry.
                                TextInputDialog dialog = new TextInputDialog();
                                dialog.setTitle("PIN");
                                dialog.setHeaderText("Podaj poprawny PIN do gry:");
                                dialog.getEditor().textProperty().addListener((ov,oldValue,newValue) -> {
                                    String portNumber = dialog.getEditor().getText();

                                    if(portNumber.length()>5){
                                        String s= dialog.getEditor().getText().substring(0,5);
                                        dialog.getEditor().setText(s);
                                    }else{
                                        if(!newValue.matches("[0-9]")){ //zapobieganie wprowadzania czegoś innego niż liczby
                                            dialog.getEditor().setText(newValue.replaceAll("[^0-9]",""));
                                        }
                                    }
                                });
                                var odp = dialog.showAndWait();
                                if(odp.isPresent() && !odp.get().isBlank()){
                                    PIN = odp.get();
                                    connString = header + nick + ";" + PIN;
                                    connection.sendMessage(connString);
                                }else if(odp.get().isBlank()){
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("PIN");
                                    alert.setHeaderText("PUSTY PIN");
                                }
                            } else if(response.equals("creating\n")){
                                //gra aktualnie jest tworzona
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Tworzenie");
                                alert.setHeaderText("Gra aktualnie jest tworzona");
                                alert.showAndWait();
                            } else if(response.equals("nogame\n")){
                                //nie ma dostępnej gry.
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Brak Gry");
                                alert.setHeaderText("Brak dostępnej gry");
                                alert.setContentText("Możesz stworzyć swoją");
                                alert.showAndWait();
                            }else if(response.equals("started\n")){
                                //gra się toczy.
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Game");
                                alert.setHeaderText("Gra aktualnie trwa");
                                alert.setContentText("Nie można dołączyć do trwającej gry");
                                alert.showAndWait();
                            }else if(response.equals("nick\n")){
                                //podano zły nick.
                                TextInputDialog dialog = new TextInputDialog();
                                dialog.setTitle("Nowy nick");
                                dialog.setHeaderText("Podaj nowy nick");
                                dialog.getEditor().textProperty().addListener((ov,odlValue,newValue)->{
                                    String nicks = dialog.getEditor().getText();

                                    if(nicks.length() > 14){
                                        String n = dialog.getEditor().getText().substring(0,14);
                                        dialog.getEditor().setText(n);
                                    }else{
                                        if(!newValue.matches("[a-zA-Z0-9]")){ // zabokowanie znaków niechcianych
                                            dialog.getEditor().setText(newValue.replaceAll("[^a-zA-Z0-9]", ""));
                                        }
                                    }
                                });
                                var odp = dialog.showAndWait();
                                if(odp.isPresent() && !odp.get().isBlank()){
                                    nick = odp.get();
                                    connString = header + nick + ";" + PIN;
                                    connection.sendMessage(connString);
                                }else if(odp.get().isBlank()){
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Nick");
                                    alert.setHeaderText("PUSTY NICK");
                                }
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
