package Engine.DM.TaskProducer;

import Engine.DM.CalculationsUtils;
import Engine.DM.DectyptionTask.DecryptionTask;
import Engine.enigmaParts.EnigmaParts;
import Engine.enums.DmTaskDifficulty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.util.Pair;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TaskProducer implements Runnable{
    private final EnigmaParts machineParts;
    DmTaskDifficulty difficulty;
    private final Set<String> dictionary;
    private final String encryptedStr;
    private final int taskSize;
    private final BlockingQueue<Runnable> decryptionTasks;
    private final List<Integer> rotorsId;
    private final int reflectorId;
    private SimpleLongProperty numOfTasks;
    private BlockingQueue<Runnable> reportTasks;
    private BiConsumer<String, Pair<String, String>> report;
    private  Consumer<Integer> progressUpdate;
    private SimpleBooleanProperty isPause;



    public TaskProducer(EnigmaParts machineParts, DmTaskDifficulty difficulty, Set<String> dictionary, String encryptedStr, int taskSize, BlockingQueue<Runnable> tasks
                        , List<Integer> rotorsId, int reflectorId, SimpleLongProperty numOfTasks, BlockingQueue<Runnable> reportTasks,
                        BiConsumer<String, Pair<String, String>> report, Consumer<Integer> progressUpdate, SimpleBooleanProperty isPause){
        this.machineParts = machineParts;
        this.difficulty = difficulty;
        this.dictionary = dictionary;
        this.encryptedStr = encryptedStr;
        this.taskSize = taskSize;
        this.decryptionTasks = tasks;
        this.reflectorId = reflectorId;
        this.rotorsId = rotorsId;
        this.numOfTasks = numOfTasks;
        this.reportTasks = reportTasks;
        this.report = report;
        this.progressUpdate = progressUpdate;
        this.isPause = isPause;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " start producing");
        switch (difficulty){
            case EASY:
                createTasksForEasyState(this.rotorsId, this.reflectorId);
                break;
            case MEDIUM:
                createTasksForMediumState(this.rotorsId);
                break;
            case HARD:
                createTasksForHardState(this.rotorsId);
                break;
            case IMPOSSIBLE:
                createTasksForImpossibleState();
                break;
        }
        System.out.println(Thread.currentThread().getName() + " finish producing");
    }

    private void createTasksForEasyState(List<Integer> rotorsId, int reflectorId){
        long offsetsPermutationsNum = (long) Math.pow(machineParts.getKeyboard().getABC().size(), rotorsId.size());
        List<Integer> offsetConfig = new ArrayList<>(Collections.nCopies(rotorsId.size(),0));
        int counter = 0;
        int nextTaskSize = 0;
        while( counter < offsetsPermutationsNum){
            isPaused();
            nextTaskSize = offsetsPermutationsNum - counter < taskSize ? (int) (offsetsPermutationsNum - counter) : taskSize;
            try{
                decryptionTasks.put(new DecryptionTask(
                        createNewMachine(rotorsId, reflectorId, offsetConfig),
                        new ArrayList<>(offsetConfig),
                        nextTaskSize,
                        this.dictionary,
                        this.encryptedStr,
                        numOfTasks,
                        report,
                        reportTasks,
                        progressUpdate
                ));
                if(Thread.interrupted()){
                    System.out.println(Thread.currentThread().getName() + " has interrupted without exception\n");
                    return;
                }
            }catch (InterruptedException e){
                System.out.println(Thread.currentThread().getName() + " has interrupted by exception thrown\n");
                System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
                return;
            }
            moveToNextConfig(offsetConfig, nextTaskSize);
            counter += nextTaskSize;
        }
    }

    private void  createTasksForMediumState(List<Integer> rotorsId){
        for(Integer reflectorId : machineParts.getReflectors().keySet()){
            createTasksForEasyState(new ArrayList<>(rotorsId), reflectorId);
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

    private synchronized void isPaused(){
        while (isPause.get()){
            try {
                this.wait(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(Thread.currentThread().getName() + " got interrupted while was paused");
            }
        }
    }
}
