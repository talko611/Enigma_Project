package Engine.configuration;

import Engine.engineAnswers.InputOperationAnswer;
import Engine.enigmaParts.EnigmaParts;
import machine.Machine;
import machine.parts.keyboard.Keyboard;
import machine.parts.plugBoard.PlugBoard;
import machine.parts.plugBoard.PlugBoardImp;
import machine.parts.reflector.Reflector;
import machine.parts.rotor.Rotor;

import java.util.*;
import java.util.stream.Collectors;


public class ConfigurationImp {


    private String startConfiguration;
    private String currentConfiguration;
    static final int MAX_ROTORS_NUM = 99;

    public String getStartConfiguration() {
        return startConfiguration;
    }

    public String getCurrentConfiguration(){return currentConfiguration;}

    public void setCurrentConfiguration(String currentConfiguration) {
        this.currentConfiguration = currentConfiguration;
    }

    public void setStartConfiguration(String startConfiguration) {
        this.startConfiguration = startConfiguration;
    }

    public InputOperationAnswer manualConfigRotors(String configLine, EnigmaParts enigmaParts, Machine machine) {
        configLine = configLine.replaceAll(","," ");
        if(!configLine.matches("[\\d ]+")){
            return new InputOperationAnswer(false, "Error - please enter only numbers seperated by comma/space. you need to enter at least " + enigmaParts.getRotorCount() + " numbers (example: 12 23 1)");
        }
        List<Integer> rotorsIds = getRotorsNumbers(configLine);
        if(rotorsIds.size() > MAX_ROTORS_NUM || rotorsIds.size() < enigmaParts.getRotorCount()){
            return new InputOperationAnswer(false, "Error - Number of ids is out of boundaries./n Machine rotor capacity is between "+ enigmaParts.getRotorCount() + "-" + Math.min(MAX_ROTORS_NUM, enigmaParts.getRotors().size()));
        }
        if(!isRotorsIdsInOfBoundary(rotorsIds,enigmaParts)){
            return new InputOperationAnswer(false, "Error - Rotor id is out of boundary. please enter only values between 1 -" + enigmaParts.getRotors().size());
        }
        if(!isAllRotorsUnique(rotorsIds)){
            return new InputOperationAnswer(false, "Error- can not enter same rotor twice. Please get each rotor id once");
        }
        Map<Integer, Rotor> idToRotor = enigmaParts.getRotors();
        List<Rotor> rotorsInMachine = rotorsIds.
                                            stream().
                                            map(idToRotor::get).
                                            collect(Collectors.toList());
        machine.setRotors(rotorsInMachine);
        return new InputOperationAnswer(true, "Rotors config successfully");
    }

    public InputOperationAnswer manualConfigRotorsOffsets(String configurationString, Machine machine){
        configurationString = configurationString.toUpperCase();
        List<Rotor> rotors = machine.getRotors();
        if(configurationString.length() != rotors.size()){
            return new InputOperationAnswer(false, "Error - offset configuration characters are not correspond to amount of rotors.\nPlease enter " + rotors.size() + " characters");
        }
        Keyboard keyboard = machine.getKeyboard();
        if(!isContainValidOffsets(configurationString, keyboard)){
            return new InputOperationAnswer(false, "Error - please enter only offsets from the define abc\nvalid values: " + keyboard.getABC().keySet() );
        }
        setRotorsOffsets(configurationString, machine);
        return new InputOperationAnswer(true, "Rotors offsets config successfully!");
    }

    public InputOperationAnswer manualConfigReflector(String configStr, Machine machine, EnigmaParts parts){
        Scanner scanner = new Scanner(configStr);
        int reflectorNum = 0;
        if(scanner.hasNextBigInteger()) reflectorNum = scanner.nextInt();
        if(!configStr.matches("\\d") || !(reflectorNum >=1 && reflectorNum <= parts.getReflectors().size())){
            return new InputOperationAnswer(false, "Error - incorrect reflector number.");
        }
        Reflector reflector = parts.getReflectorById(reflectorNum);
        machine.setReflector(reflector);
        return new InputOperationAnswer(true, "Config reflector successfully!");
    }

