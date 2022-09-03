package GraphicPresentor;

import Engine.*;
import Engine.engineAnswers.InputOperationAnswer;
import Engine.engineAnswers.MachineDetailsAnswer;
import GraphicPresentor.firstScreen.configComponent.MachineConfigController;
import GraphicPresentor.firstScreen.currentConfiguration.CurrentConfigurationController;
import GraphicPresentor.firstScreen.machineDetailsComponent.MachineDetailController;
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
    @FXML private ScrollPane machineDetailsComponent;
    @FXML private MachineDetailController machineDetailsComponentController;
    @FXML private ScrollPane machineConfigureComponent;
    @FXML private MachineConfigController machineConfigureComponentController;
    @FXML private ScrollPane currentConfigurationTabComponent;

    @FXML private CurrentConfigurationController currentConfigurationTabComponentController;




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

        machineDetailsComponentController.setUiAdapter(uiAdapter);
        machineDetailsComponentController.bind();

        machineConfigureComponentController.setEngine(engine);
        machineConfigureComponentController.setUiAdapter(uiAdapter);

        currentConfigurationTabComponentController.setUiAdapter(uiAdapter);

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
        uiAdapter.isLoadedProperty().set(answer.isSuccess());
        loadFileAnswerLabel.setText(answer.getMessage());
        if(answer.isSuccess()){
            uiAdapter.isConfigProperty().set(false);
            uiAdapter.updateMachineDetails();
        }
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

}
