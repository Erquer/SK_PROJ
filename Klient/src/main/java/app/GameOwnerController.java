package app;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GameOwnerController {
    // ==== Private Fields ====
    private Connection connection;
    private Game game;

    // ==== FXML Fields ====
    @FXML
    public Button button;



    public void onButtonClicked(Event event){
        System.out.println("działam :)");
    }

    // ==== Getters & Setters ====
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
