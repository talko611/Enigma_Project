package GraphicPresentor.Screens.secondScreen.encryptDecryptComponent;


import Engine.Engine;
import Engine.engineAnswers.EncryptDecryptMessage;
import GraphicPresentor.Screens.secondScreen.keyboardButtons.KeyboardButtonController;
import GraphicPresentor.UiAdapter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import machine.parts.keyboard.Keyboard;
import org.checkerframework.checker.units.qual.K;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EncryptDecryptController {


    @FXML private Label currentConfigLb;
    @FXML private TextField srcTextField;
    @FXML private TextField encryptedTextField;
    @FXML private RadioButton enterMessageRb;
    @FXML private ToggleGroup encryptMode;
    @FXML private RadioButton typeRb;
    @FXML private Button processBt;
    @FXML private Button clearBt;
    @FXML private Button restBt;
    @FXML private Button doneBt;
    @FXML private FlowPane lightKeyBoard;
    @FXML private FlowPane realKeyboard;

    private Engine engine;
    private UiAdapter uiAdapter;
    private long duration = 0;

    Map<String, TilePane> lightsKeyboardMapping;


    public void setEngine(Engine engine){ this.engine = engine;}

    public void setUi(UiAdapter uiAdapter){
        this.uiAdapter = uiAdapter;
        bindToUiAdapter();
    }

    void bindToUiAdapter(){this.currentConfigLb.textProperty().bind(uiAdapter.currentConfigurationProperty());}

    @FXML
    void initialize(){
        processBt.disableProperty().bind(typeRb.selectedProperty());
        clearBt.disableProperty().bind(typeRb.selectedProperty());
        doneBt.disableProperty().bind(enterMessageRb.selectedProperty());
    }

    @FXML
    void clearButtonClicked(ActionEvent event) {
        srcTextField.setText("");
        encryptedTextField.setText("");
    }

    @FXML
    void doneButtonClicked(ActionEvent event) {
        EncryptDecryptMessage message = new EncryptDecryptMessage();
        message.setSrc(srcTextField.getText());
        message.setOut(encryptedTextField.getText());
        message.setDuration(duration);
        try {
            engine.saveEncryptionData(message);
        }catch (CloneNotSupportedException e){
            System.out.println("Couldn't sava message(" + message.getSrc() +") statistics");
        }
        duration = 0;
        uiAdapter.setProcessedMessagesNum(String.valueOf(engine.getMachineDetails().getNumOfProcessedMessages()));
        srcTextField.setText("");
        encryptedTextField.setText("");
    }

    @FXML
    void processButtonClicked(ActionEvent event)  {
        if (srcTextField.getText().length() != 0) {
            EncryptDecryptMessage answer = engine.encryptDecryptMessage(srcTextField.getText(), true,false);
            encryptedTextField.setText(answer.getSuccess() ? answer.getOut() : answer.getError());
            uiAdapter.setProcessedMessagesNum(String.valueOf(engine.getMachineDetails().getNumOfProcessedMessages()));
            uiAdapter.setCurrentConfiguration(engine.getMachineDetails().getCurrentState());
        }
    }

    @FXML
    void restButtonClicked(ActionEvent event) {
        this.engine.resetMachine();
        uiAdapter.setCurrentConfiguration(engine.getMachineDetails().getCurrentState());
        encryptedTextField.setText("");
        srcTextField.setText("");
    }

    @FXML
    void typedFromKeyboard(KeyEvent event){
        if(typeRb.isSelected()){
            EncryptDecryptMessage message = engine.encryptDecryptMessage(event.getText(), false, false);
            if(message.getSuccess()){
                encryptedTextField.setText(encryptedTextField.getText() == null ? "" : encryptedTextField.getText() + message.getOut());
                duration += message.getDuration();
                turnOnLightButton(message.getOut());
            }
            uiAdapter.setCurrentConfiguration(engine.getMachineDetails().getCurrentState());
        }
    }

    @FXML
    void typeStopped(KeyEvent event){
        lightKeyBoard.getChildren().forEach(button -> {
            button.setStyle("-fx-background-color: #434346");
            button.setStyle("-fx-border-color : White");
        });
    }

    @FXML
    void clickedRb(ActionEvent event){
        clearButtonClicked(event);
    }

    public void loadKeyboard()  {
        if(uiAdapter.isLoadedProperty().get()){
            realKeyboard.getChildren().clear();
            lightKeyBoard.getChildren().clear();
            URL urlReal = getClass().getSuperclass().getResource("/ComponentsFXML/Second_Screen/keyboardButtonComponent/keyBoardButtonLayout.fxml");
            URL urlLight = getClass().getSuperclass().getResource("/ComponentsFXML/Second_Screen/keyboardButtonComponent/lightKeyboardButton.fxml");
            Keyboard keyboard = engine.getMachine().getKeyboard();
            lightsKeyboardMapping = new HashMap<>(keyboard.getABC().size());
            keyboard.getABC().keySet().forEach(i -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(urlReal);
                    TilePane realButton = fxmlLoader.load();
                    KeyboardButtonController currentController = fxmlLoader.getController();
                    currentController.setIsTypeStateSelected(typeRb.selectedProperty());
                    currentController.connectToParentComponentEvent(srcTextField);
                    Label current = (Label) realButton.getChildren().get(0);
                    current.setText(i);
                    realKeyboard.getChildren().add(realButton);

                    fxmlLoader = new FXMLLoader(urlLight);
                    TilePane lightButton = fxmlLoader.load();
                    lightButton.setStyle("-fx-background-color: #434346");
                    lightButton.setStyle("-fx-border-color: White");
                    lightsKeyboardMapping.put(i, lightButton);
                    current = (Label) lightButton.getChildren().get(0);
                    current.setText(i);
                    lightKeyBoard.getChildren().add(lightButton);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }
            });
        }
    }

    private void turnOnLightButton(String letter){
        if(lightsKeyboardMapping.containsKey(letter)){
            TilePane button = lightsKeyboardMapping.get(letter.toUpperCase());
            button.setStyle("-fx-background-color: Yellow");
        }
    }
}


