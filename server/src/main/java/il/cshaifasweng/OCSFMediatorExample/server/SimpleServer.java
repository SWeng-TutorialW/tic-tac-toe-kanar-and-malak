package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.GameMessage;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer implements Serializable {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    String[][] GameBoard = new String[3][3];
    int counter = 0;//counts the full cells in the board
    public ConnectionToClient playerX = null;
    public ConnectionToClient playerO = null;
    public String currentTurn ;
    public int clientsNum = 0;

    public SimpleServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String msgString = msg.toString();
        System.out.println(msgString);
        if (msgString.startsWith("#warning")) {
            Warning warning = new Warning("Warning from server!");
            try {
                client.sendToClient(warning);
                System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msgString.startsWith("add client")) {
            SubscribedClient connection = new SubscribedClient(client);
            SubscribersList.add(connection);
            try {
                System.out.println("5555555");
                System.out.println("5555555");
                client.sendToClient("client added successfully11");
                System.out.println("444444444444");
                System.out.println("info2:"+currentTurn);
                System.out.println("33333333333333333333333333");
                clientsNum++;
                System.out.println("client num"+clientsNum);
                if(clientsNum == 2)
                {
                    sendToAllClients("Game started");
                }
                System.out.println(clientsNum);
            } catch (IOException e) {
                System.out.println("Failed to send initial confirmation to client");
                e.printStackTrace();
            }
            try {
                if (playerX == null) {
                    playerX = client;
                    client.setInfo("symbol", "X");
                    client.sendToClient("You are player X");
                } else if (playerO == null) {
                    playerO = client;
                    client.setInfo("symbol", "O");
                    client.sendToClient("You are player O");
                    sendToAllClients("Game started! Player X begins.");
                    sendToAllClients("current turn: X");
                    currentTurn = "X";
                } else {
                    client.sendToClient("Game is full.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (msgString.startsWith("remove client")) {
            if (!SubscribersList.isEmpty()) {
                for (SubscribedClient subscribedClient : SubscribersList) {
                    if (subscribedClient.getClient().equals(client)) {
                        SubscribersList.remove(subscribedClient);
                        break;
                    }
                }
            }
        } else if (msg instanceof GameMessage) {
            GameMessage gameMessage = (GameMessage) msg;
            if (gameMessage.getType().equals("move")) {
                if (!currentTurn.equals(gameMessage.getSymbol())) {
                    try {
                        client.sendToClient("Not your turn.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                update_board(gameMessage.getRow(), gameMessage.getCol(), gameMessage.getSymbol());
                sendToAllClients(String.format("Player %s moved to [%d,%d]", gameMessage.getSymbol(), gameMessage.getRow(), gameMessage.getCol()));
                if (WinCheck(GameBoard)) {
                    try {
                        client.sendToClient("You won!");
                        getOpponent(client).sendToClient("You lost!");
                        sendToAllClients("Game over.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                } else if (counter == 9) {
                    sendToAllClients("Game ended, there is a tie!");
                    return;
                }
                currentTurn = currentTurn.equals("X") ? "O" : "X";
                sendToAllClients("current turn: " + currentTurn);
            }


        }
    }

    void update_board(int row, int col, String symbol) {
        GameBoard[row][col] = symbol;
        counter++;
    }

    public boolean WinCheck(String[][] board) {
        // check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != null &&
                    board[i][0].equals(board[i][1]) &&
                    board[i][0].equals(board[i][2])) {
                return true;
            }
        }

        // check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != null &&
                    board[0][j].equals(board[1][j]) &&
                    board[0][j].equals(board[2][j])) {
                return true;
            }
        }

        // main diagonal
        if (board[0][0] != null &&
                board[0][0].equals(board[1][1]) &&
                board[0][0].equals(board[2][2])) {
            return true;
        }

        // secondary diagonal
        if (board[0][2] != null &&
                board[0][2].equals(board[1][1]) &&
                board[0][2].equals(board[2][0])) {
            return true;
        }

        return false; // there is no winner
    }


    public void sendToAllClients(String message) {
        try {
            for (SubscribedClient subscribedClient : SubscribersList) {
                subscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private ConnectionToClient getOpponent(ConnectionToClient client) {
        return client == playerX ? playerO : playerX;
    }
}
