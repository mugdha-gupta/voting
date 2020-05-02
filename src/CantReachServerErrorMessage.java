import java.io.Serializable;

public class CantReachServerErrorMessage implements Serializable {
    public RequestMessage requestMessage;

    public CantReachServerErrorMessage(RequestMessage requestMessage) {
        this.requestMessage = requestMessage;
    }

}
