package GraphicPresentor.Screens.thirdScreen.bruteForceDashBoardComponent;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class TimerTask extends Task<Void> {

    private final DoubleProperty progress;
    private final Consumer<String> setTime;
    private final SimpleBooleanProperty isPause;

    public TimerTask(DoubleProperty progress , Consumer<String> setTime, SimpleBooleanProperty isPaused){
        this.progress = progress;
        this.setTime = setTime;
        this.isPause = isPaused;
    }

    @Override
    protected Void call() {
        int  timer = 0;
        while(progress.getValue() != 1){
            int finalTimer = timer;
            Platform.runLater(()->setTime.accept(finalTimer + " Sec"));
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
