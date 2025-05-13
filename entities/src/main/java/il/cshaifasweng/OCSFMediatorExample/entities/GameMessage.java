package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class GameMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type; // "move", "assign_symbol", "update_board", "game_over"
    private int row;
    private int col;
    private String symbol;
    private String[][] board;
    private String currentTurn;
    private String status;

    public GameMessage(String type, int row, int col, String symbol) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.symbol = symbol;
    }

    public GameMessage(String type) {
        this.type = type;
    }

    // Getters & Setters...

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String[][] getBoard() { return board; }
    public void setBoard(String[][] board) { this.board = board; }

    public String getCurrentTurn() { return currentTurn; }
    public void setCurrentTurn(String currentTurn) { this.currentTurn = currentTurn; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
