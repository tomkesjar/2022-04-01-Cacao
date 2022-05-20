package common.messages;

import java.io.Serializable;

public class ActionMessage implements Serializable {
    private String message;

    public ActionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ActionMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
