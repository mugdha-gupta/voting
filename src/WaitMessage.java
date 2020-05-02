import java.io.Serializable;

public class WaitMessage implements Serializable {
    int clientId;
    int serverId;
    int requestNum;
    int fileId;

    public WaitMessage(int clientId, int serverId, int requestNum, int fileId) {
        this.clientId = clientId;
        this.serverId = serverId;
        this.requestNum = requestNum;
        this.fileId = fileId;
    }
}
