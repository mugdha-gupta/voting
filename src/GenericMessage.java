import java.io.Serializable;

public class GenericMessage implements Serializable {
    public int id;

    GenericMessage(int id){
        this.id = id;
    }
}
