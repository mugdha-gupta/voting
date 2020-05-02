public class ReleaseMessage {
    int clientId;
    int serverId;
    int fileId;

    public ReleaseMessage(int clientId, int serverId, int fileId) {
        this.clientId = clientId;
        this.serverId = serverId;
        this.fileId = fileId;
    }
}
