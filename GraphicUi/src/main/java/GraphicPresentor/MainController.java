package GraphicPresentor;

import Engine.*;
import Engine.engineAnswers.InputOperationAnswer;
import Engine.engineAnswers.MachineDetailsAnswer;
import GraphicPresentor.firstScreen.configComponent.MachineConfigController;
import GraphicPresentor.firstScreen.machineDetailsComponent.MachineDetailController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class MainController {


    @FXML private ComboBox<?> changeSkinButton;
    @FXML private Button LoadFileButton;
    @FXML private Label loadFileAnswerLabel;
    @FXML private Tab machineTab;
    @FXML private Tab machineDetailsTab;
    @FXML private Tab configTab;
    @FXML private Tab currentConfigurationTab;
    @FXML private Tab encryptDecryptTab;
    @FXML private Tab bruteForceTab;
    @FXML private ScrollPane machineDetailsComponent;
    @FXML private MachineDetailController machineDetailsComponentController;
    @FXML private ScrollPane machineConfigureComponent;

    @FXML private MachineConfigController machineConfigureComponentController;

    private SimpleBooleanProperty isFileLoaded;
    private SimpleBooleanProperty isConfigMachine;
    private SimpleStringProperty initConfig;
    private SimpleStringProperty currConfig;

    private Engine engine;
    private Stage primaryStage;


    public MainController(){
        this.engine = new EngineImp();
        this.isFileLoaded = new SimpleBooleanProperty(false);
        this.isConfigMachine = new SimpleBooleanProperty(false);
        this.initConfig = new SimpleStringProperty(" Unavailable");
        this.currConfig = new SimpleStringProperty(" Unavailable");
    }
    @FXML
    void initialize(){
        this.machineTab.disableProperty().bind(isFileLoaded.not());
        this.encryptDecryptTab.disableProperty().bind(isConfigMachine.not());
        this.bruteForceTab.disableProperty().bind(isConfigMachine.not());
        this.machineDetailsComponentController.bindConfigurations(initConfig, currConfig,isFileLoaded);
        this.machineConfigureComponentController.setEngine(engine);
        this.machineConfigureComponentController.connectBinding(isConfigMachine);
        this.isFileLoaded.addListener(((observable, oldValue, newValue) -> {
            if(newValue) {
                setMachineDetails();
                machineConfigureComponentController.initData();
                isConfigMachine.set(false);
            }
        }));

        //Set combo box items
    }

    @FXML void changeSkin(ActionEvent event) {

    }

    @FXML void loadFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to load..");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        String absolutePath = selectedFile.getAbsolutePath();
        InputOperationAnswer answer = this.engine.loadFromFile(absolutePath);
        this.isFileLoaded.set(answer.isSuccess());
        this.loadFileAnswerLabel.setText(answer.getMessage());
    }

    public void setMachineDetails(){
        MachineDetailsAnswer answer = this.engine.getMachineDetails();
        initConfig.set(answer.getInitialConfiguration() == null ? "Machine is not config yet" : answer.getInitialConfiguration());
        currConfig.set(answer.getCurrentState() == null ? "Machine is not config yet" : answer.getCurrentState());
        this.machineDetailsComponentController.updateState(answer.getUsedRotorNum() + "/" + answer.getPossibleRotorsNum(), String.valueOf(answer.getNumOfReflectors()), String.valueOf(answer.getNumOfProcessedMessages()));
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

}
