/**
 * Sample Skeleton for 'primary.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.GameMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;

import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.client;

import java.io.IOException;
import java.io.Serializable;

import javafx.application.Platform;
import org.greenrobot.eventbus.Subscribe;

public class PrimaryController implements Serializable {

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

    public String mySymbol;     // player X/O
    public String currentTurn = "X";
    private Button[][] buttonMatrix = new Button[3][3];
    private static PrimaryController instance;

    public PrimaryController() {
        instance = this;
    }

    public static PrimaryController getInstance() {
        return instance;
    }

    //fxml?
    public void initialize() {
        mySymbol = SimpleClient.client.getmySymbol();
        currentTurn = SimpleClient.client.getCurrentTurn();
        System.out.println("My symbol is222: " + mySymbol);
        if (mySymbol != null) {
            System.out.println("My symbol is1111: " + mySymbol);
        }
        instance = this;

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
    private boolean myTurn() {
        return mySymbol != null && mySymbol.equals(currentTurn);
    }
    @Subscribe
    public void setMySymbol(String symbol) {
        this.mySymbol = symbol;
    }
    @Subscribe
    public void setCurrentTurn(String currTurn) {
        this.currentTurn = currTurn;
    }

    public void updateTurn(){
        if(currentTurn.equals("X"))
            currentTurn = "O";
        else {
            currentTurn = "X";
        }
    }

    //    private void NotmyTurn() {
//        if (!myTurn()) {
//            Platform.runLater(() -> {
//                cell00.setDisable(true);
//                cell01.setDisable(true);
//                cell02.setDisable(true);
//                cell10.setDisable(true);
//                cell11.setDisable(true);
//                cell12.setDisable(true);
//                cell20.setDisable(true);
//                cell21.setDisable(true);
//                cell22.setDisable(true);
//            });
//        }
//    }
    @Subscribe
    public void updateButtonOnBoard(int row, int col, String symbol) {
        Platform.runLater(() -> {
            Button btn = buttonMatrix[row][col];
            btn.setText(symbol);
            btn.setDisable(true);
            btn.setStyle(symbol.equals("X") ?
                    "-fx-text-fill: blue; -fx-font-size: 24px;" :
                    "-fx-text-fill: red; -fx-font-size: 24px;");
        });
    }

    @Subscribe
    @FXML
    public void click(ActionEvent event) {
        System.out.println("clicked");
        System.out.println("mySymbol = " + mySymbol);
        System.out.println("currentTurn = " + currentTurn);
        System.out.println("is my turn? " + myTurn());

        System.out.println(mySymbol);
        Button clicked = (Button) event.getSource();
        Integer rowIndex = GridPane.getRowIndex(clicked);
        Integer colIndex = GridPane.getColumnIndex(clicked);
        int row = (rowIndex == null) ? 0 : rowIndex;
        int col = (colIndex == null) ? 0 : colIndex;



        // if it is not your turn
        if (!myTurn()) {
            EventBus.getDefault().post(new WarningEvent(new Warning("It's not your turn!")));
            return;
        }

        Platform.runLater(() -> {
            System.out.println(mySymbol);
            clicked.setText(mySymbol);

            if (mySymbol.equals("X")) {
                clicked.setStyle("-fx-text-fill: blue; -fx-font-size: 24px;");
            } else if (mySymbol.equals("O")) {
                clicked.setStyle("-fx-text-fill: red; -fx-font-size: 24px;");
            }

            // disable the button to prevent clicking the same button again
            clicked.setDisable(true);
        });
        // send the move to the server
        try {
            GameMessage move = new GameMessage("move", row, col, mySymbol);
            //String msg = String.format("move %d %d %s", row, col, mySymbol);
            client.sendToServer(move);
            updateTurn();



        } catch (IOException e) {
            EventBus.getDefault().post(new WarningEvent(new Warning("Failed to send move.")));
        }
    }

    @Subscribe
    public void updateBoardAccessibility() {
        Platform.runLater(() -> {
            boolean enable = mySymbol != null && mySymbol.equals(currentTurn);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Button btn = buttonMatrix[i][j];
                    // נפתח רק תאים ריקים (בלי X/O)
                    if (btn.getText().isEmpty()) {
                        btn.setDisable(!enable);
                    }
                }
            }
        });
    }

}
