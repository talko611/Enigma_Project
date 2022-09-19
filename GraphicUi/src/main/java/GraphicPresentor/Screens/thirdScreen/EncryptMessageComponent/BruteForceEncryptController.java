package GraphicPresentor.Screens.thirdScreen.EncryptMessageComponent;

import Engine.Engine;
import Engine.engineAnswers.EncryptDecryptMessage;
import GraphicPresentor.Screens.thirdScreen.TrieDataSrtucture.Trie;
import GraphicPresentor.Screens.thirdScreen.bruteForceDashBoardComponent.DashboardController;
import GraphicPresentor.UiAdapter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.util.Set;


public class BruteForceEncryptController {

    @FXML private Label currentConfigurationLb;
    @FXML private TextField srcMessage;
    @FXML private TextField destMessage;
    @FXML private Label processSuccessMessage;
    @FXML private TextField dictionarySearchBox;
    @FXML private ListView<String> wordList;

    private Engine engine;
    private UiAdapter uiAdapter;
    private Trie availableWord;
    private DashboardController dashboardController;

    @FXML
    void initialize(){
        dictionarySearchBox.textProperty().addListener(((observable, oldValue, newValue) -> {
            wordList.getItems().clear();
            wordList.getItems().addAll(availableWord.getAllChildren(newValue.toLowerCase()));
        }));
        wordList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            srcMessage.setText((srcMessage.getText() + " " +newValue));
            srcMessage.setText(srcMessage.getText().trim());
        });
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setUiAdapter(UiAdapter uiAdapter) {
        this.uiAdapter = uiAdapter;
        destMessage.textProperty().bind(uiAdapter.bruteForceEncryptedMessageProperty());
        currentConfigurationLb.textProperty().bind(uiAdapter.currentConfigurationProperty());
    }

    public void getDecipherDetails(){
        availableWord = new Trie();
        Set<String> dictionary = engine.getDmDetails().getDictionary();
        dictionary.forEach(word -> availableWord.addWord(word.toLowerCase()));
        wordList.getItems().addAll(availableWord.getAllChildren(""));
    }

    public void initSuccessMessage(){
        processSuccessMessage.setText("");
    }

    @FXML
    void clearBtClicked(ActionEvent event) {
        srcMessage.setText("");
        uiAdapter.bruteForceEncryptedMessageProperty().set("");
        processSuccessMessage.setText("");
        uiAdapter.isBruteForceEncryptProcessSuccessProperty().set(false);
        dashboardController.init();
    }

    @FXML
    void processBtClicked(ActionEvent event) {
        if(!engine.getMachine().getPlugBord().getAllPlugs().isEmpty()){
            processSuccessMessage.setText("Brute Force does not support decrypting machine with plug bord\nplease remove plug board first");
        }else{
            EncryptDecryptMessage answer = this.engine.encryptDecryptMessage(srcMessage.getText(), true, true);
            if (answer.getSuccess()) {
                processSuccessMessage.setText("Encrypted Successfully! \nPlease move to next tab to activate brute force decryption");
                uiAdapter.bruteForceEncryptedMessageProperty().set(answer.getOut());
                uiAdapter.isBruteForceEncryptProcessSuccessProperty().set(true);
                uiAdapter.updateMachineDetails();
                dashboardController.init();
            } else {
                processSuccessMessage.setText(answer.getError());
                uiAdapter.isBruteForceEncryptProcessSuccessProperty().set(false);
            }
        }
    }

    @FXML
    void removePlugsBtClicked(ActionEvent event) {
        engine.removePlugsFromMachine();
        uiAdapter.updateMachineDetails();
    }

    @FXML
    void resetMachineBt(ActionEvent event) {
        engine.resetMachine();
        uiAdapter.updateMachineDetails();

    }

}
