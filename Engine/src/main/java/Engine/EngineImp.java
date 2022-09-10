package Engine;


import Engine.DM.CalculationsUtils;
import Engine.DM.DecipherManager;
import Engine.configuration.ConfigurationImp;
import Engine.engineAnswers.*;
import Engine.enigmaParts.EnigmaParts;
import Engine.XMLLoader.FileReader;
import Engine.XMLLoader.XMLLoaderImp;
import Engine.enums.DmTaskDifficulty;
import javafx.scene.Parent;
import javafx.util.Pair;
import machine.Machine;
import machine.MachineImp;
import machine.parts.keyboard.Keyboard;
import machine.parts.plugBoard.PlugBoardImp;
import org.apache.commons.lang3.SerializationUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class EngineImp implements Engine{
    public Machine machine;
    private final EnigmaParts enigmaParts;
    private  Map<String,List<EncryptDecryptMessage>> statistics;
    private final ConfigurationImp configurationImp;

    private DecipherManager decipherManager;



    public EngineImp(){
        this.machine = new MachineImp();
        this.enigmaParts = new EnigmaParts();
        this.configurationImp = new ConfigurationImp();
    }

    @Override
    public InputOperationAnswer loadFromFile(String filePath){
        boolean success;
        String message;
        try{
            FileReader reader = new XMLLoaderImp(filePath);
            this.enigmaParts.saveMachineParts(reader.load());
            this.machine.setKeyboard(this.enigmaParts.getKeyboard());//set up keyBoard because it's the only part which doesn't need configuration
            this.decipherManager = enigmaParts.getDecipherManager();
            this.statistics = new LinkedHashMap<>();
            this.configurationImp.setCurrentConfiguration(null);
            this.configurationImp.setStartConfiguration(null);
            success = true;
            message = "File loaded successfully";
        }catch (InstantiationError | InputMismatchException e){
            success = false;
            message = e.getMessage();
        }
        return new InputOperationAnswer(success, message);
    }

    @Override
    public InputOperationAnswer manualConfigRotors(String rotorsConfigLine) {
        if(machine.getRotors() != null) machine.reset();
        return configurationImp.manualConfigRotors(rotorsConfigLine, this.enigmaParts, this.machine);
    }

    @Override
    public InputOperationAnswer manualConfigOffsets(String configurationLine){
        return configurationImp.manualConfigRotorsOffsets(configurationLine, this.machine);
    }

    @Override
    public InputOperationAnswer manualConfigReflector(String configurationLine){
        return this.configurationImp.manualConfigReflector(configurationLine, this.machine, this.enigmaParts);
    }

    @Override
    public InputOperationAnswer manualConfigPlugBoard(String configurationLine){
        return this.configurationImp.manualConfigPlugBoard(configurationLine, this.machine);

    }
    @Override
    public InputOperationAnswer autoConfig(){
        if(machine.getRotors() != null) machine.reset();
        return   this.configurationImp.autoConfigMachine(enigmaParts, machine);

    }
    @Override
    public void resetMachine(){
        machine.reset();
        this.configurationImp.setCurrentConfiguration(this.configurationImp.createConfiguration(this.machine));
    }

    @Override
    public MachineDetailsAnswer getMachineDetails(){
        MachineDetailsAnswer answer = new MachineDetailsAnswer();
        int numOfUsedRotors;
        answer.setPossibleRotorsNum(this.enigmaParts.getRotors().size());
        answer.setNumOfReflectors(this.enigmaParts.getReflectors().size());
        answer.setNumOfProcessedMessages(this.getNumberOfMessageProcessed());
        if (this.configurationImp.getStartConfiguration() == null) {
            answer.setMachineConfig(false);
            answer.setUsedRotorNum(0);
        } else {
            answer.setUsedRotorNum(this.machine.getRotors().size());
            answer.setMachineConfig(true);
            answer.setInitialConfiguration(this.configurationImp.getStartConfiguration());
            answer.setCurrentState(this.configurationImp.getCurrentConfiguration());
        }
        return answer;
    }

    @Override
    public EncryptDecryptMessage encryptDecryptMessage(String message,boolean saveStats, boolean decipherState) {
        message = message.toUpperCase();
        EncryptDecryptMessage response = new EncryptDecryptMessage();
        Pair<Boolean,String> isValid = isAllLettersValid(message.toUpperCase());
        StringBuilder builder = new StringBuilder();
        if(!isValid.getKey()){
            response.setSuccess(false);
            response.setError("Error - Letter " + isValid.getValue() + "is not in machine ABC, please enter only letters from the abc " + machine.getKeyboard().getABC().keySet());
        }
        else {
            if(decipherState){
                Pair<Boolean,String> isAllWordInDictionary = this.decipherManager.isAllWordsInDictionary(message);
                if(!isAllWordInDictionary.getKey()){
                    response.setSuccess(false);
                    response.setError("Error - message contains words that are not in the dictionary");
                    return response;
                }
                message = isAllWordInDictionary.getValue();
            }
            response.setSrc(message);
            Instant start = Instant.now();
            for(int i = 0; i < message.length(); ++i){
                builder.append(machine.encryptDecrypt(String.valueOf(message.charAt(i))));
            }
            response.setOut(builder.toString());
            Instant end = Instant.now();
            response.setDuration(Duration.between(start, end).toNanos());
            response.setSuccess(true);
            this.configurationImp.setCurrentConfiguration(configurationImp.createConfiguration(this.machine));
            try{
                if(saveStats){
                    saveEncryptionData(response);
                }
            }catch (CloneNotSupportedException e){
                System.out.println("Couldn't save to statistics: (" + response.getSrc() + ") Encryption");
            }
        }
        return response;
    }

    public void saveEncryptionData(EncryptDecryptMessage data) throws CloneNotSupportedException {
        String currentConfiguration = configurationImp.getStartConfiguration();
        if (statistics.containsKey(currentConfiguration))
            statistics.get(currentConfiguration).add((EncryptDecryptMessage) data.clone());
        else {
            List<EncryptDecryptMessage> lst = new ArrayList<>();
            lst.add((EncryptDecryptMessage) data.clone());
            statistics.put(currentConfiguration, lst);
        }
    }

    @Override
    public StatisticsAnswer getStatistics() {
        Map<String, List<EncryptDecryptMessage>> statisticsCopy = SerializationUtils.clone(new LinkedHashMap<>(this.statistics));
        return new StatisticsAnswer(statisticsCopy);
    }
    @Override
    public SizeOfElementAnswer getNumOfReflectors(){
        SizeOfElementAnswer answer = new SizeOfElementAnswer();
        if(enigmaParts.getReflectors() != null) answer.setElementExists(true);
        answer.setSize(enigmaParts.getReflectors().size());
        return answer;
    }

    public EnigmaParts getEnigmaParts(){return this.enigmaParts;}

    @Override
    public long initializeDm(DmTaskDifficulty difficulty, String encrypted, int allowedAgents, int taskSize){
        List<Integer> rotorsIds = new ArrayList<>();
        machine.getRotors().forEach(i -> rotorsIds.add(i.getId()));

        switch (difficulty){
            case EASY:
                decipherManager.setRotorsId(rotorsIds);
                decipherManager.setReflectorId(machine.getReflector().getId());
                break;
            case MEDIUM:
            case HARD:
                decipherManager.setRotorsId(rotorsIds);
                break;
        }
        decipherManager.setMachineParts(enigmaParts);
        return decipherManager.initializeDm(difficulty, encrypted, allowedAgents, taskSize);
    }
    @Override
    public void startBruteForce(){
        this.decipherManager.startBruteForce();
    }

    private int getNumberOfMessageProcessed(){
        return statistics
                .values()
                .stream()
                .map(List::size)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private Pair<Boolean,String> isAllLettersValid(String message){
        Keyboard keyboard = machine.getKeyboard();
        String letter;
        for(int i = 0; i < message.length(); ++i){
            letter = String.valueOf(message.charAt(i));
            if(!keyboard.isKeyExists(letter)){
                return new Pair<>(false, letter);
            }
        }
        return new Pair<>(true, null);
    }

    @Override
    public Machine getMachine() {
        return machine;
    }

    @Override
    public DmAnswer getDmDetails(){
        DmAnswer answer= new DmAnswer();
        answer.setDictionary(decipherManager.getDictionary());
        answer.setMaxAgents(decipherManager.getMaxAgents());
        return answer;
    }

    @Override
    public void removePlugsFromMachine(){
        this.machine .setPlugBord(new PlugBoardImp(new HashMap<>()));
        String newConfiguration = configurationImp.createConfiguration(machine);
        configurationImp.setCurrentConfiguration(newConfiguration);
        configurationImp.setStartConfiguration(newConfiguration);
    }

    public static void main(String[] args) {
        Engine engine = new EngineImp();
        System.out.println(engine.loadFromFile("/Users/talkoren/tal/University/mta/java_course/ex2_files/ex2-basic (1).xml").getMessage());
        engine.autoConfig();
        engine.getMachine().setPlugBord(new PlugBoardImp(new HashMap<>()));
        engine.manualConfigPlugBoard("");
        System.out.println(engine.getMachineDetails().getCurrentState());
        EncryptDecryptMessage message= engine.encryptDecryptMessage("element fire",false,true);
        System.out.println("Encryption method in decipher mode is : " + (message.getSuccess() ? "Success" : "failed"));
        long num = engine.initializeDm(DmTaskDifficulty.EASY, message.getOut(), 3, 100);
        System.out.println("Number of Tasks : " + num);
        engine.startBruteForce();


    }
}

///Users/talkoren/tal/University/mta/java_programing/enigma_proj/engima_proj_part1_testFiles/ex1-sanity-paper-enigma.xml

//
