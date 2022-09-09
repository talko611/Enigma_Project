package Engine.DM.TaskProducer;

import Engine.DM.CalculationsUtils;
import Engine.DM.DectyptionTask.DecryptionTask;
import Engine.enigmaParts.EnigmaParts;
import Engine.enums.DmTaskDifficulty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import machine.Machine;
import machine.MachineImp;
import machine.parts.keyboard.Keyboard;
import machine.parts.keyboard.KeyboardImp;
import machine.parts.plugBoard.PlugBoardImp;
import machine.parts.reflector.ReflectorImp;
import machine.parts.rotor.Rotor;
import machine.parts.rotor.RotorImp;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class TaskProducer implements Runnable{
    private final EnigmaParts machineParts;
    DmTaskDifficulty difficulty;
    private final Set<String> dictionary;
    private final String encryptedStr;
    private final int taskSize;
    private final BlockingQueue<Runnable> tasks;
    private final List<Integer> rotorsId;
    private final int reflectorId;

    private Consumer<Integer> updateNumOfTasks;



    public TaskProducer(EnigmaParts machineParts, DmTaskDifficulty difficulty, Set<String> dictionary, String encryptedStr, int taskSize, BlockingQueue<Runnable> tasks
                        , List<Integer> rotorsId, int reflectorId, Consumer<Integer> updateNumOfTasks){
        this.machineParts = machineParts;
        this.difficulty = difficulty;
        this.dictionary = dictionary;
        this.encryptedStr = encryptedStr;
        this.taskSize = taskSize;
        this.tasks = tasks;
        this.reflectorId = reflectorId;
        this.rotorsId = rotorsId;
        this.updateNumOfTasks = updateNumOfTasks;
    }

    @Override
    public void run() {
        switch (difficulty){
            case EASY:
                createTasksForEasyState(this.rotorsId, this.reflectorId);
                break;
            case MEDIUM:
                createTasksForMediumState(this.rotorsId);
            case HARD:
                createTasksForHardState(this.rotorsId);
            case IMPOSSIBLE:
                createTasksForImpossibleState();
        }
    }

    private void createTasksForEasyState(List<Integer> rotorsId, int reflectorId){
        long offsetsPermutationsNum = (long) Math.pow(machineParts.getKeyboard().getABC().size(), rotorsId.size());
        List<Integer> offsetConfig = new ArrayList<>(Collections.nCopies(rotorsId.size(),0));
        int counter = 0;
        int nextTaskSize = 0;
        while( counter < offsetsPermutationsNum){
            nextTaskSize = offsetsPermutationsNum - counter < taskSize ? (int) (offsetsPermutationsNum - counter) : taskSize;
            try{
                tasks.put(new DecryptionTask(
                        createNewMachine(rotorsId, reflectorId, offsetConfig),
                        new ArrayList<>(offsetConfig),
                        nextTaskSize,
                        this.dictionary,
                        this.encryptedStr,
                        updateNumOfTasks
                ));
            }catch (InterruptedException e){
                System.out.println(Thread.currentThread().getName() + " has interrupted\n");
                System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
            }
            moveToNextConfig(offsetConfig, nextTaskSize);
            counter += nextTaskSize;
        }
        System.out.println("Finish produce all tasks");
    }

    private void  createTasksForMediumState(List<Integer> rotorsId){
        for(Integer reflectorId : machineParts.getReflectors().keySet()){
            createTasksForEasyState(rotorsId, reflectorId);
        }
    }

    private void createTasksForHardState(List<Integer> rotorsId){
        List<List<Integer>> idsPermutations = CalculationsUtils.allPermutationOfNElements(rotorsId);
        idsPermutations.forEach(this::createTasksForMediumState);
    }

    private void createTasksForImpossibleState(){
        Set<Integer> rotorsId = machineParts.getRotors().keySet();
        Set<Set<Integer>> allGroupsInSizeK;
        for(int i = machineParts.getRotorCount(); i <= Math.min(machineParts.getRotors().size(), 99); ++i){
            allGroupsInSizeK = CalculationsUtils.add_All_Sub_Groups_SizeK_Out_Of_N_Elements(rotorsId, i);
            allGroupsInSizeK.forEach(group ->{
                List<Integer> lst = new ArrayList<>(group);
                createTasksForHardState(lst);
            });
        }
    }

    private Machine createNewMachine(List<Integer> rotorsId, int reflectorId, List<Integer> offsets){
        Keyboard keyboard = machineParts.getKeyboard();
        Machine machine = new MachineImp();
        List<Rotor> rotors = new ArrayList<>();
        //Copy needed rotors
        rotorsId.forEach(i -> rotors.add(new RotorImp((RotorImp) machineParts.getRotorById(i))));
        //Set to initial configuration
        for(int i = 0; i< offsets.size(); ++i){
            rotors.get(i).setOffset(keyboard.getEntryMatchKey(offsets.get(i)));
        }
        machine.setRotors(rotors);
        //Set reflector
        machine.setReflector(new ReflectorImp((ReflectorImp) machineParts.getReflectors().get(reflectorId)));
        //Set keyboard
        machine.setKeyboard(new KeyboardImp((KeyboardImp) keyboard));
        //Set null plugBoard
        machine.setPlugBord(new PlugBoardImp(new HashMap<>()));

        return machine;
    }

    private void moveToNextConfig(List<Integer> offsetsConfig, int taskSize){
        int base = machineParts.getKeyboard().getABC().size();
        for(int i = 0; i < taskSize; ++i){
            boolean moveNext = true;
            for(int j = 0; j < offsetsConfig.size() && moveNext; ++j){
                offsetsConfig.set(j, Math.floorMod(offsetsConfig.get(j) + 1, base));
                moveNext = offsetsConfig.get(j) == 0;
            }
        }
    }
}
