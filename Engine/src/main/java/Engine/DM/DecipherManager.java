package Engine.DM;

import Engine.DM.MyThreadPool.MyThreadPool;
import Engine.DM.ResultReporter.ResultReporter;
import Engine.DM.TaskProducer.TaskProducer;
import Engine.engineAnswers.DmInitAnswer;
import Engine.enigmaParts.EnigmaParts;
import Engine.enums.DmTaskDifficulty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DecipherManager {
    private Set<String> dictionary;
    private final String forbiddenChars;
    private final int maxAgents;
    private EnigmaParts machineParts;
    private List<Integer> rotorsId;
    private int reflectorId;
    private DmTaskDifficulty difficulty;
    private Long  totalTasks;
    private int taskSize;
    private MyThreadPool agents;
    private BlockingQueue<Runnable> tasks;
    private BlockingQueue<Runnable> answers;
    private String messageToDecrypt;
    private final SimpleLongProperty finishedTasks;
    private Thread tasksProducer;
    private Thread resultReporter;




    public DecipherManager(Set<String> dictionary, int maxAgents, String forbiddenChars){
        this.dictionary = dictionary;
        this.maxAgents = maxAgents;
        this.forbiddenChars = forbiddenChars;
        this.finishedTasks = new SimpleLongProperty(0);
    }

    public DmInitAnswer initializeDm(DmTaskDifficulty taskDifficulty, String encryptedStr, int numberOfAgentsAllowed, int taskSize){
        DmInitAnswer answer = new DmInitAnswer();
        this.taskSize = taskSize;
        this.difficulty = taskDifficulty;
        finishedTasks.set(0);
        this.tasks = new ArrayBlockingQueue<>(1000);
        this.agents= new MyThreadPool(numberOfAgentsAllowed, numberOfAgentsAllowed, 0L, TimeUnit.MILLISECONDS, this.tasks, new ThreadFactory() {
            private int threadCounter = 1;
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("Agent " + threadCounter++);
                return thread;
            }
        });
        this.answers = new LinkedBlockingQueue<>();
        this.messageToDecrypt= encryptedStr;
        try{
            totalTasks = calculateNumberOfTasks();
            answer.setNumOfTasks(totalTasks);
            answer.setSuccess(true);
            answer.setMessage("DM set successfully. Read to start");
        }catch (InputMismatchException e){
            answer.setSuccess(false);
            answer.setMessage("Task size is larger then total work size");
        }
         return answer;
    }

    public void startBruteForce(BiConsumer<String, Pair<String, String>> reportUpdate , Consumer<Integer> progressUpdate, SimpleBooleanProperty isPause){
        agents.prestartAllCoreThreads();
        tasksProducer = new Thread(new TaskProducer(machineParts, difficulty, dictionary, messageToDecrypt, taskSize,
                                    tasks, rotorsId, reflectorId, finishedTasks, answers, reportUpdate, progressUpdate, isPause),"Task Producer");

        tasksProducer.start();
        resultReporter = new Thread(new ResultReporter(answers,finishedTasks, isPause, totalTasks,agents), "Result Reporter");
        resultReporter.start();
    }

    public void stopBruteForce(){
        agents.shutdownNow();
        tasksProducer.interrupt();
        resultReporter.interrupt();
    }

    public void pauseWork(){
        agents.pause();
    }

    public void resumeWork(){
        agents.resume();
    }

    public long calculateNumberOfTasks(){
        long numberOfTasks = 0;
        switch (difficulty){
            case EASY:
                numberOfTasks = (long) Math.pow(machineParts.getKeyboard().getABC().size(), rotorsId.size());
                break;
            case MEDIUM:
                numberOfTasks = machineParts.getReflectors().size();
                numberOfTasks *= (long) Math.pow(machineParts.getKeyboard().getABC().size(), rotorsId.size());
                break;
            case HARD:
                numberOfTasks = CalculationsUtils.factorial(rotorsId.size());
                numberOfTasks *= machineParts.getReflectors().size();
                numberOfTasks *= (long) Math.pow(machineParts.getKeyboard().getABC().size(), rotorsId.size());
                break;
            case IMPOSSIBLE:
                int possibleRotors = machineParts.getRotors().size();
                for(int i = machineParts.getRotorCount(); i <= Math.min(possibleRotors, 99); ++i){
                    numberOfTasks += (long) (CalculationsUtils.NumOfPermutations_k_of_n(possibleRotors, i) *
                            Math.pow(machineParts.getKeyboard().getABC().size(), i)) *
                            machineParts.getReflectors().size();
                }
        }
        if(taskSize > numberOfTasks){
            throw new InputMismatchException();
        }
        numberOfTasks /= taskSize;
        return numberOfTasks;
    }

    public Set<String> getDictionary() {
        return dictionary;
    }

    public int getMaxAgents() {
        return maxAgents;
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

    public Pair<Boolean,String> isAllWordsInDictionary(String message){
        List<String> words = Arrays.asList(message.split(" "));
        words = words.stream()
                .map(word -> word.replaceAll("[" + forbiddenChars + "]", "").toUpperCase())
                .collect(Collectors.toList());
        for(String word : words){
            if(!dictionary.contains(word)){
                return new Pair<>(false, message);
            }
        }
        StringBuilder builder = new StringBuilder();
        words.forEach(word -> builder.append(word).append(" "));
        builder.trimToSize();
        builder.deleteCharAt(builder.toString().length() -1);
        return new Pair<>(true, builder.toString());
    }
}
