package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import javax.imageio.IIOException;
import java.io.IOException;

public class SimpleClient extends AbstractClient {

    public static SimpleClient client;
    String mySymbol;
    String currentTurn;
    int clientsNum = 0;

    public String getmySymbol() {
        return mySymbol;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    private SimpleClient(String host, int port) {
        super(host, port);
    }


    @Override
    protected void handleMessageFromServer(Object msg) {

        if (msg.getClass().equals(Warning.class)) {
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
        } else {
            String message = msg.toString();
            System.out.println("Server: " + message);

            if (message.startsWith("symbol")) {
                mySymbol = message.substring("symbol: ".length()).trim();

            } else if (message.startsWith("Game started")) {
                EventBus.getDefault().post("startGame");
                EventBus.getDefault().post("currentTurn " + currentTurn);
                try {
                    client.sendToServer("start");
                }catch (IOException e){
                    e.printStackTrace();
                }

            } else if (message.startsWith("current turn")) {
                currentTurn = message.substring("current turn".length()).trim();
                EventBus.getDefault().post("currentTurn " + currentTurn);
                EventBus.getDefault().post("accessibility");

            } else if (message.startsWith("Player") && message.contains("moved")) {
                String[] parts = message.split(" ");
                String symbol = parts[1];
                int row = Integer.parseInt(parts[4].substring(1, 2));
                int col = Integer.parseInt(parts[4].substring(3, 4));
                Move move = new Move(row, col, symbol);
                EventBus.getDefault().post(move);
            } else if (message.startsWith("Game over")) {
                if (message.contains("You won!")) {
                    showAlert("Victory", "You won the game!");
                } else if (message.contains("You lost!")) {
                    showAlert("Defeat", "Better luck next time.");
                } else if (message.contains("Tie!")) {
                    showAlert("Draw", "No winner this time.");
                }
                try {
                    client.sendToServer("remove all clients");
                    clientsNum--;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static SimpleClient getClient(String host, int port) {
        if (client == null) {
            client = new SimpleClient(host, port);
        }
        return client;
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

}
