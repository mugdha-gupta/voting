import java.io.Serializable;

public class FailedMessage implements Serializable {
    int serverId;
    RequestMessage requestMessage;

    public FailedMessage(int serverId, RequestMessage requestMessage) {
        this.serverId = serverId;
        this.requestMessage = requestMessage;
    }
}
