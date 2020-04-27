import java.io.IOException;

public class HandleIncomingProxyMessage implements  Runnable {

    Object message;
    int localId;
    boolean isServerConnection;

    public HandleIncomingProxyMessage(Object message, int localId, boolean isServerConnection) {
        this.message = message;
        this.localId = localId;
        this.isServerConnection = isServerConnection;
    }

    @Override
    public void run() {
        if(message instanceof GenericMessage){
            String type = isServerConnection? "server" : "client";
            System.out.println(type + " " + localId + " " + " returned " + ((GenericMessage) message).id);
        }

        else if(message instanceof  Message){
            if(isServerConnection) {
                try {
                    Proxy.sendServerToServerMessage(((Message) message).id, ((Message) message).recipientId, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    Proxy.sendClientToServerMessage(((Message) message).id, ((Message) message).recipientId, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
