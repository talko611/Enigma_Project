package GraphicPresentor.firstScreen.machineDetailsComponent;

import Engine.Engine;
import Engine.engineAnswers.MachineDetailsAnswer;
import GraphicPresentor.UiAdapter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MachineDetailController {
    @FXML private Label usedVsAvailHd;
    @FXML private Label usedVsAvailAns;
    @FXML private Label reflectorNumHd;
    @FXML private Label reflectorNumAns;
    @FXML private Label processedMessagesHd;
    @FXML private Label processedMessagesAns;
    @FXML private Label initConfigurationHd;
    @FXML private Label initConfigurationAns;
    @FXML private Label currConfigurationHd;
    @FXML private Label currConfigurationAns;
    @FXML private Label machineNotLoadedLb;

    private UiAdapter uiAdapter;

    public void bind(){
        initConfigurationAns.textProperty().bind(uiAdapter.initialConfigurationProperty());
        currConfigurationAns.textProperty().bind(uiAdapter.currentConfigurationProperty());
        machineNotLoadedLb.visibleProperty().bind(uiAdapter.isLoadedProperty().not());
        usedVsAvailAns.textProperty().bind(uiAdapter.usedVsPossibleRotorsProperty());
        processedMessagesAns.textProperty().bind(uiAdapter.processedMessagesNumProperty());
        reflectorNumAns.textProperty().bind(uiAdapter.reflectorsNumProperty());
    }

    public void setUiAdapter(UiAdapter uiAdapter){
        this.uiAdapter = uiAdapter;
    }
}
