package il.cshaifasweng.OCSFMediatorExample.client;

public class Move{
    int row;
    int col;
    String symbol;
    public Move(int row, int col, String symbol) {
        this.row = row;
        this.col = col;
        this.symbol = symbol;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public String getSymbol() {
        return symbol;
    }




}
