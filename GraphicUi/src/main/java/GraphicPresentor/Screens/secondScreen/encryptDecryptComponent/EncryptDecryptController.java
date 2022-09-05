package GraphicPresentor.Screens.secondScreen.encryptDecryptComponent;


import Engine.Engine;
import Engine.engineAnswers.EncryptDecryptMessage;
import Engine.engineAnswers.MachineDetailsAnswer;
import GraphicPresentor.UiAdapter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

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

    private Engine engine;
    private UiAdapter uiAdapter;
    private long duration = 0;


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
            EncryptDecryptMessage answer = engine.encryptDecryptMessage(srcTextField.getText(), true);
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
            EncryptDecryptMessage message = engine.encryptDecryptMessage(event.getCharacter(), false);
            if(message.getSuccess()){
                encryptedTextField.setText(encryptedTextField.getText() == null ? "" : encryptedTextField.getText() + message.getOut());
                duration += message.getDuration();
            }
            uiAdapter.setCurrentConfiguration(engine.getMachineDetails().getCurrentState());
        }
    }
    @FXML
    void clickedRb(ActionEvent event){
        clearButtonClicked(event);
    }
}