    public InputOperationAnswer manualConfigPlugBoard(String configLine, Machine machine) {
        configLine = configLine.toUpperCase();
        Map<String, String> lettersPairs = new HashMap<>();

        if(configLine.length() % 2 != 0){
            return new InputOperationAnswer(false, "Error - Cannot config odd num of characters.\n please enter even number of characters");
        }
        if(configLine.length() / 2 > (machine.getKeyboard().getABC().size() / 2)){
            return new InputOperationAnswer(false, "Error - too many pairs of letter to config in plug board.\nCan config up to "+ machine.getKeyboard().getABC().size() /2 + " num of pairs");
        } else if (configLine.length() == 0) {
            machine.setPlugBord(new PlugBoardImp(lettersPairs));
        } else if (!allCharsAreValid(machine.getKeyboard(), configLine)) {
            return new InputOperationAnswer(false, "Error - Configuration line contains letters that not exists in keyBoard");
        } else {
            try {
                List<String> letters = Arrays.asList(configLine.split(""));
                machine.setPlugBord(new PlugBoardImp(buildPlugBoardMap(letters)));
            } catch (InputMismatchException e) {
                return new InputOperationAnswer(false, e.getMessage());
            }
        }
        this.startConfiguration = this.currentConfiguration = createConfiguration(machine);
        return new InputOperationAnswer(true, "plugBoard configuration passed successfully!");
    }

    public InputOperationAnswer autoConfigMachine(EnigmaParts enigmaParts, Machine machine){
        Random random = new Random(System.currentTimeMillis());
        autoConfigRotors(enigmaParts,machine,random);
        autoConfigOffsets(machine,random);
        autoConfigReflector(machine,enigmaParts,random);
        autoConfigPlugBord(machine,random);
        this.startConfiguration = this.currentConfiguration = createConfiguration(machine);
        return new InputOperationAnswer(true, "Auto config machine successfully!");
    }

    private void autoConfigRotors(EnigmaParts enigmaParts, Machine machine,Random random){
        int maxRotorsInMachine = Math.min(enigmaParts.getRotors().size(), MAX_ROTORS_NUM);
        int amountOfRotors = random.nextInt(maxRotorsInMachine) + 1;
        while(amountOfRotors< enigmaParts.getRotorCount() || amountOfRotors > maxRotorsInMachine){
            amountOfRotors = random.nextInt(maxRotorsInMachine) + 1;
        }

        Set<Integer>chosenIds = new LinkedHashSet<>();
        while (chosenIds.size() != amountOfRotors){
            chosenIds.add(random.nextInt(amountOfRotors) + 1);
        }
        StringBuilder builder = new StringBuilder();
        chosenIds.forEach(i -> builder.append(i).append(" "));


        manualConfigRotors(builder.toString(), enigmaParts, machine);
    }

    private void autoConfigOffsets(Machine machine, Random random){
        StringBuilder builder = new StringBuilder();
        int amountOfValues = machine.getKeyboard().getABC().size();

        machine.getRotors().forEach((i) -> {
             int offset = random.nextInt(amountOfValues);
             builder.append(machine.getKeyboard().getEntryMatchKey(offset));
        });
        manualConfigRotorsOffsets(builder.toString(),machine);
    }

    private void autoConfigReflector(Machine machine, EnigmaParts enigmaParts, Random random){
        int reflectorNum = random.nextInt(enigmaParts.getReflectors().size()) + 1;
        Reflector reflector = enigmaParts.getReflectorById(reflectorNum);
        machine.setReflector(reflector);
    }

    private void autoConfigPlugBord(Machine machine, Random random){
        int keyboardSize = machine.getKeyboard().getABC().size();
        int numberOfPairs = random.nextInt(keyboardSize / 2) + 1;
        Map<String, String> letterPairs = new HashMap<>();
        Keyboard keyboard = machine.getKeyboard();
        String letter1;
        String letter2;

        while (letterPairs.size() != numberOfPairs * 2){
            letter1 = keyboard.getEntryMatchKey(random.nextInt(keyboardSize));
            letter2 = keyboard.getEntryMatchKey(random.nextInt(keyboardSize));
            if(!letter1.equals(letter2)  && !letterPairs.containsKey(letter1) && !letterPairs.containsKey(letter2)){
                letterPairs.put(letter1, letter2);
                letterPairs.put(letter2, letter1);
            }
        }
        machine.setPlugBord(new PlugBoardImp(letterPairs));
    }

    private boolean allCharsAreValid(Keyboard keyboard, String configLine){
        for(int i = 0; i < configLine.length(); ++i){
            if(!keyboard.isKeyExists(String.valueOf(configLine.charAt(i)))){
                return false;
            }
        }
        return true;
    }

