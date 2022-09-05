package GraphicPresentor.Screens.firstScreen.configComponent;

import Engine.*;
import Engine.engineAnswers.MachineDetailsAnswer;
import GraphicPresentor.*;
import Engine.enigmaParts.EnigmaParts;
import Engine.enums.ReflectorGreekNums;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.stream.Collectors;


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
    @FXML private Label rotorLb;


    private Engine engine;
    private UiAdapter uiAdapter;
    private StringBuilder rotorsConfigLine;
    private StringBuilder offsetConfigLine;
    private StringBuilder plugBordConfigLine;
    private int rotorCount;
    private int minimalRotorNum;


    public MachineConfigController(){
        rotorsConfigLine = new StringBuilder();
        offsetConfigLine = new StringBuilder();
        plugBordConfigLine = new StringBuilder();
    }

    public void setUiAdapter(UiAdapter uiAdapter){
        this.uiAdapter = uiAdapter;
    }

    public void setEngine(Engine engine){
        this.engine = engine;
    }

    @FXML
    void addPlug(ActionEvent event) {
        String val1 = plugChoice1.getValue();
        String val2 = plugChoice2.getValue();
        if(val1 == null || val2 == null){
            plugErrorMs.setText("Cannot cannot plug to itself");
        }
        else if(!val1.equals(val2)){
            plugErrorMs.visibleProperty().set(false);
            plugBordConfigLine.append(val1).append(val2);
            plugChoice1.getItems().remove(val1);
            plugChoice1.getItems().remove(val2);
            plugChoice2.getItems().remove(val1);
            plugChoice2.getItems().remove(val2);
            plugErrorMs.setText("");
        }else{
            plugErrorMs.setText("Cannot plug the same letter");
        }
    }

    @FXML
    void addRotor(ActionEvent event) {
        if(rotorIdCh.getValue() != null && rotorOffsetCh.getValue() != null){
            rotorsConfigLine.append(rotorIdCh.getValue()).append(" ");
            rotorIdCh.getItems().remove(rotorIdCh.getValue());
            offsetConfigLine.append(rotorOffsetCh.getValue());
            ++rotorCount;
            rotorLb.setText("");
        }
        else{
            rotorLb.setText("Cannot add rotor without offset");
        }
        rotorOffsetCh.setValue(null);
        rotorIdCh.setValue(null);
    }

    @FXML
    void autoConfig(ActionEvent event) {
        this.engine.autoConfig();
        uiAdapter.setIsConfig(true);
        uiAdapter.updateMachineDetails();
        isMachineConfigureLb.setText("Machine configure successfully");
        initData();//Ready component to next configuration
    }

    @FXML
    void manualConfig(ActionEvent event) {
        if(rotorCount >= minimalRotorNum && rotorCount < 99 && reflectorCh.getValue() != null){
            this.engine.manualConfigRotors(rotorsConfigLine.toString());
            this.engine.manualConfigOffsets(offsetConfigLine.toString());
            this.engine.manualConfigReflector(String.valueOf(Arrays.stream(ReflectorGreekNums.values())
                    .filter((i) -> i.getSymbol().equals(reflectorCh.getValue()))
                    .collect(Collectors.toList())
                    .get(0).getVal()));
            this.engine.manualConfigPlugBoard(plugBordConfigLine.toString());
            uiAdapter.setIsConfig(true);
            uiAdapter.updateMachineDetails();
            isMachineConfigureLb.setText("Machine configure successfully");
            initData();//Ready component to next configuration
        }else {
            isMachineConfigureLb.setText("Number of rotors add is either less "+ minimalRotorNum + " or more then 99");
        }
    }

    public void initData(){
        EnigmaParts enigmaParts = this.engine.getEnigmaParts();
        minimalRotorNum = enigmaParts.getRotorCount();
        rotorIdCh.getItems().remove(0, rotorIdCh.getItems().size());
        enigmaParts.getRotors().forEach((id,rotor) -> {rotorIdCh.getItems().add(id);});
        reflectorCh.getItems().remove(0, reflectorCh.getItems().size());
        enigmaParts.getReflectors().forEach((num, reflector) -> reflectorCh.getItems().add(reflector.getId()));
        plugChoice1.getItems().remove(0, plugChoice1.getItems().size());
        plugChoice2.getItems().remove(0, plugChoice2.getItems().size());
        rotorOffsetCh.getItems().remove(0, rotorOffsetCh.getItems().size());
        enigmaParts.getKeyboard().getABC().forEach((str, entry)->{
            plugChoice1.getItems().add(str);
            plugChoice2.getItems().add(str);
            rotorOffsetCh.getItems().add(str);
        });
        rotorsConfigLine.setLength(0);
        plugBordConfigLine.setLength(0);
        rotorIdCh.setValue(null);
        rotorOffsetCh.setValue(null);
        reflectorCh.setValue(null);
        rotorCount = 0;
        rotorLb.setText("Rotors will be inserted from right to left");
    }

    public void setIsMachineConfigureText(String text) {
        this.isMachineConfigureLb.setText(text);
    }
}
