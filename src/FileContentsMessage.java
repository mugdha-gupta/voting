import java.io.Serializable;

public class FileContentsMessage implements Serializable {
    int clientId;
    int serverId;
    String message;

    public FileContentsMessage(int clientId, int serverId, String message) {
        this.clientId = clientId;
        this.serverId = serverId;
        this.message = message;
    }
}
