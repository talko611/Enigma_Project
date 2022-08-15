package Engine;


import Engine.configuration.ConfigurationImp;
import Engine.engineAnswers.EncryptDecryptMessage;
import Engine.engineAnswers.InputOperationAnswer;
import Engine.engineAnswers.MachineDetailsAnswer;
import Engine.engineAnswers.StatisticsAnswer;
import Engine.enigmaParts.EnigmaParts;
import Engine.XMLLoader.FileReader;
import Engine.XMLLoader.XMLLoaderImp;
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
        int numOfPossibleRotors = this.enigmaParts.getRotors().size();
        int numOfUsedRotors = this.machine.getRotors() == null ? 0 : this.machine.getRotors().size();
        answer.setUsedVsAvailableRotors(numOfUsedRotors + "/" + numOfPossibleRotors);
        answer.setNumOfReflectors(this.enigmaParts.getReflectors().size());
        answer.setNumOfProcessedMessages(this.getNumberOfMessageProcessed());
        if (numOfUsedRotors == 0) {
            answer.setMachineConfig(false);
        } else {
            answer.setMachineConfig(true);
            answer.setInitialConfiguration(this.configurationImp.getStartConfiguration());
            answer.setCurrentState(this.configurationImp.getCurrentConfiguration());
        }
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

    public static void main(String[] args) throws CloneNotSupportedException {
        EngineImp engineImp = new EngineImp();
        engineImp.loadFromFile("/Users/talkoren/tal/University/mta/java_programing/enigma_proj/engima_proj_part1_testFiles/ex1-sanity-paper-enigma.xml");
        engineImp.autoConfig();
        EncryptDecryptMessage message = engineImp.encryptDecrypt("dafagfa");

        printDetails(engineImp.getMachineDetails());


//        engineImp.loadFromFile("/Users/talkoren/tal/University/mta/java_programing/enigma_proj/engima_proj_part1_testFiles/ex1-sanity-small.xml");
//        engineImp.autoConfig();
//        System.out.println("done");
    }

     private static void printDetails(MachineDetailsAnswer detailsAnswer){
         System.out.println("Machine details:");
        System.out.println("Rotor num use: " + detailsAnswer.getUsedVsAvailableRotors());
        System.out.println("Reflector number: " + detailsAnswer.getNumOfReflectors());
        System.out.println("Number of messages encrypt: " + detailsAnswer.getNumOfProcessedMessages());
        if(detailsAnswer.isMachineConfig()){
            System.out.println("Initial config: " + detailsAnswer.getInitialConfiguration());
            System.out.println("Current state: " + detailsAnswer.getCurrentState());
        }
        else{
            System.out.println("Machine is not config yet!");
        }
    }

    private static void printStats(StatisticsAnswer statisticsAnswer){
        System.out.println("statistics:");
        statisticsAnswer.getStats().forEach((i,k) -> {
            System.out.println(i);
            k.forEach(s-> System.out.print(s.getSrc()+ "---> " + s.getOut() +" " + String.format("%,d\n", s.getDuration())));
        });
    }

}
