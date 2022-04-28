public class TRow {
    String symbol;
    int address, index;

    public TRow(String symbol, int address) {
        this.symbol = symbol;
        this.address = address;
        index = 0;
    }

    public TRow(String symbol, int address, int index) {
        this(symbol, address);
        this.index = index;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public int getAddess() {
        return address;
    }
    public void setAddess(int address) {
        this.address = address;
    }
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
