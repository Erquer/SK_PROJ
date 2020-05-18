package app;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static FXMLLoader loader;
    private static Stage stage;
    private static Scene scene;


    public static Stage getStage() { return  stage; }

    public static void setLoader(FXMLLoader loader1) { loader = loader1; }

    public void setRoot(String fxml) throws IOException{
        scene.setRoot(loadFXML(fxml));
    }
    private static Parent loadFXML(String fxml) throws IOException{
        loader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return loader.load();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getClassLoader().getResource("mainScene.fxml"));

        Parent root = loader.load();

        //default connection parameters.
        String IP = "127.0.0.1";
        int port = 1400;

        if(getParameters().getRaw().size() >= 2){
            System.out.println(getParameters().getRaw().get(0) + " " + getParameters().getRaw().get(1));
            IP = getParameters().getRaw().get(0);
            port = Integer.parseInt(getParameters().getRaw().get(1));
        }

        MainSceneContoller contoller = loader.getController();
        //Connect client to server
        try{
            contoller.setConnection(new Connection(IP,port));
            primaryStage.setTitle("Kahoot! SK Project!");
            primaryStage.setOnCloseRequest(windowEvent -> {
                System.exit(0);
            });
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd Serwera");
            alert.setHeaderText("Serwer nie działa, nie udało się połączyć.");
            alert.setContentText("Zamykam aplikację.");
            alert.showAndWait();
            System.exit(0);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
