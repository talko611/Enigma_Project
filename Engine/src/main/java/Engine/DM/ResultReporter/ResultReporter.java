package Engine.DM.ResultReporter;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class ResultReporter implements Runnable {

    private BlockingQueue<Runnable> answers;
    private ThreadPoolExecutor agents;
    private BlockingQueue<Runnable> decryptionTasks;
    private SimpleLongProperty taskNum;
    private SimpleBooleanProperty isPause;

    public ResultReporter(BlockingQueue<Runnable> answers, ThreadPoolExecutor agents, BlockingQueue<Runnable> decryptionTasks, SimpleLongProperty taskNum, SimpleBooleanProperty isPause){
        this.agents = agents;
        this.answers = answers;
        this.decryptionTasks = decryptionTasks;
        this.taskNum = taskNum;
        this.isPause = isPause;
    }

    @Override
    public void run() {
//        int counter = 0;
        //To give some time for the producer to work
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " has interrupted with exception(After first sleep)");
            return;
        }
        System.out.println(Thread.currentThread().getName() + " start listening to results");
        while(!answers.isEmpty() || agents.getActiveCount() != 0 || !decryptionTasks.isEmpty() ){
            isPaused();
            if(!answers.isEmpty()){
                try {
                    Runnable update = answers.take();
                    update.run();
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " has interrupted with exception(tried to pull new reporting task)");
                    return;
                }
            }
            if(Thread.interrupted()){
                System.out.println(Thread.currentThread().getName() + " has interrupted without exception(exiting...)");
                return;
            }
        }
        agents.shutdown();
        System.out.println(Thread.currentThread().getName() + " finish work");
    }

    private synchronized void isPaused(){
        while (isPause.get()){
            try {
                this.wait(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(Thread.currentThread().getName() + " got interrupt when was paused");
            }
        }
    }
}
