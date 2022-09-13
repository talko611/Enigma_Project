package Engine.DM.DectyptionTask;


import Engine.DM.ReportTask.ReportTask;
import Engine.configuration.ConfigurationImp;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.util.Pair;
import machine.Machine;
import machine.parts.keyboard.Keyboard;
import machine.parts.rotor.Rotor;
import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DecryptionTask implements Runnable{
    private final Machine machine;
    private final int taskSize;
    List<Integer> initialConfig;
    private final Set<String> dictionary;
    private final String encryptedStr;
    private final ConfigurationImp configurator;
    private final SimpleLongProperty numOfTasks;
    private final  BiConsumer<String, Pair<String, String>> updateResult;
    private final BlockingQueue<Runnable> reportTasks;
    private Consumer<Integer> progressUpdate;



    public DecryptionTask(Machine machine, List<Integer> initialConfig, int taskSize, Set<String> dictionary, String encryptedStr, SimpleLongProperty numOfTasks,
                          BiConsumer<String, Pair<String, String>> updateResult, BlockingQueue<Runnable> reportTasks, Consumer<Integer> progressUpdate) {
        this.machine = machine;
        this.taskSize = taskSize;
        this.dictionary = dictionary;
        this.encryptedStr = encryptedStr;
        this.initialConfig = initialConfig;
        this.configurator = new ConfigurationImp();
        this.numOfTasks = numOfTasks;
        this.updateResult = updateResult;
        this.reportTasks = reportTasks;
        this.progressUpdate = progressUpdate;
    }

    @Override
    public void run() {
        String currenDecryption;
        for(int i = 0; i < taskSize; ++i){
            setOffset();
            currenDecryption = decrypt();
            if(isOptionalDecryption(currenDecryption)){
                try {
                    System.out.println(Thread.currentThread().getName() + " has found a candidate");
                    System.out.println("\tConfiguration: " + configurator.createConfiguration(machine));
                    System.out.println("\tDecryption: "  + currenDecryption + "\n");
                    machine.reset();
                    reportTasks.put(new ReportTask(currenDecryption, configurator.createConfiguration(machine),Thread.currentThread().getName(), updateResult));
                    System.out.println(Thread.currentThread().getName() + " enter result to answer queue");
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " has interrupted with exception");
                    return;
                }
            }
            move();
            if(Thread.interrupted()){
                System.out.println(Thread.currentThread().getName() + " has interrupted without exception");
                return;
            }
        }
        synchronized (numOfTasks){
            numOfTasks.set(numOfTasks.get() + 1);
            Platform.runLater(()->progressUpdate.accept(numOfTasks.intValue()));
        }
    }

    private void setOffset(){
        machine.reset();
        List<Rotor> rotors = machine.getRotors();
        Keyboard keyboard = machine.getKeyboard();
        for(int i  = 0; i < initialConfig.size(); ++i){
            rotors.get(i).setOffset(keyboard.getEntryMatchKey(initialConfig.get(i)));
        }
        configurator.setCurrentConfiguration(configurator.createConfiguration(machine));
    }

    private void move(){
        int base = machine.getKeyboard().getABC().size();
        boolean moveNext = true;
        for(int i = 0; i < initialConfig.size() && moveNext; ++i){
            initialConfig.set(i, Math.floorMod(initialConfig.get(i) + 1, base));
            moveNext = initialConfig.get(i) == 0;
        }
    }

    private String decrypt(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < encryptedStr.length(); ++i){
            builder.append(machine.encryptDecrypt(String.valueOf(encryptedStr.charAt(i))));
        }
        return builder.toString();
    }

    private boolean isOptionalDecryption(String decryptedStr){
        String[] words = decryptedStr.split(" ");
        for(String word : words){
            if(!dictionary.contains(word)){
                return false;
            }
        }
        return true;
    }
}
