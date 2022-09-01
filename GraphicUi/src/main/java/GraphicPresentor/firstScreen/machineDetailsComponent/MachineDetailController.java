package GraphicPresentor.firstScreen.machineDetailsComponent;

import GraphicPresentor.MainController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
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

    public void bindConfigurations(SimpleStringProperty initConfiguration, SimpleStringProperty currentConfiguration , SimpleBooleanProperty isLoaded){
        this.initConfigurationAns.textProperty().bind(initConfiguration);
        this.currConfigurationAns.textProperty().bind(currentConfiguration);
        this.machineNotLoadedLb.visibleProperty().bind(isLoaded.not());
    }

    public void updateState(String usedVsAvailRotors, String reflectorNum, String processedMessages){
        this.usedVsAvailAns.setText(usedVsAvailRotors);
        this.reflectorNumAns.setText(reflectorNum);
        this.processedMessagesAns.setText(processedMessages);
    }
}
