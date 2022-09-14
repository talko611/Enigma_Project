package GraphicPresentor.Screens.thirdScreen.bruteForceDashBoardComponent;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class TimerTask extends Task<Void> {

    private DoubleProperty progress;
    private Consumer<String> setTime;
    private SimpleBooleanProperty isPause;

    public TimerTask(DoubleProperty progress , Consumer<String> setTime, SimpleBooleanProperty isPaused){
        this.progress = progress;
        this.setTime = setTime;
        this.isPause = isPaused;
    }
    @Override
    protected Void call() throws Exception {
        int  timer = 0;
        while(progress.getValue() != 1){
            int finalTimer = timer;
            Platform.runLater(()->setTime.accept(String.valueOf(finalTimer) + " Sec"));
            if(!isPause.get()){
                ++timer;
            }
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                System.out.println(Thread.currentThread().getName() + " has interrupted");
                break;
            }
            if (Thread.interrupted()){
                System.out.println(Thread.currentThread().getName() + " has interrupted");
                break;
            }
        }
        return null;
    }

}
