package Engine.DM.ReportTask;

import javafx.application.Platform;
import javafx.util.Pair;

import java.util.function.BiConsumer;


public class ReportTask implements Runnable {

    private String configuration;
    private String agentNum;
    private String decryption;

    BiConsumer<String, Pair<String, String>> update;

    public ReportTask(String decryption, String configuration, String agentNum, BiConsumer<String, Pair<String, String>> update){
        this.decryption = decryption;
        this.agentNum = agentNum;
        this.configuration = configuration;
        this.update = update;
    }
    @Override
    public void run() {
        System.out.println("Report task reporting to Ui");
        System.out.println("Decryption " + decryption);
        System.out.println("Agent " + agentNum);
        System.out.println("Configuration " + configuration +"\n");
        Platform.runLater(()->update.accept(decryption, new Pair<>(configuration, agentNum)));
    }
}
