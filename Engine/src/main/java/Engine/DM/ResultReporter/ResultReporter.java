package Engine.DM.ResultReporter;

import Engine.DM.MyThreadPool.MyThreadPool;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;

import java.util.concurrent.BlockingQueue;


public class ResultReporter implements Runnable {

    private final BlockingQueue<Runnable> answers;
    private final SimpleLongProperty finishedTasks;
    private final SimpleBooleanProperty isPause;
    private final long totalWork;
    private final MyThreadPool agents;

    public ResultReporter(BlockingQueue<Runnable> answers, SimpleLongProperty finishedTasks, SimpleBooleanProperty isPause, long totalWork, MyThreadPool agents){
        this.answers = answers;
        this.finishedTasks = finishedTasks;
        this.isPause = isPause;
        this.totalWork = totalWork;
        this.agents = agents;
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " start listening to results");
            while(!answers.isEmpty() || totalWork != finishedTasks.get()){
                isPaused();
                if(!answers.isEmpty()){
                    Runnable update = answers.take();
                    update.run();
                }
                if(Thread.interrupted()){
                    agents.shutdown();
                    System.out.println(Thread.currentThread().getName() + " has interrupted without exception(exiting...)");
                    return;
                }
            }
            agents.shutdown();
            System.out.println(Thread.currentThread().getName() + " finish work");
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " has interrupted with exception thrown");
        }
    }

    private synchronized void isPaused() throws InterruptedException {
        while (isPause.get()){
            this.wait(2000);
        }
    }
}
