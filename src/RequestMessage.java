import java.io.Serializable;

public class RequestMessage implements Serializable, Comparable {
    public int clientId;
    public int objectToEditId;
    public int serverId;
    public int requestNum;
    public String message;

    public RequestMessage(int clientId, int objectToEditId, int serverId, int requestNum, String message) {
        this.clientId = clientId;
        this.objectToEditId = objectToEditId;
        this.serverId = serverId;
        this.requestNum = requestNum;
        this.message = message;
    }
    @Override
    public int compareTo(Object o) {
        RequestMessage rm = (RequestMessage) o;
        if(this.clientId < rm.clientId)
            return -1;
        else if (this.clientId > rm.clientId)
            return 1;
        else
            return 0;
    }
}
