package Engine.DM.TaskProducer;

import Engine.DM.CalculationsUtils;
import Engine.DM.DectyptionTask.DecryptionTask;
import Engine.enigmaParts.EnigmaParts;
import Engine.enums.DmTaskDifficulty;
import machine.Machine;
import machine.MachineImp;
import machine.parts.keyboard.Keyboard;
import machine.parts.reflector.Reflector;
import machine.parts.reflector.ReflectorImp;
import machine.parts.rotor.Rotor;
import machine.parts.rotor.RotorImp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class TaskProducer implements Runnable{
    private EnigmaParts machineParts;
    DmTaskDifficulty difficulty;
    private final Set<String> dictionary;
    private final String encryptedStr;
    private final int taskSize;
    private BlockingQueue<DecryptionTask> tasks;
    private List<Integer> rotorsId;
    private int reflectorId;
    private long offsetsPermutationsNum;

    public TaskProducer(EnigmaParts machineParts, DmTaskDifficulty difficulty, Set<String> dictionary, String encryptedStr, int taskSize, BlockingQueue<DecryptionTask> tasks
                        , List<Integer> rotorsId, int reflectorId){
        this.machineParts = machineParts;
        this.difficulty = difficulty;
        this.dictionary = dictionary;
        this.encryptedStr = encryptedStr;
        this.taskSize = taskSize;
        this.tasks = tasks;
        this.reflectorId = reflectorId;
        this.rotorsId = rotorsId;
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
        }
    }

    private void createTasksForEasyState(List<Integer> rotorsId, int reflectorId){
        offsetsPermutationsNum = (long) Math.pow(machineParts.getKeyboard().getABC().size(), rotorsId.size()) / taskSize;
        List<Integer> offsetConfig = new ArrayList<>(rotorsId.size());
        for(int i = 0; i < offsetsPermutationsNum; i += taskSize){
            try{
                tasks.put(new DecryptionTask(
                        createNewMachine(rotorsId, reflectorId, offsetConfig),
                        offsetConfig,
                        offsetsPermutationsNum - i < taskSize ? (int) (offsetsPermutationsNum - i) : taskSize,
                        this.dictionary,
                        this.encryptedStr
                ));
            }catch (InterruptedException e){
                System.out.println(Thread.currentThread().getName() + " has interrupted\n");
                System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
            }
            moveToNextConfig(offsetConfig, offsetsPermutationsNum - i < taskSize ? (int) (offsetsPermutationsNum - i) : taskSize);
        }
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
