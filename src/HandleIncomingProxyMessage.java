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

        if(message instanceof MyMessage){
            if(isServerConnection) {
                try {
                    System.out.println("reached handle runnable");
                    Proxy.sendServerToServerMessage(((MyMessage) message).id, ((MyMessage) message).recipientId, (MyMessage) message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    Proxy.sendClientToServerMessage(((MyMessage) message).id, ((MyMessage) message).recipientId,(MyMessage) message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(message instanceof ClientMessage){
            try {
                Proxy.sendServerToClientMessage(((ClientMessage) message).id, ((ClientMessage) message).recipientId, (ClientMessage) message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
