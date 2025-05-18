package il.cshaifasweng.OCSFMediatorExample.server;


import il.cshaifasweng.OCSFMediatorExample.entities.MoveEvent;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import java.io.IOException;
import java.util.ArrayList;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    String[][] GameBoard = new String[3][3];
    int counter = 0;//counts the full cells in the board
    public ConnectionToClient playerX = null;
    public ConnectionToClient playerO = null;
    public String currentTurn;
    public int clientsNum = 0;

    public SimpleServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String msgString = msg.toString();
        if (msgString.startsWith("#warning")) {
            Warning warning = new Warning("Warning from server!");
            try {
                client.sendToClient(warning);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msg.toString().startsWith("Primary Initialized")) {
                currentTurn = "X";

        }else if (msgString.startsWith("add client")) {
            SubscribedClient connection = new SubscribedClient(client);
            SubscribersList.add(connection);
            System.out.println("client added successfully");
            clientsNum++;
            System.out.println("clients number: " + clientsNum);
            try {
                if (playerX == null) {
                    playerX = client;
                    client.sendToClient("symbol: X");

                } else if (playerO == null) {
                    playerO = client;
                    client.sendToClient("symbol: O");
                } else {
                    client.sendToClient("Game is full.");
                }
                currentTurn = "X";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (clientsNum == 2) {
                sendToAllClients("Ready");
            }

        }else if (msgString.startsWith("remove client")) {
            clientsNum--;
            if (!SubscribersList.isEmpty()) {
                for (SubscribedClient subscribedClient : SubscribersList) {
                    if (subscribedClient.getClient().equals(client)) {
                        SubscribersList.remove(subscribedClient);
                        break;
                    }
                }
            }
        } else if (msgString.startsWith("remove all clients")) {
            clientsNum = 0;
            SubscribersList.clear();
            playerX = null;
            playerO = null;
            counter = 0;
            GameBoard = new String[3][3];
            currentTurn = null;

        } else if (msgString.startsWith("move")) {
           // System.out.println("Received message: > im in move<");
            String[] parts = msg.toString().split(" ");
            String[] indices = parts[1].split(",");
            int row = Integer.parseInt(indices[0]);
            int col = Integer.parseInt(indices[1]);
            String symbol = indices[2];
            update_board(row, col, symbol);
            MoveEvent move = new MoveEvent(row, col, symbol);
            sendToAllClients(move);
            if (WinCheck(GameBoard)) {
                try {
                    System.out.println("game over");
                    client.sendToClient("Game over, You won!");
                    getOpponent(client).sendToClient("Game over, You lost!");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            } else if (counter == 9) {
                sendToAllClients("Game over, Tie!");
                return;
            }
            currentTurn = currentTurn.equals("X") ? "O" : "X";
            System.out.println("current turn: before sending" + currentTurn);
            sendToAllClients("current turn" + currentTurn);
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
