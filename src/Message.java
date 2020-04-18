import java.io.Serializable;

public class Message implements Serializable {
    public int id;

    Message(int id){
        this.id = id;
    }
}
