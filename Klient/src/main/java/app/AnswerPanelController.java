package app;

import javafx.scene.layout.AnchorPane;

public class AnswerPanelController implements Runnable {

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    //private Fields
    private Connection connection;

    //FXML Fields

    public AnchorPane rootPane;

    @Override
    public void run() {

    }
}
