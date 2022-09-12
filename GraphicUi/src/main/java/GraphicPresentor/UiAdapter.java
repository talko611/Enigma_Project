package GraphicPresentor;

import Engine.Engine;
import Engine.engineAnswers.MachineDetailsAnswer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class UiAdapter {
    private SimpleBooleanProperty isLoaded;
    private SimpleBooleanProperty isConfig;
    private SimpleStringProperty initialConfiguration;
    private SimpleStringProperty currentConfiguration;
    private SimpleStringProperty usedVsPossibleRotors;
    private SimpleStringProperty processedMessagesNum;
    private SimpleStringProperty reflectorsNum;
    private SimpleStringProperty bruteForceEncryptedMessage;
    private SimpleBooleanProperty isBruteForceEncryptProcessSuccess;

    private Engine engine;


    public UiAdapter(Engine engine){
        isLoaded = new SimpleBooleanProperty(false);
        isConfig = new SimpleBooleanProperty(false);
        initialConfiguration = new SimpleStringProperty("Unavailable");
        currentConfiguration = new SimpleStringProperty("Unavailable");
        usedVsPossibleRotors = new SimpleStringProperty();
        processedMessagesNum = new SimpleStringProperty();
        reflectorsNum = new SimpleStringProperty();
        bruteForceEncryptedMessage = new SimpleStringProperty(null);
        isBruteForceEncryptProcessSuccess = new SimpleBooleanProperty(false);
        this.engine = engine;
    }

    public SimpleStringProperty bruteForceEncryptedMessageProperty() {
        return bruteForceEncryptedMessage;
    }

    public SimpleStringProperty reflectorsNumProperty() {
        return reflectorsNum;
    }

    public void setReflectorsNum(String reflectorsNum) {
        this.reflectorsNum.set(reflectorsNum);
    }

    public void setProcessedMessagesNum(String processedMessagesNum) {
        this.processedMessagesNum.set(processedMessagesNum);
    }

    public SimpleStringProperty usedVsPossibleRotorsProperty() {
        return usedVsPossibleRotors;
    }

    public SimpleStringProperty processedMessagesNumProperty() {
        return processedMessagesNum;
    }

    public void setIsLoaded(boolean isLoaded) {
        this.isLoaded.set(isLoaded);
    }

    public void setIsConfig(boolean isConfig) {
        this.isConfig.set(isConfig);
    }

    public void setCurrentConfiguration(String currentConfiguration) {
        this.currentConfiguration.set(currentConfiguration);
    }

    public SimpleBooleanProperty isLoadedProperty() {
        return isLoaded;
    }

    public SimpleBooleanProperty isConfigProperty() {
        return isConfig;
    }

    public SimpleStringProperty initialConfigurationProperty() {
        return initialConfiguration;
    }

    public String getCurrentConfiguration() {
        return currentConfiguration.get();
    }

    public SimpleStringProperty currentConfigurationProperty() {
        return currentConfiguration;
    }

    public SimpleBooleanProperty isBruteForceEncryptProcessSuccessProperty() {
        return isBruteForceEncryptProcessSuccess;
    }
    public void setIsBruteForceEncryptProcessSuccess(boolean isBruteForceEncryptProcessSuccess) {
        this.isBruteForceEncryptProcessSuccess.set(isBruteForceEncryptProcessSuccess);
    }

    public void updateMachineDetails(){
        MachineDetailsAnswer answer = engine.getMachineDetails();
        currentConfiguration.set(answer.getCurrentState() == null ? "Machine is config yet" : answer.getCurrentState());
        initialConfiguration.set(answer.getInitialConfiguration() == null ? "Machine is not config yet" : answer.getInitialConfiguration());
        reflectorsNum.set(String.valueOf(answer.getNumOfReflectors()));
        processedMessagesNum.set(String.valueOf(answer.getNumOfProcessedMessages()));
        usedVsPossibleRotors.set(answer.getUsedRotorNum() + "/" + answer.getPossibleRotorsNum());
    }
}
