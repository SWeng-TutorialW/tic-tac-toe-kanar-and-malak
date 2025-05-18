/**
 * Sample Skeleton for 'primary.fxml' Controller Class
 * final
 */

package il.cshaifasweng.OCSFMediatorExample.client;


import il.cshaifasweng.OCSFMediatorExample.entities.MoveEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.client;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.getClient;
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

    private final Button[][] buttonMatrix = new Button[3][3];

    @FXML
    public void initialize() throws IOException {
        System.out.println("PrimaryController initialized");
        getClient("", 3000).sendToServer("Primary Initialized");
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
    }

    @Subscribe
    public void updateButtonOnBoard(MoveEvent move) {
        System.out.println("updateButtonOnBoard: " + move.getSymbol());
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
        if (client.mySymbol != null && client.mySymbol.equals(client.currentTurn)) {
            System.out.println("its my turn");
            Button clicked = (Button) event.getSource();
            Integer rowIndex = GridPane.getRowIndex(clicked);
            Integer colIndex = GridPane.getColumnIndex(clicked);
            int row = (rowIndex == null) ? 0 : rowIndex;
            int col = (colIndex == null) ? 0 : colIndex;

            // send the move to the server
            try {
                MoveEvent move = new MoveEvent(row, col, client.mySymbol);
                client.sendToServer("move " + row + "," + col + "," + client.mySymbol);
                EventBus.getDefault().post(move);
                client.currentTurn = client.currentTurn.equals("X") ? "O" : "X";
            } catch (IOException e) {
                EventBus.getDefault().post(new WarningEvent(new Warning("Failed to send move.")));
            }
        } else {
            System.out.println("its NOT my turn");
            EventBus.getDefault().post(new WarningEvent(new Warning("it's not your turn.")));
        }
    }

    @Subscribe
    public void updateTurn(TurnEvent event) {
        System.out.println("Got TurnEvent: " + event.turn);
        client.currentTurn = event.turn;
    }
}

//    @SubscriberdAccessibility
//    public void updateBoa(String event) {
//        if (event.equals("accessibility")) {
//            Platform.runLater(() -> {
//                boolean enable = (client.mySymbol != null && client.mySymbol.equals(client.currentTurn));
//                for (int i = 0; i < 3; i++) {
//                    for (int j = 0; j < 3; j++) {
//                        Button btn = buttonMatrix[i][j];
//                        if (btn.getText().isEmpty()) {
//                            btn.setDisable(!enable);
//                        }
//                    }
//                }
//            });
//        }
//    }
//}
