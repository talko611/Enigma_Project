package machine;

import machine.enums.RotorDirection;
import machine.parts.keyboard.Keyboard;
import machine.parts.plugBoard.PlugBoard;
import machine.parts.reflector.Reflector;
import machine.parts.rotor.Rotor;


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
        StringBuilder outPut = new StringBuilder();
        int entryPoint;
        for(int i = 0; i < input.length(); ++i){
            moveRotors();
            entryPoint = keyboard.getEntryPointByKey(plugBoard.switchLetters(String.valueOf(input.charAt(i))));
            for(Rotor rotor : rotors){
                entryPoint = rotor.scramble(entryPoint, RotorDirection.In);
            }
            entryPoint = reflector.reflect(entryPoint);
            for (int j  = rotors.size() -1; j >= 0; --j){
                entryPoint = rotors.get(j).scramble(entryPoint, RotorDirection.Out);
            }
            outPut.append(plugBoard.switchLetters(keyboard.getEntryMatchKey(entryPoint)));
        }
        return outPut.toString();
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
