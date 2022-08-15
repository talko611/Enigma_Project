package machine.parts.rotor;

import machine.enums.RotorDirection;

public interface Rotor {
    int scramble(int entryPoint , RotorDirection direction);

    boolean move(boolean canMove);

    int getId();

    int getOffsetPos();

    int getNotch();

    void setOffset(String letter);

    String getOffset();

    @Override
    String toString();

    void resetMoves();

    int getMoves();
}
