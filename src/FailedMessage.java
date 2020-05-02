public class FailedMessage {
    int serverId;
    RequestMessage requestMessage;

    public FailedMessage(int serverId, RequestMessage requestMessage) {
        this.serverId = serverId;
        this.requestMessage = requestMessage;
    }
}
