package GraphicPresentor.Screens.thirdScreen.bruteForceDashBoardComponent;

import Engine.Engine;
import Engine.engineAnswers.DmAnswer;
import Engine.engineAnswers.DmInitAnswer;
import Engine.enums.DmTaskDifficulty;
import GraphicPresentor.UiAdapter;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DashboardController {

    @FXML private Label numOfAgentsLb;
    @FXML private Slider numOfAgentsSlider;
    @FXML private ComboBox<DmTaskDifficulty> difficultyCb;
    @FXML private TextField taskSizeTb;
    @FXML private Label numOfTasksLb;
    @FXML private Label timeLb;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressBarLb;
    @FXML private Label userLabel;
    @FXML  private TableView<UiBruteForceResults> candidates;
    @FXML private TableColumn<?, ?> decryptionCol;
    @FXML private TableColumn<?, ?> configurationCol;
    @FXML private TableColumn<?, ?> foundByCol;
    @FXML private Button startBt;
    @FXML private Button pauseBt;
    @FXML private Button resumeBt;
    @FXML private Button stopBt;
    @FXML private Button setBt;
    @FXML private Button restBt;



//    boolean isSet;
    private UiAdapter uiAdapter;
    private Engine engine;
    private ObservableList<UiBruteForceResults> data;
    private long totalTasks;
    private SimpleBooleanProperty isPaused;
    private Thread timer;
    private SimpleBooleanProperty isSet;
    private SimpleBooleanProperty isStopped;
    private SimpleBooleanProperty isStarted;


    @FXML
    void initialize(){
        numOfAgentsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {numOfAgentsLb.setText(String.valueOf(newValue.intValue()));});
        difficultyCb.getItems().addAll(DmTaskDifficulty.values());
        decryptionCol.setCellValueFactory(new PropertyValueFactory<>("decryption"));
        configurationCol.setCellValueFactory(new PropertyValueFactory<>("configuration"));
        foundByCol.setCellValueFactory(new PropertyValueFactory<>("foundBy"));
        isPaused = new SimpleBooleanProperty(false);
        isStopped = new SimpleBooleanProperty(false);
        isSet = new SimpleBooleanProperty(false);
        isStarted = new SimpleBooleanProperty(false);
        setBt.disableProperty().bind(isSet);
        startBt.disableProperty().bind(isSet.not().or(isStarted).or(isStopped).or(numOfTasksLb.textProperty().isEqualTo("0")));
        pauseBt.disableProperty().bind(isSet.not().or(isPaused).or(isStopped).or(numOfTasksLb.textProperty().isEqualTo("0")));
        resumeBt.disableProperty().bind(isSet.not().or(isPaused.not()).or(isStopped).or(numOfTasksLb.textProperty().isEqualTo("0")));
        stopBt.disableProperty().bind(isStarted.not().or(isStopped).or(numOfTasksLb.textProperty().isEqualTo("0")));
        numOfTasksLb.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("0")){
                restBt.disableProperty().set(false);
            }
        });
    }

    public void setUiAdapter(UiAdapter uiAdapter){
        this.uiAdapter = uiAdapter;
        uiAdapter.isBruteForceEncryptProcessSuccessProperty().addListener((observable, oldValue, newValue) -> {
            if(!oldValue && newValue){
                init();
            }
        });
    }

    public void  setEngine(Engine engine){
        this.engine = engine;
    }

    public void init(){
        DmAnswer answer = engine.getDmDetails();
        numOfAgentsSlider.setMax(answer.getMaxAgents());
        numOfAgentsSlider.setMin(1);
        numOfAgentsSlider.setValue(1);
        difficultyCb.setAccessibleText("Difficulty Level");
        userLabel.setText("");
        numOfTasksLb.setText("");
        taskSizeTb.setText("");
        timeLb.setText("");
        progressBarLb.setText("");
        progressBar.setProgress(0);
        isSet.set(false);
        isStopped.set(false);
        isPaused.set(false);
        isStarted.set(false);
        data = FXCollections.observableArrayList();
        candidates.setItems(data);
        restBt.disableProperty().set(true);
    }

    @FXML
    void pauseButtonClicked(ActionEvent event) {
        userLabel.setText("Process paused");
        isPaused.set(true);
        engine.pauseBruteForce();
    }

    @FXML
    void resumeButtonClicked(ActionEvent event) {
        userLabel.setText("Process running");
        isPaused.set(false);
        engine.resumeBruteForce();
    }

    @FXML
    void startButtonClicked(ActionEvent event) {
        isStarted.set(true);
        userLabel.setText("Process running");
        BiConsumer<String, Pair<String,String>> tableUpdate = (str, pair)->{
            data.add(new UiBruteForceResults(str, pair.getKey(), pair.getValue()));
            System.out.println("JAT entering result to table\n");
        };
        Consumer<Integer> progressUpdate = (taskCompleted)->{
            double progress = (double) Math.round(taskCompleted/(double)totalTasks * 1000) / 1000;
            progressBar.setProgress(progress);
            numOfTasksLb.setText(String.valueOf(totalTasks - taskCompleted));
            progressBarLb.setText(String.format("%.2f",progress * 100) + "%");
        };
        timer = new Thread(new TimerTask(progressBar.progressProperty(), timeLb::setText, isPaused),"Timer");
        timer.start();
        engine.startBruteForce(tableUpdate , progressUpdate ,isPaused);
    }

    @FXML
    void stopButtonClicked(ActionEvent event) {
        engine.abortBruteForce();
        timer.interrupt();
        userLabel.setText("Process ended\nClick reset to set process on same message or encrypt different message on the previous tab ");
        isStopped.set(true);
        restBt.disableProperty().set(false);
    }

    @FXML
    void setButtonClicked(ActionEvent event) {
        try {
            if(difficultyCb.getValue() == null){
                userLabel.setText("Please choose difficulty level");
                return;
            }
            DmInitAnswer answer = engine.initializeDm(difficultyCb.getValue(),
                    uiAdapter.bruteForceEncryptedMessageProperty().get(),
                    (int) Math.floor(numOfAgentsSlider.getValue()),
                    Integer.parseInt(taskSizeTb.getText()));
            userLabel.setText(answer.getMessage());
            isSet.set(answer.getSuccess());
            timeLb.setText("0 Sec");
            progressBarLb.setText("0%");
            numOfTasksLb.setText(String.valueOf(answer.getNumOfTask()));
            totalTasks = answer.getNumOfTask();
        } catch (NumberFormatException e) {
            userLabel.setText("please enter only whole number in task size box with max of 10 digits max");
        }

    }
    @FXML
    void resetButtonClicked(ActionEvent event){
        init();
    }

    public static class UiBruteForceResults{
        private SimpleStringProperty decryption;
        private SimpleStringProperty configuration;
        private SimpleStringProperty foundBy;

        public UiBruteForceResults(String decryption, String configuration, String foundBy) {
            this.decryption = new SimpleStringProperty(decryption);
            this.configuration = new SimpleStringProperty(configuration);
            this.foundBy = new SimpleStringProperty(foundBy);
        }

        public void setDecryption(String decryption) {
            this.decryption.set(decryption);
        }

        public void setConfiguration(String configuration) {
            this.configuration.set(configuration);
        }

        public void setFoundBy(String foundBy) {
            this.foundBy.set(foundBy);
        }

        public String getDecryption() {
            return decryption.get();
        }

        public SimpleStringProperty decryptionProperty() {
            return decryption;
        }

        public String getConfiguration() {
            return configuration.get();
        }

        public SimpleStringProperty configurationProperty() {
            return configuration;
        }

        public String getFoundBy() {
            return foundBy.get();
        }

        public SimpleStringProperty foundByProperty() {
            return foundBy;
        }
    }

}
