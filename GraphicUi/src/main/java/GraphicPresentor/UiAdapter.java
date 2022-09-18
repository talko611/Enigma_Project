package GraphicPresentor;

import Engine.Engine;
import Engine.engineAnswers.MachineDetailsAnswer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class UiAdapter {
    private final SimpleBooleanProperty isLoaded;
    private final SimpleBooleanProperty isConfig;
    private final SimpleStringProperty initialConfiguration;
    private final SimpleStringProperty currentConfiguration;
    private final SimpleStringProperty usedVsPossibleRotors;
    private final SimpleStringProperty processedMessagesNum;
    private final SimpleStringProperty reflectorsNum;
    private final SimpleStringProperty bruteForceEncryptedMessage;
    private final SimpleBooleanProperty isBruteForceEncryptProcessSuccess;

    private final Engine engine;


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


    public void updateMachineDetails(){
        MachineDetailsAnswer answer = engine.getMachineDetails();
        currentConfiguration.set(answer.getCurrentState() == null ? "Machine is config yet" : answer.getCurrentState());
        initialConfiguration.set(answer.getInitialConfiguration() == null ? "Machine is not config yet" : answer.getInitialConfiguration());
        reflectorsNum.set(String.valueOf(answer.getNumOfReflectors()));
        processedMessagesNum.set(String.valueOf(answer.getNumOfProcessedMessages()));
        usedVsPossibleRotors.set(answer.getUsedRotorNum() + "/" + answer.getPossibleRotorsNum());
    }
}
