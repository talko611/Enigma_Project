package machine;

import machine.enums.RotorDirection;
import machine.parts.keyboard.Keyboard;
import machine.parts.plugBoard.PlugBoard;
import machine.parts.reflector.Reflector;
import machine.parts.rotor.Rotor;
import machine.parts.rotor.RotorImp;


import java.util.ArrayList;
import java.util.List;


public class MachineImp implements Machine{

    private List<Rotor> rotors;
    private Reflector reflector;
    private PlugBoard plugBoard;
    private Keyboard keyboard;


    public MachineImp(){
        this.rotors = null;
        this.keyboard = null;
        this.plugBoard = null;
        this.reflector = null;
    }

    @Override
    public void setRotors(List<Rotor> rotors) {
        this.rotors = rotors;
    }

    @Override
    public List<Rotor> getRotors() {
        return rotors;
    }

    @Override
    public void setReflector(Reflector reflector) {
        this.reflector = reflector;
    }

    @Override
    public Reflector getReflector() {
        return reflector;
    }

    @Override
    public void setPlugBord(PlugBoard plugBord) {
        this.plugBoard = plugBord;
    }

    @Override
    public PlugBoard getPlugBord(){return plugBoard;}

    @Override
    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    @Override
    public Keyboard getKeyboard(){return this.keyboard;}

    @Override
    public String encryptDecrypt(String input){
        input = input.toUpperCase();
        moveRotors();
        int entryPoint = keyboard.getEntryPointByKey(plugBoard.switchLetters(input));
        for(Rotor rotor : rotors){
            entryPoint = rotor.scramble(entryPoint, RotorDirection.In);
        }
        entryPoint = reflector.reflect(entryPoint);
        for(int i = rotors.size() -1; i >= 0; --i){
            entryPoint = rotors.get(i).scramble(entryPoint, RotorDirection.Out);
        }
        return plugBoard.switchLetters(keyboard.getEntryMatchKey(entryPoint));
    }

    private void moveRotors(){
        boolean canMove = true;
        for(Rotor rotor : rotors) {
            canMove = rotor.move(canMove);
        }
    }

    @Override
    public void reset(){
        this.rotors.forEach(Rotor::resetMoves);
    }

}
