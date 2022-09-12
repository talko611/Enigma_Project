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
    private Object pause;
    private SimpleBooleanProperty isPause;

    public ResultReporter(BlockingQueue<Runnable> answers, ThreadPoolExecutor agents, BlockingQueue<Runnable> decryptionTasks, SimpleLongProperty taskNum,
                          Object pause, SimpleBooleanProperty isPause){
        this.agents = agents;
        this.answers = answers;
        this.decryptionTasks = decryptionTasks;
        this.taskNum = taskNum;
        this.pause = pause;
        this.isPause = isPause;
    }

    @Override
    public void run() {
        int counter = 0;
        //To give some time for the producer to work
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("Reporter thread is down\n");
        }
        System.out.println("Reporter Start listening to results\n");
        while(!decryptionTasks.isEmpty() && taskNum.get() != 0){
            isPaused();
            if(!answers.isEmpty()){
                if(counter == 0){
                    System.out.println("Reporter Enter first loop\n");
                    counter =1;
                }
                try {
                    Runnable update = answers.take();
                    update.run();
                } catch (InterruptedException e) {
                    System.out.println("Reporter Failed to update Ui\n");
                    System.out.println(e.getStackTrace());
                }
            }
        }
        while (agents.getActiveCount() != 0){
            isPaused();
            if(counter <= 1){
                System.out.println("Reporter Enter second loop\n");
                counter = 2;
            }
            if(!answers.isEmpty()){
                try{
                    Runnable update = answers.take();
                    update.run();
                }catch (InterruptedException e){
                    System.out.println("Reporter Failed to update Ui\n");
                    System.out.println(e.getStackTrace());
                }
            }
        }
        agents.shutdown();
        while (!answers.isEmpty()){
            isPaused();
            if(counter<= 2){
                System.out.println("Reporter Enter second loop\n");
                counter = 3;
            }

            try{
                Runnable update = answers.take();
                update.run();
            }catch (InterruptedException e){
                System.out.println("Reporter Failed to update Ui\n");
                System.out.println(e.getStackTrace());
            }
        }
        System.out.println("Reporter Finish listening to results\n");
    }

    private void isPaused(){
        while (isPause.get()){
            synchronized (pause){
                try {
                    this.wait(1000);
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted");
                }
            }
        }
        notifyAll();
    }
}
