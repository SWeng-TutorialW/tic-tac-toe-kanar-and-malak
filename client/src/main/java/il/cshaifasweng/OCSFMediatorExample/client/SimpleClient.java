package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.MoveEvent;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.application.Platform;
import javafx.scene.control.Alert;
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

            } else if (message.startsWith("Ready")) {
                currentTurn = "X";
                EventBus.getDefault().post("startGame");

            } else if (message.startsWith("current turn")) {
                System.out.println("Received message: >" + message + "<");
                System.out.println("mySymbol: >" + mySymbol + "<, currentTurn: >" + currentTurn + "<");
                currentTurn = message.substring("current turn".length()).trim();

            } else if (msg instanceof MoveEvent) {
                System.out.println("Received message: >" + message + "<");
                EventBus.getDefault().post(msg);

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
