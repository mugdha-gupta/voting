import java.io.Serializable;

public class MyMessage implements Serializable {
    public int id;
    public int recipientId;
    public String message;

    public MyMessage(int id, int recipientId, String message) {
        this.id = id;
        this.recipientId = recipientId;
        this.message = message;
    }


}
