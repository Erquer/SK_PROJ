package app;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;

public class GameOwnerController {
    // ==== Private Fields ====
    private Connection connection;
    private Game game;

    // ==== FXML Fields ====
    @FXML
    public Button button;



    public void onButtonClicked(Event event) {
        //System.out.println("dzialam");
        try {
            connection.sendMessage("dzialam");
            String response  = connection.read();
            System.out.println(response);

        } catch (IOException e) {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CONNECTION");
            alert.setHeaderText("Connection problems");
            alert.showAndWait();
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
