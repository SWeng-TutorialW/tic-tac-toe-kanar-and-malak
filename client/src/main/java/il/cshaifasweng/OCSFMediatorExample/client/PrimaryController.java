/**
 * Sample Skeleton for 'primary.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;

import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.client;

import java.io.IOException;

import javafx.application.Platform;
import org.greenrobot.eventbus.Subscribe;

public class PrimaryController {

    @FXML // fx:id="GameGrid"
    private GridPane GameGrid; // Value injected by FXMLLoader

    @FXML // fx:id="cell00"
    private Button cell00; // Value injected by FXMLLoader

    @FXML // fx:id="cell01"
    private Button cell01; // Value injected by FXMLLoader

    @FXML // fx:id="cell02"
    private Button cell02; // Value injected by FXMLLoader

    @FXML // fx:id="cell10"
    private Button cell10; // Value injected by FXMLLoader

    @FXML // fx:id="cell11"
    private Button cell11; // Value injected by FXMLLoader

    @FXML // fx:id="cell12"
    private Button cell12; // Value injected by FXMLLoader

    @FXML // fx:id="cell20"
    private Button cell20; // Value injected by FXMLLoader

    @FXML // fx:id="cell21"
    private Button cell21; // Value injected by FXMLLoader

    @FXML // fx:id="cell22"
    private Button cell22; // Value injected by FXMLLoader

    private Button[][] buttonMatrix = new Button[3][3];

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);

        buttonMatrix[0][0] = cell00;
        buttonMatrix[0][1] = cell01;
        buttonMatrix[0][2] = cell02;
        buttonMatrix[1][0] = cell10;
        buttonMatrix[1][1] = cell11;
        buttonMatrix[1][2] = cell12;
        buttonMatrix[2][0] = cell20;
        buttonMatrix[2][1] = cell21;
        buttonMatrix[2][2] = cell22;

        if (!(myTurn())) {
            Platform.runLater(() -> {
                cell00.setDisable(true);
                cell01.setDisable(true);
                cell02.setDisable(true);
                cell10.setDisable(true);
                cell11.setDisable(true);
                cell12.setDisable(true);
                cell20.setDisable(true);
                cell21.setDisable(true);
                cell22.setDisable(true);
            });
        }
    }

    private boolean myTurn() {
        return client.mySymbol != null && client.mySymbol.equals(client.currentTurn);
    }

    @Subscribe
    public void updateButtonOnBoard(Move move) {
        Platform.runLater(() -> {
            Button btn = buttonMatrix[move.getRow()][move.getCol()];
            btn.setText(move.getSymbol());
            btn.setDisable(true);
            btn.setStyle(move.getSymbol().equals("X") ?
                    "-fx-text-fill: blue; -fx-font-size: 24px;" :
                    "-fx-text-fill: red; -fx-font-size: 24px;");
        });
    }

    @FXML
    public void click(ActionEvent event) {
        if (myTurn()) {
            Button clicked = (Button) event.getSource();
            Integer rowIndex = GridPane.getRowIndex(clicked);
            Integer colIndex = GridPane.getColumnIndex(clicked);
            int row = (rowIndex == null) ? 0 : rowIndex;
            int col = (colIndex == null) ? 0 : colIndex;

            // send the move to the server
            try {
                Move move = new Move(row, col, client.mySymbol);
                client.sendToServer("move" + row + "," + col + "," + client.mySymbol);
                updateButtonOnBoard(move);
            } catch (IOException e) {
                EventBus.getDefault().post(new WarningEvent(new Warning("Failed to send move.")));
            }
        } else {
            EventBus.getDefault().post(new WarningEvent(new Warning("it's not your turn.")));
        }
    }

    @Subscribe
    public void updateBoardAccessibility(String event) {
        if (event.equals("accessibility")) {
            Platform.runLater(() -> {
                boolean enable = myTurn();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        Button btn = buttonMatrix[i][j];
                        if (btn.getText().isEmpty()) {
                            btn.setDisable(!enable);
                        }
                    }
                }
            });
        }
    }
}