    private Map<String, String> buildPlugBoardMap(List<String> letters) throws InputMismatchException{
        Map<String,String> lettersPairs = new HashMap<>();

        for(int i = 0; i < letters.size() -1; i += 2){
            String firstCurrLetter= letters.get(i);
            String secondCurrLetter = letters.get(i + 1);
            if(lettersPairs.containsKey(firstCurrLetter) || lettersPairs.containsKey(secondCurrLetter)){
                throw new InputMismatchException("Error - tried to connect two different letters to same letter");
            }
            if(firstCurrLetter.equals(secondCurrLetter)){
                throw new InputMismatchException("Error cannot plug a letter to itself.\nplease enter pairs of 2 different letters");
            }
            lettersPairs.put(firstCurrLetter, secondCurrLetter);
            lettersPairs.put(secondCurrLetter, firstCurrLetter);
        }
        return lettersPairs;
    }

    private void setRotorsOffsets(String configurationString, Machine machine){
        List<Rotor> rotors = machine.getRotors();
        for(int i = 0; i < configurationString.length(); ++i){
            rotors.get(i).setOffset(String.valueOf(configurationString.charAt(i)));
        }
    }

    private boolean isContainValidOffsets(String configurationString, Keyboard keyboard){
        boolean res = true;
        for (int i = 0; i < configurationString.length(); ++i){
            if(!keyboard.isKeyExists(String.valueOf(configurationString.charAt(i)))){
                res = false;
                break;
            }
        }
        return res;
    }

    private List<Integer> getRotorsNumbers(String rotorsConfigLine){
        Scanner scanner = new Scanner(rotorsConfigLine);
        List<Integer> rotorsIds = new ArrayList<>();
        while (scanner.hasNextBigInteger()){
            rotorsIds.add(scanner.nextInt());
        }
        return rotorsIds;
    }

    private boolean isRotorsIdsInOfBoundary(List<Integer> rotorsId, EnigmaParts enigmaParts){
        int idsOutOfBoundary = (int) rotorsId.stream()
                .filter(id -> !enigmaParts.getRotors().containsKey(id))
                .count();
        return idsOutOfBoundary == 0;
    }

    private boolean isAllRotorsUnique(List<Integer> rotorsIds){
        Map<Integer,Integer> idsToCount = new HashMap<>();
        rotorsIds.forEach(id -> idsToCount.merge(id, 1, Integer::sum));
        int idsWithMoreThenOneOccurrence = (int) idsToCount
                .values()
                .stream()
                .filter(i-> i > 1)
                .count();

        return idsWithMoreThenOneOccurrence == 0;
    }

    private String buildRotorConfigLine(List<Rotor> rotors){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<");
        Rotor current;
        for(int i = rotors.size() -1; i >=0; --i){
            current = rotors.get(i);
            stringBuilder.append(current.getId());
            if(i != 0){
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(">");
        return stringBuilder.toString();
    }

    private String buildOffsetsConfigLine(List<Rotor> rotors){
        StringBuilder stringBuilder = new StringBuilder();
        Rotor current;
        for(int i = rotors.size() -1; i >= 0; --i){
             current = rotors.get(i);
             stringBuilder.append(current.getCurrentOffset())
                     .append("(")
                     .append(current.getNotchStepsToZero())
                     .append(")");
             if(i != 0){
                 stringBuilder.append(",");
             }
        }
        return "<" + stringBuilder + ">";
    }

    private String buildPlugBoardConfigLine(PlugBoard plugBord){
        if(plugBord.getAllPlugs().size() == 0){
            return "";
        }
        Map<String, Boolean> isLetterAdded = plugBord
                                                    .getAllPlugs()
                                                    .keySet()
                                                    .stream()
                                                    .collect(Collectors
                                                            .toMap(i -> i, i->false));
        StringBuilder builder = new StringBuilder();
        builder.append("<");
        String first, second;
        for (Map.Entry<String, String> entry : plugBord.getAllPlugs().entrySet()){
            first = entry.getKey();
            second = entry.getValue();
            if(!isLetterAdded.get(first) && !isLetterAdded.get(second)){
                builder.append(first).append("|").append(second).append(",");
                isLetterAdded.replace(first,true);
                isLetterAdded.replace(second,true);
            }
        }
        builder.deleteCharAt(builder.toString().length() -1).append(">");
        return builder.toString();
    }

    public String createConfiguration(Machine machine){
        StringBuilder builder = new StringBuilder();
        builder.append(buildRotorConfigLine(machine.getRotors()));
        builder.append(buildOffsetsConfigLine(machine.getRotors()));
        builder.append("<").append(machine.getReflector().getSymbol()).append(">");
        builder.append(buildPlugBoardConfigLine(machine.getPlugBord()));
        return builder.toString();
    }
}
