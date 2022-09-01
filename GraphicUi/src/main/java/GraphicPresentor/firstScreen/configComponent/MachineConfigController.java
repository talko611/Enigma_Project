package GraphicPresentor.firstScreen.configComponent;

import Engine.Engine;
import Engine.engineAnswers.MachineDetailsAnswer;
import Engine.enigmaParts.EnigmaParts;
import Engine.enums.ReflectorGreekNums;
import GraphicPresentor.GraphicPresentor;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javafx.beans.binding.Bindings.when;

public class MachineConfigController {
    @FXML private ChoiceBox<Integer> rotorIdCh;
    @FXML private Button addRotorBt;
    @FXML private Button addPlugBt;
    @FXML private Label isMachineConfigureLb;
    @FXML private ChoiceBox<String> rotorOffsetCh;
    @FXML private ChoiceBox<String> plugChoice1;
    @FXML private ChoiceBox<String> plugChoice2;
    @FXML private ChoiceBox<String> reflectorCh;
    @FXML private Label plugErrorMs;

    private SimpleBooleanProperty isAllRotorUsed;
    private SimpleBooleanProperty isAllPlugsUsed;

    private SimpleBooleanProperty isConfig;

    private Engine engine;
    private StringBuilder rotorsConfigLine;
    private StringBuilder offsetConfigLine;
    private StringBuilder plugBordConfigLine;

    public MachineConfigController(){
        rotorsConfigLine = new StringBuilder();
        offsetConfigLine = new StringBuilder();
        plugBordConfigLine = new StringBuilder();
        isAllRotorUsed = new SimpleBooleanProperty(false);
        isAllPlugsUsed = new SimpleBooleanProperty(false);
    }

    @FXML
    void initialize(){
        addRotorBt.disableProperty().bind(isAllRotorUsed);
        addPlugBt.disableProperty().bind(isAllPlugsUsed);
    }
    @FXML
    void addPlug(ActionEvent event) {
        String val1 = plugChoice1.getValue();
        String val2 = plugChoice2.getValue();
        if(!val1.equals(val2)){
            plugErrorMs.visibleProperty().set(false);
            plugBordConfigLine.append(val1).append(val2);
            plugChoice1.getItems().remove(val1);
            plugChoice1.getItems().remove(val2);
            plugChoice2.getItems().remove(val1);
            plugChoice2.getItems().remove(val2);
            isAllPlugsUsed.set(plugChoice1.getItems().size() == 0);
        }
        else {
            plugErrorMs.visibleProperty().set(true);
        }
    }

    @FXML
    void addRotor(ActionEvent event) {
        rotorsConfigLine.append(rotorIdCh.getValue()).append(" ");
        rotorIdCh.getItems().remove(rotorIdCh.getValue());
        offsetConfigLine.append(rotorOffsetCh.getValue()).append(" ");
        isAllRotorUsed.set(rotorIdCh.getItems().size() == 0);
    }

    @FXML
    void autoConfig(ActionEvent event) {

    }

    @FXML
    void manualConfig(ActionEvent event) {
            this.engine.manualConfigRotors(rotorsConfigLine.toString());
            this.engine.manualConfigOffsets(offsetConfigLine.toString());
            this.engine.manualConfigReflector(String.valueOf(Arrays.stream(ReflectorGreekNums.values())
                    .filter((i) -> i.getSymbol().equals(reflectorCh.getValue()))
                    .collect(Collectors.toList())
                    .get(0).getVal()));
            this.engine.manualConfigPlugBoard(plugBordConfigLine.toString());

    }

    public void setEngine(Engine engine){
        this.engine = engine;
    }

    public void initData(){
        EnigmaParts enigmaParts = this.engine.getEnigmaParts();
        IntStream.range(1 , enigmaParts.getRotorCount() + 1).forEach(i -> rotorIdCh.getItems().add(i));
        enigmaParts.getReflectors().forEach((num, reflector) -> reflectorCh.getItems().add(reflector.getId()));
        enigmaParts.getKeyboard().getABC().forEach((str, entry)->{
            plugChoice1.getItems().add(str);
            plugChoice2.getItems().add(str);
            rotorOffsetCh.getItems().add(str);
        });
//        rotorIdCh.setValue(rotorIdCh.getItems().get(0));
//        rotorOffsetCh.setValue(rotorOffsetCh.getItems().get(0));
//        reflectorCh.setValue(reflectorCh.getItems().get(0));
//        plugChoice1.setValue(plugChoice1.getItems().get(0));
//        plugChoice2.setValue(plugChoice2.getItems().get(1));
    }

    public void connectBinding(SimpleBooleanProperty isConfig){
        this.isConfig = isConfig;
        isMachineConfigureLb.visibleProperty().bind(isConfig);
    }

}
