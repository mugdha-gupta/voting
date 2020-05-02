import java.io.Serializable;

public class YieldMessage implements Serializable {
    RequestMessage requestMessage;

    public YieldMessage(RequestMessage requestMessage) {
        this.requestMessage = requestMessage;
    }
}
