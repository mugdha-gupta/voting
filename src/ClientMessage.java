import java.io.Serializable;

public class ClientMessage implements Serializable {
    public int id;
    public int recipientId;
    public String message;

    public ClientMessage(int id, int recipientId, String message) {
        this.id = id;
        this.recipientId = recipientId;
        this.message = message;
    }


}
