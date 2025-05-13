package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.GameMessage;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import javax.imageio.IIOException;
import java.io.IOException;

public class SimpleClient extends AbstractClient {
	
	public static SimpleClient client;
	public String mySymbol;
	String currentTurn;

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

		String message = (String) msg;
//		if(((String) msg).startsWith(mySymbol)) {
//			EventBus.getDefault().post(new Move());
//		}

		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		else{
//			String message = msg.toString();
//			System.out.println(message);
//		}
		//if (msg instanceof String message) {
			//System.out.println("Server: " + message);

			if (message.startsWith("client num")) {
				/// ////////////////////////////////////////////////new class client info
			}

			if (message.startsWith("You are player")) {
				mySymbol = message.endsWith("X") ? "X" : "O";
			}
			else if (message.startsWith("Game started")) {
				EventBus.getDefault().post("Game started");
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
				PrimaryController.getInstance().updateButtonOnBoard(new Move(row, col, symbol));
				currentTurn = symbol.equals("X") ? "O" : "X"; // Switch turn
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

			else if (message.startsWith("Game over")) {
				try {
					client.sendToServer("remove all clients");
				}
				catch (IOException e){
					throw new RuntimeException(e);
				}
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
