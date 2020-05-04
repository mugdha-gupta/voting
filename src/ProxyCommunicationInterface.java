import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Proxy class
 * Runnable that conrols all network traffic betwen servers and clients
 */
public class ProxyCommunicationInterface implements Runnable {
    boolean isServerConnection;
    int id;
    ObjectInputStream in;
    ObjectOutputStream out;
    Socket socket;

    public ProxyCommunicationInterface(boolean isServerConnection, int id) throws IOException {
        this.isServerConnection = isServerConnection;
        this.id = id;
        socket = Util.getProxySideSocket(isServerConnection? Util.SERVER_PROXY_LISTENING_PORT : Util.CLIENT_PROXY_LISTENING_PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    //send various messages
    synchronized  public void sendMessage(DoneMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized  public void sendMessage(FinishedMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized  public void sendMessage(ReadMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized  public void sendMessage(PartitionMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized  public void sendMessage(WaitMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized  public void sendMessage(FileContentsMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized  public void sendMessage(ReleaseMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized  public void sendMessage(CommitMessage commitMessage) throws IOException {
        out.writeObject(commitMessage);
    }
    synchronized public void sendMessage(FailedMessage failedMessage) throws IOException {
        out.writeObject(failedMessage);
    }
    synchronized public void sendMessage(ReplyMessage myMessage) throws IOException {
        out.writeObject(myMessage);
    }
    synchronized public void sendMessage(InquireMessage inquireMessage) throws IOException {
        out.writeObject(inquireMessage);
    }
    synchronized public void sendMessage(RequestMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized public void sendMessage(YieldMessage message) throws IOException {
        out.writeObject(message);
    }

    //we will listen for incoming messages when this runnable is executed
    @Override
    public void run() {

        //we don't want to create too many threads so restict the thread pool for message handling
        ExecutorService pool = Executors.newFixedThreadPool(5);
        Object message;
        while(true){
            try {
                message = in.readObject();
                if( message == null)
                    continue;

                pool.execute(new HandleIncomingProxyMessage(message, id, isServerConnection));
            } catch (IOException | ClassNotFoundException e) {
                continue;
            }
        }
    }

}
