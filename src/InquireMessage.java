import java.io.Serializable;

public class InquireMessage implements Serializable {
    int serverId;
    int clientReplied;
    RequestMessage requestMessage;

    public InquireMessage(int serverId, int clientReplied, RequestMessage requestMessage) {
        this.serverId = serverId;
        this.clientReplied = clientReplied;
        requestMessage = requestMessage;
    }
}
