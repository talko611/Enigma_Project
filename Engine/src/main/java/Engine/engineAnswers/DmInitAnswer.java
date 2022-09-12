package Engine.engineAnswers;

public class DmInitAnswer {
    Boolean isSuccess;
    long numOfTask;
    String message;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public long getNumOfTask() {
        return numOfTask;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public void setNumOfTasks(long numOfTask) {
        this.numOfTask = numOfTask;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
