package Engine.DM;

import Engine.DM.DectyptionTask.DecryptionTask;
import Engine.enigmaParts.EnigmaParts;
import Engine.enums.DmTaskDifficulty;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class DecipherManager {
    private Set<String> dictionary;
    private  int maxAgents;
    private EnigmaParts machineParts;
    private List<Integer> rotorsId;
    private int reflectorId;
    private DmTaskDifficulty difficulty;
    private long  numberOfTasks;
    private int taskSize;
    private ExecutorService agents;

    private BlockingQueue<DecryptionTask> tasks;
    private BlockingQueue<?> answers;



    public DecipherManager(Set<String> dictionary, int maxAgents){
        this.dictionary = dictionary;
        this.maxAgents = maxAgents;
    }


    public long initializeDm(DmTaskDifficulty taskDifficulty, String encryptedStr, int numberOfAgentsAllowed, int taskSize){
        this.taskSize = taskSize;
        this.difficulty = taskDifficulty;
        this.agents = Executors.newFixedThreadPool(numberOfAgentsAllowed);
        this.tasks = new ArrayBlockingQueue<>(numberOfAgentsAllowed * 10);
        this.answers = new LinkedBlockingQueue<>();
        return calculateNumberOfTasks();
    }


    public long calculateNumberOfTasks(){
        switch (difficulty){
            case EASY:
                numberOfTasks = (long) Math.pow(machineParts.getKeyboard().getABC().size(), rotorsId.size())/ taskSize;
                break;
            case MEDIUM:
                numberOfTasks = machineParts.getReflectors().size();
                numberOfTasks *= (long) Math.pow(machineParts.getKeyboard().getABC().size(), rotorsId.size());
                numberOfTasks /= taskSize;
                break;
            case HARD:
                numberOfTasks = CalculationsUtils.factorial(rotorsId.size());
                numberOfTasks *= machineParts.getReflectors().size();
                numberOfTasks *= (long) Math.pow(machineParts.getKeyboard().getABC().size(), rotorsId.size());
                numberOfTasks /= taskSize;
                break;
            case IMPOSSIBLE:
                long total = 0;
                int possibleRotors = machineParts.getRotors().size();
                for(int i = machineParts.getRotorCount(); i <= Math.min(possibleRotors, 99); ++i){
                    total += (long) (CalculationsUtils.NumOfPermutations_k_of_n(possibleRotors, i) *
                            Math.pow(machineParts.getKeyboard().getABC().size(), i)) *
                            machineParts.getReflectors().size();
                }
                numberOfTasks = total / taskSize;
        }
        return numberOfTasks;
    }

    public Set<String> getDictionary() {
        return dictionary;
    }

    public void setMachineParts(EnigmaParts machineParts) {
        this.machineParts = machineParts;
    }

    public void setDictionary(Set<String> dictionary) {
        this.dictionary = dictionary;
    }

    public void setRotorsId(List<Integer> rotorsId) {
        this.rotorsId = rotorsId;
    }

    public void setReflectorId(int reflectorId) {
        this.reflectorId = reflectorId;
    }
}
