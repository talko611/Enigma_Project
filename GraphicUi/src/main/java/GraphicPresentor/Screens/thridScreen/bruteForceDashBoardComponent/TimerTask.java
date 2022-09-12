package GraphicPresentor.Screens.thridScreen.bruteForceDashBoardComponent;

import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class TimerTask extends Task<Void> {

    private DoubleProperty progress;
    private Consumer<String> setTime;

    public TimerTask(DoubleProperty progress , Consumer<String> setTime){
        this.progress = progress;
        this.setTime = setTime;
    }
    @Override
    protected Void call() throws Exception {
        int  timer = 0;
        while(progress.getValue() != 1){
            int finalTimer = timer;
            Platform.runLater(()->setTime.accept(String.valueOf(finalTimer) + " Sec"));
            ++timer;
            Thread.sleep(1000);
        }
        return null;
    }
}
