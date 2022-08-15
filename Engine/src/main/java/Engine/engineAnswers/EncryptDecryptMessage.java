package Engine.engineAnswers;


import java.io.Serializable;

public class EncryptDecryptMessage implements Cloneable, Serializable {
    private  Boolean success;
    private String src;
    private String out;
    private Long duration;
    private String error;

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getSrc() {
        return src;
    }

    public String getOut() {
        return out;
    }

    public Long getDuration() {
        return duration;
    }

    public String getError() {
        return error;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
