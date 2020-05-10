package app;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getClassLoader().getResource("mainScene.fxml"));

        Parent root = loader.load();

        //default connection parameters.
        String IP = "127.0.0.1";
        int port = 1400;

        if(getParameters().getRaw().size() >= 2){
            IP = getParameters().getRaw().get(0);
            port = Integer.parseInt(getParameters().getRaw().get(1));
        }

        MainSceneContoller contoller = loader.getController();
        //Connect client to server
        try{
            contoller.setConnection(new Connection(IP,port));

        }catch (IOException e){
            e.printStackTrace();
        }
        primaryStage.setTitle("Kahoot! SK Project!");
        primaryStage.setOnCloseRequest(windowEvent->{
            System.exit(0);
        });
        primaryStage.setScene(new Scene(root));
        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
