package Engine.DM.DectyptionTask;


import Engine.configuration.ConfigurationImp;
import machine.Machine;
import machine.parts.keyboard.Keyboard;
import machine.parts.rotor.Rotor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DecryptionTask implements Runnable{
    private final Machine machine;
    private final int taskSize;
    List<Integer> initialConfig;
    private final Set<String> dictionary;
    private final String encryptedStr;
    private final ConfigurationImp configurator;

    private final Consumer<Integer> updateNumOfTasks;




    public DecryptionTask(Machine machine,List<Integer> initialConfig, int taskSize, Set<String> dictionary, String encryptedStr, Consumer<Integer> updateNumOfTasks) {
        this.machine = machine;
        this.taskSize = taskSize;
        this.dictionary = dictionary;
        this.encryptedStr = encryptedStr;
        this.initialConfig = initialConfig;
        this.configurator = new ConfigurationImp();
        this.updateNumOfTasks = updateNumOfTasks;
    }

    @Override
    public void run() {
//        System.out.println(Thread.currentThread().getName() + " Starting task");
        String currenDecryption;

        for(int i = 0; i < taskSize; ++i){
            setOffset();
            currenDecryption = decrypt();
            if(isOptionalDecryption(currenDecryption)){
                System.out.println("Found optional Decryption by " + Thread.currentThread().getName());
                System.out.println("Decrypted message is :" + currenDecryption);
                System.out.println("Machine configuration is " + configurator.createConfiguration(this.machine));
            }
            move();
        }
//        System.out.println(Thread.currentThread().getName() + " Finish tasks");
        synchronized (updateNumOfTasks){
//            updateNumOfTasks.accept( taskSize);
        }
    }

    private void setOffset(){
        machine.reset();
        List<Rotor> rotors = machine.getRotors();
        Keyboard keyboard = machine.getKeyboard();
        for(int i  = 0; i < initialConfig.size(); ++i){
            rotors.get(i).setOffset(keyboard.getEntryMatchKey(initialConfig.get(i)));
        }
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
