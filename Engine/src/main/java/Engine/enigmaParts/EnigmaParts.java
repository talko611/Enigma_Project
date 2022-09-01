package Engine.enigmaParts;

import Engine.Decipher.Decipher;
import Engine.enums.ReflectorGreekNums;
import Engine.generated.*;
import machine.parts.keyboard.Keyboard;
import machine.parts.keyboard.KeyboardImp;
import machine.parts.reflector.Reflector;
import machine.parts.reflector.ReflectorImp;
import machine.parts.rotor.Rotor;
import machine.parts.rotor.RotorImp;

import java.util.*;
import java.util.stream.Collectors;

public class EnigmaParts {
    private Map<Integer, Rotor> rotors;
    private  Map<Integer, Reflector> reflectors;
    private Keyboard keyboard;
    private int rotorCount;

    private Decipher decipher;


    public Map<Integer, Rotor> getRotors() {
        return rotors;
    }

    public Map<Integer, Reflector> getReflectors() {
        return reflectors;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public Rotor getRotorById(int id){
        return rotors.get(id);
    }

    public Reflector getReflectorById(int id){
        return reflectors.get(id);
    }

    public int getRotorCount(){return this.rotorCount;}

    public void saveMachineParts(CTEEnigma enigma){
        CTEMachine cteMachine = enigma.getCTEMachine();
        this.keyboard = saveKeyboard(cteMachine.getABC());
        this.rotors = saveRotors(cteMachine.getCTERotors());
        this.reflectors = saveReflectors(cteMachine.getCTEReflectors());
        this.rotorCount = cteMachine.getRotorsCount();
        this.decipher = saveDecipher(enigma.getCTEDecipher());
    }

    private Keyboard saveKeyboard(String abc){
        List<String> listOfAbc = Arrays.asList(abc.trim().toUpperCase().split(""));
        return new KeyboardImp(listOfAbc);
    }

    private Rotor saveRotor(CTERotor cteRotor){
        List<String> right = cteRotor.
                getCTEPositioning().
                stream().
                map(CTEPositioning::getRight).
                collect(Collectors.toList());

        List<String> left = cteRotor
                .getCTEPositioning()
                .stream()
                .map(CTEPositioning::getLeft)
                .collect(Collectors.toList());
        Map<Integer, Integer> inputOutput = right.stream().collect(Collectors.toMap(right::indexOf, left::indexOf));
        Map<String, Integer> letterToInput = right.stream().collect(Collectors.toMap(i->i, right::indexOf));

        return new RotorImp(cteRotor.getId(), cteRotor.getNotch() -1, inputOutput, letterToInput,0);
    }

    private Map<Integer,Rotor> saveRotors(CTERotors cteRotors){
        Map<Integer, Rotor> idToRotor = new HashMap<>();
        cteRotors.getCTERotor().forEach(i ->idToRotor.put(i.getId(), saveRotor(i)));
        return idToRotor;
    }

    private Reflector saveReflector(CTEReflector cteReflector){
        Map<Integer, Integer>inputOutPut = new HashMap<>();
        cteReflector.getCTEReflect().forEach((i)->{
            int input = i.getInput() - 1;
            int output = i.getOutput() - 1;
            inputOutPut.put(input,output);
            inputOutPut.put(output, input);
        });
        return new ReflectorImp(inputOutPut, cteReflector.getId());
    }

    private Map<Integer, Reflector> saveReflectors(CTEReflectors cteReflectors){
        Map<Integer, Reflector> idToReflector = new HashMap<>();
        cteReflectors.getCTEReflector().forEach(i-> idToReflector.put(getReflectorIntId(i.getId()), saveReflector(i)));
        return idToReflector;
    }

    private int getReflectorIntId(String greekNum){
        int res = -1;
        for(ReflectorGreekNums enumVal : ReflectorGreekNums.values()){
            if(enumVal.getSymbol().equals(greekNum)){
                res = enumVal.getVal();
                break;
            }
        }
        return res;
    }

    private Decipher saveDecipher(CTEDecipher cteDecipher){
        Set<String> dictionary = new HashSet<>();
        String exclude = "[" + cteDecipher.getCTEDictionary().getExcludeChars().trim() + "]";
        Scanner scanner = new Scanner(cteDecipher.getCTEDictionary().getWords().trim());
        String currentWord;
        while (scanner.hasNext()){
            currentWord = scanner.next().replaceAll(exclude, "");
            dictionary.add(currentWord);
        }
        return new Decipher(dictionary);
    }
}
