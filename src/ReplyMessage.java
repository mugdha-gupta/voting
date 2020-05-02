import java.io.Serializable;

public class ReplyMessage implements Serializable {
    int serverId;
    int clientId;
    RequestMessage requestMessage;

    public ReplyMessage(int serverId, int clientId, RequestMessage requestMessage) {
        this.serverId = serverId;
        this.clientId = clientId;
        this.requestMessage = requestMessage;
    }
}
