package Engine.enums;

public enum ReflectorGreekNums {
    NUM1("I", 1),
    NUM2("II", 2),
    NUM3("III", 3),
    NUM4("IV", 4),
    NUM5("V", 5);

    private final String symbol;
    private final int val;
    ReflectorGreekNums(String v, int i) {
        this.symbol = v;
        this.val = i;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getVal() {
        return val;
    }
}
