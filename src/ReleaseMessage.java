import java.io.Serializable;

public class ReleaseMessage implements Serializable {
    int clientId;
    int serverId;
    int fileId;
    int requestNum;

    public ReleaseMessage(int clientId, int serverId, int fileId, int requestNum) {
        this.clientId = clientId;
        this.serverId = serverId;
        this.fileId = fileId;
        this.requestNum = requestNum;
    }

}
