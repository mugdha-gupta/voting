import java.io.Serializable;

public class FinishedMessage implements Serializable {
    int clientId;
    public FinishedMessage(int clientId) {
        this.clientId = clientId;
    }
}
