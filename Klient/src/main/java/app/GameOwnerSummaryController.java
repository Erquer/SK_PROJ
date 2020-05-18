package app;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class GameOwnerSummaryController {

    @FXML
    public ListView<String> playerView;
    @FXML
    public ListView<String> qaView;

    public void setPlayerView(List<String > playerView) {
        this.playerView.getItems().addAll(playerView);
    }

    public void setQaView(List<String> qaView) {
        this.qaView.getItems().addAll(qaView);
    }
}
