import java.io.Serializable;

public class ReadMessage implements Serializable {
    int clientId;
    int serverId;
    int fileId;

    public ReadMessage(int clientId, int serverId, int fileId) {
        this.clientId = clientId;
        this.serverId = serverId;
        this.fileId = fileId;
    }
}
