package GraphicPresentor.firstScreen.configComponent;

import Engine.Engine;
import GraphicPresentor.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class machineConfigController {
    @FXML private ChoiceBox<?> rotorIdCh;
    @FXML private Button addRotorBt;
    @FXML private Button addPlugBt;
    @FXML private Label isMachineConfigureLb;
    @FXML private ChoiceBox<?> rotorOffsetCh;
    @FXML private ChoiceBox<?> plugChoice1;
    @FXML private ChoiceBox<?> plugChoice2;
    @FXML private ChoiceBox<?> reflectorCh;

    private Engine engine;

    @FXML
    void addPlug(ActionEvent event) {

    }

    @FXML
    void addRotor(ActionEvent event) {

    }

    @FXML
    void autoConfig(ActionEvent event) {

    }

    @FXML
    void manualConfig(ActionEvent event) {

    }

    public void setEngine(Engine engine){
        this.engine = engine;
    }

}
