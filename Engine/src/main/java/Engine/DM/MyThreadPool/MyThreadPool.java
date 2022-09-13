package Engine.DM.MyThreadPool;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadPool extends ThreadPoolExecutor {
    private Boolean isPaused;
    private ReentrantLock pauseLock;
    private Condition unPaused;

    public MyThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue, @NotNull ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        pauseLock = new ReentrantLock();
        unPaused = pauseLock.newCondition();
        isPaused = false;
    }

    protected void beforeExecute(Thread t, Runnable r){
        super.beforeExecute(t , r);
        pauseLock.lock();
        try{
            while (isPaused)
                unPaused.await();
        }catch (InterruptedException ie){
            t.interrupt();
        }finally {
            pauseLock.unlock();
        }
    }
    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unPaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }
}
