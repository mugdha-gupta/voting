import java.io.Serializable;

public class Message implements Serializable {
    public int id;
    public int recipientId;
    public String message;

    public Message(int id, int recipientId, String message) {
        this.id = id;
        this.recipientId = recipientId;
        this.message = message;
    }


}
