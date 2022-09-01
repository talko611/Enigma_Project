package Engine.engineAnswers;

public class MachineDetailsAnswer {
    private String usedVsAvailableRotors;
    private int possibleRotorsNum;
    private int usedRotorNum;
    private int numOfReflectors;
    private int numOfProcessedMessages;
    private String initialConfiguration;
    private String currentState;
    private boolean isMachineConfig;

    public void setUsedVsAvailableRotors(String usedVsAvailableRotors) {
        this.usedVsAvailableRotors = usedVsAvailableRotors;
    }

    public void setNumOfReflectors(int numOfReflectors) {
        this.numOfReflectors = numOfReflectors;
    }

    public void setNumOfProcessedMessages(int numOfProcessedMessages) {
        this.numOfProcessedMessages = numOfProcessedMessages;
    }

    public void setInitialConfiguration(String initialConfiguration) {
        this.initialConfiguration = initialConfiguration;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public void setMachineConfig(boolean machineConfig) {
        isMachineConfig = machineConfig;
    }

    public String getUsedVsAvailableRotors() {
        return usedVsAvailableRotors;
    }

    public int getNumOfReflectors() {
        return numOfReflectors;
    }

    public int getNumOfProcessedMessages() {
        return numOfProcessedMessages;
    }

    public String getInitialConfiguration() {
        return initialConfiguration;
    }

    public String getCurrentState() {
        return currentState;
    }

    public boolean isMachineConfig() {
        return isMachineConfig;
    }

    public int getPossibleRotorsNum() {
        return possibleRotorsNum;
    }

    public int getUsedRotorNum() {
        return usedRotorNum;
    }

    public void setPossibleRotorsNum(int possibleRotorsNum) {
        this.possibleRotorsNum = possibleRotorsNum;
    }

    public void setUsedRotorNum(int usedRotorNum) {
        this.usedRotorNum = usedRotorNum;
    }
}
