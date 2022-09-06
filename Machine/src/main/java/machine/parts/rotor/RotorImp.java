package machine.parts.rotor;

import machine.enums.RotorDirection;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RotorImp implements Rotor {
    private final int notch;
    private final Map<Integer, Integer> inputToOutput;
    private int moves;
    private  int offset;
    private Map<String, Integer> letterToEntryPoint;
    private final int id;

    public RotorImp(int id,int notch, Map<Integer, Integer> rotorMapping,Map<String,Integer> letterToEntryPoint, int offset ){
        this.id = id;
        this.notch = notch;
        this.offset = offset;
        this.moves = 0;
        this.inputToOutput = rotorMapping;
        this.letterToEntryPoint = letterToEntryPoint;
    }

    public RotorImp(RotorImp copyFrom){
        this.notch = copyFrom.notch;
        this.inputToOutput = SerializationUtils.clone( new HashMap<>(copyFrom.inputToOutput));
        this.moves = 0;
        this.offset = 0;
        letterToEntryPoint = SerializationUtils.clone(new HashMap<>(copyFrom.letterToEntryPoint));
        id = copyFrom.id;
    }


    @Override
    public int scramble(int entryPoint, RotorDirection direction) {
        int exitPoint = 0;
        entryPoint = Math.floorMod(entryPoint + (offset + moves), inputToOutput.size());
        if (direction.equals(RotorDirection.In)) {
            exitPoint = Math.floorMod(inputToOutput.get(entryPoint) - (offset + moves), inputToOutput.size());
        } else {
            for (Map.Entry<Integer, Integer> entry : inputToOutput.entrySet()) {
                if (entry.getValue() == entryPoint) {
                    exitPoint = Math.floorMod(entry.getKey() - (offset + moves), inputToOutput.size());
                }
            }
        }
        return exitPoint;
    }

    @Override
    public boolean move(boolean canMove) {
        boolean answer = false;// answers if you can move next rotor
        if(canMove){
            ++moves;
            answer = isMoveNextRotator();
        }
        return answer;
    }

    private boolean isMoveNextRotator() {
        return Math.floorMod(notch - (moves + offset ) , this.inputToOutput.size()) == 0; // To Do - change to global variable that hold the number of characters
    }

    @Override
    public String getInitialOffset(){
        String str = null;
        for(Map.Entry<String, Integer>entry : letterToEntryPoint.entrySet()){
            if(entry.getValue() == this.offset){
                str = entry.getKey();
            }
        }
        return str;
    }

    @Override
    public String getCurrentOffset(){
        String str = null;
        int currentOffset = Math.floorMod(offset + moves, inputToOutput.size());
        for(Map.Entry<String, Integer> entry : letterToEntryPoint.entrySet()){
            if(entry.getValue() == currentOffset){
                str = entry.getKey();
                break;
            }
        }
        return str;
    }

    @Override
    public int getNotchStepsToZero(){
        return Math.floorMod(notch - (offset + moves), inputToOutput.size());
    }

    @Override
    public void resetMoves(){
        this.moves = 0;
    }
    @Override
    public int getNotch() {
        return notch;
    }

    @Override
    public int getId() {
        return id;
    }

    public Map<Integer, Integer> getInputToOutput() {
        return inputToOutput;
    }
    @Override
    public int getMoves() {
        return moves;
    }

    @Override
    public void setOffset(String letter){
        this.offset = this.letterToEntryPoint.get(letter);
    }
    @Override
    public String toString(){
        return "RotorId:" + this.id
                +"\nnotch: "+ this.notch
                +"\nmoves:" + this.moves
                +"\nOffset:" +this.offset
                +"\nLetter to entry point: " + this.letterToEntryPoint.toString()
                +"\nMapping:" + this.inputToOutput.toString();
    }
}
