package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class SimpleClient extends AbstractClient {

	public static SimpleClient client = null;
	private String playerSymbol;

	public String getPlayerSymbol() {
		return playerSymbol;
	}

	private SimpleClient(String host, int port) {
		super(host, port);
	}



	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		else{
			String message = msg.toString();
			System.out.println(message);
		}
		if (msg instanceof String message) {
			System.out.println("Server: " + message);

			if (message.startsWith("You are player")) {
				String playerSymbol = message.endsWith("X") ? "X" : "O";
				if (playerSymbol != null) {
					//.setMySymbol(playerSymbol);////////////////////////
				}
			}

			else if (message.startsWith("current turn:")) {
				String turn = message.substring("current turn:".length()).trim();
				PrimaryController.getInstance().setCurrentTurn(turn);
				PrimaryController.getInstance().updateBoardAccessibility();
			}

			else if (message.startsWith("Player") && message.contains("moved")) {
				String[] parts = message.split(" ");
				String symbol = parts[1];
				int row = Integer.parseInt(parts[4].substring(1, 2));
				int col = Integer.parseInt(parts[4].substring(3, 4));
				PrimaryController.getInstance().updateButtonOnBoard(row, col, symbol);
				String currentTurn = symbol.equals("X") ? "O" : "X"; // Switch turn
				PrimaryController.getInstance().setCurrentTurn(currentTurn);
			}

			else if (message.equals("You won!")) {
				showAlert("Victory", "You won the game!");
			}

			else if (message.equals("You lost!")) {
				showAlert("Defeat", "Better luck next time.");
			}

			else if (message.equals("Game ended in a draw.")) {
				showAlert("Draw", "No winner this time.");
			}

			else if (message.equals("Not your turn.") || message.equals("Cell already taken.")) {
				EventBus.getDefault().post(new WarningEvent(new Warning(message)));
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
