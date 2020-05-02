import java.io.Serializable;

public class CommitMessage implements Serializable {
    int clientId;
    int requestNum;
    int serverId;
    int fileId;

    public CommitMessage(int clientId, int requestNum, int serverId, int fileId) {
        this.clientId = clientId;
        this.requestNum = requestNum;
        this.serverId = serverId;
        this.fileId = fileId;
    }


}
