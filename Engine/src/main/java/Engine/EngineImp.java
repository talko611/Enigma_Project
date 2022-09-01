package Engine;


import Engine.configuration.ConfigurationImp;
import Engine.engineAnswers.*;
import Engine.enigmaParts.EnigmaParts;
import Engine.XMLLoader.FileReader;
import Engine.XMLLoader.XMLLoaderImp;
import javafx.concurrent.Task;
import javafx.util.Pair;
import machine.Machine;
import machine.MachineImp;
import machine.parts.keyboard.Keyboard;
import org.apache.commons.lang3.SerializationUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


public class EngineImp implements Engine{
    public Machine machine;
    private final EnigmaParts enigmaParts;
    private  Map<String,List<EncryptDecryptMessage>> statistics;

    private final ConfigurationImp configurationImp;



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
//             numOfUsedRotors = 0;
            answer.setUsedRotorNum(0);
        } else {
//            numOfUsedRotors = this.machine.getRotors().size();
            answer.setUsedRotorNum(this.machine.getRotors().size());
            answer.setMachineConfig(true);
            answer.setInitialConfiguration(this.configurationImp.getStartConfiguration());
            answer.setCurrentState(this.configurationImp.getCurrentConfiguration());
        }
//        answer.setUsedVsAvailableRotors(numOfUsedRotors + "/" + numOfPossibleRotors);
        return answer;
    }

    @Override
    public EncryptDecryptMessage encryptDecrypt(String message) throws CloneNotSupportedException {
        EncryptDecryptMessage response = new EncryptDecryptMessage();
        Pair<Boolean,String> isValid = isAllLettersValid(message.toUpperCase());
        if(!isValid.getKey()){
            response.setSuccess(false);
            response.setError("Error - Letter " + isValid.getValue() + "is not in machine ABC, please enter only letters from the abc " + machine.getKeyboard().getABC().keySet());
        }
        else {
            response.setSrc(message);
            Instant start = Instant.now();
            response.setOut(machine.encryptDecrypt(message));
            Instant end = Instant.now();
            response.setDuration(Duration.between(start, end).toNanos());
            response.setSuccess(true);
            this.configurationImp.setCurrentConfiguration(configurationImp.createConfiguration(this.machine));
            String currentConfiguration = configurationImp.getStartConfiguration();
            if (statistics.containsKey(currentConfiguration))
                statistics.get(currentConfiguration).add((EncryptDecryptMessage) response.clone());
            else {
                List<EncryptDecryptMessage> lst = new ArrayList<>();
                lst.add((EncryptDecryptMessage) response.clone());
                statistics.put(currentConfiguration, lst);
            }

        }
        return response;
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

}
