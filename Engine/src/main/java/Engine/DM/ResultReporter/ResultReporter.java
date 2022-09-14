package Engine.DM.ResultReporter;

import Engine.DM.MyThreadPool.MyThreadPool;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class ResultReporter implements Runnable {

    private BlockingQueue<Runnable> answers;
    private SimpleLongProperty finishedTasks;
    private SimpleBooleanProperty isPause;
    private long totalWork;
    private MyThreadPool agents;

    public ResultReporter(BlockingQueue<Runnable> answers, SimpleLongProperty finishedTasks, SimpleBooleanProperty isPause, long totalWork, MyThreadPool agents){
        this.answers = answers;
        this.finishedTasks = finishedTasks;
        this.isPause = isPause;
        this.totalWork = totalWork;
        this.agents = agents;
    }

    @Override
    public void run() {
        String errorMessage;
        try {
//            Thread.sleep(3000); //To give some time for the producer to work
            System.out.println(Thread.currentThread().getName() + " start listening to results");
            while(!answers.isEmpty() || totalWork != finishedTasks.get()){
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
