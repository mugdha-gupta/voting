public class DoneMessage {
    int clientId;
    int serverId;
    int fileId;
    int requestNum;

    public DoneMessage(int clientId, int serverId, int fileId, int requestNum) {
        this.clientId = clientId;
        this.serverId = serverId;
        this.fileId = fileId;
        this.requestNum = requestNum;
    }
}
