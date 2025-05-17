package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class MoveEvent implements Serializable {
    int row;
    int col;
    String symbol;
    public MoveEvent(int row, int col, String symbol) {
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
