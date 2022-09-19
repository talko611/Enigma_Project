package GraphicPresentor;

import Engine.*;
import Engine.engineAnswers.InputOperationAnswer;
import GraphicPresentor.Screens.firstScreen.configComponent.MachineConfigController;
import GraphicPresentor.Screens.firstScreen.currentConfiguration.CurrentConfigurationController;
import GraphicPresentor.Screens.firstScreen.machineDetailsComponent.MachineDetailController;
import GraphicPresentor.Screens.secondScreen.encryptDecryptComponent.EncryptDecryptController;
import GraphicPresentor.Screens.secondScreen.statisticsComponent.StatisticsController;
import GraphicPresentor.Screens.thirdScreen.EncryptMessageComponent.BruteForceEncryptController;
import GraphicPresentor.Screens.thirdScreen.bruteForceDashBoardComponent.DashboardController;
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
    @FXML private Tab currentConfigTab;
    @FXML private Tab encryptDecryptTab;
    @FXML private Tab bruteForceTab;
    @FXML private Tab dashboardTab;
    @FXML private ScrollPane machineDetailsComponent;
    @FXML private MachineDetailController machineDetailsComponentController;
    @FXML private ScrollPane machineConfigureComponent;
    @FXML private MachineConfigController machineConfigureComponentController;
    @FXML private ScrollPane currentConfigurationTabComponent;
    @FXML private CurrentConfigurationController currentConfigurationTabComponentController;
    @FXML private ScrollPane encryptDecryptComponent;
    @FXML private EncryptDecryptController encryptDecryptComponentController;
    @FXML private ScrollPane statisticsComponent;
    @FXML private StatisticsController statisticsComponentController;
    @FXML private ScrollPane bruteEncryptComponent;
    @FXML private BruteForceEncryptController bruteEncryptComponentController;
    @FXML private ScrollPane bruteForceDashboardComponent;
    @FXML private DashboardController bruteForceDashboardComponentController;




    private Engine engine;
    private UiAdapter uiAdapter;
    private Stage primaryStage;


    public MainController(){
        this.engine = new EngineImp();
        uiAdapter = new UiAdapter(engine);
    }
    @FXML
    void initialize(){
        machineTab.disableProperty().bind(uiAdapter.isLoadedProperty().not());
        encryptDecryptTab.disableProperty().bind(uiAdapter.isConfigProperty().not());
        bruteForceTab.disableProperty().bind(uiAdapter.isConfigProperty().not());
        currentConfigTab.disableProperty().bind(uiAdapter.isConfigProperty().not());
        dashboardTab.disableProperty().bind(uiAdapter.isBruteForceEncryptProcessSuccessProperty().not());


        machineDetailsComponentController.setUiAdapter(uiAdapter);
        machineDetailsComponentController.bind();

        machineConfigureComponentController.setEngine(engine);
        machineConfigureComponentController.setUiAdapter(uiAdapter);

        currentConfigurationTabComponentController.setUiAdapter(uiAdapter);

        encryptDecryptComponentController.setEngine(engine);
        encryptDecryptComponentController.setUi(uiAdapter);

        statisticsComponentController.setEngine(engine);

        bruteEncryptComponentController.setEngine(engine);
        bruteEncryptComponentController.setUiAdapter(uiAdapter);
        bruteEncryptComponentController.setDashboardController(bruteForceDashboardComponentController);
        uiAdapter.isConfigProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                bruteEncryptComponentController.getDecipherDetails();
            }
        });


        bruteForceDashboardComponentController.setUiAdapter(uiAdapter);
        bruteForceDashboardComponentController.setEngine(engine);
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
        uiAdapter.setIsLoaded(answer.isSuccess());
        loadFileAnswerLabel.setText(answer.getMessage());
        if(answer.isSuccess()){
            uiAdapter.updateMachineDetails();
            encryptDecryptComponentController.loadKeyboard();
        }
        uiAdapter.isConfigProperty().set(false);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML void configTabClick(){
        machineConfigureComponentController.setIsMachineConfigureText("");
        machineConfigureComponentController.initData();
    }

    @FXML void currentConfigTabClicked(){
        currentConfigurationTabComponentController.getCurrentConfiguration();
    }

    @FXML
    void statisticsTabClicked(){
        statisticsComponentController.setStatistics();
    }

    @FXML
    void bruteEncryptTabClicked(){
        bruteEncryptComponentController.initSuccessMessage();
    }

    @FXML
    void machineTabClicked(){
        if(uiAdapter.isLoadedProperty().get()){
            machineConfigureComponentController.setIsMachineConfigureText("");
            machineConfigureComponentController.initData();
            if(uiAdapter.isConfigProperty().get()){
                currentConfigurationTabComponentController.getCurrentConfiguration();
            }
        }
    }

    @FXML
    void encryptTabClicked(){
        if(uiAdapter.isConfigProperty().get()){
            statisticsComponentController.setStatistics();
        }
    }

}
