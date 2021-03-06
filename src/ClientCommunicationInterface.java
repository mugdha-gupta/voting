
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * MyServerSocket class
 * Runnable that holds all information required to send messages and receive from the server
 * after it receives a message, it creates a new thread to handle it
 */
public class ClientCommunicationInterface implements Runnable {
    Client client;
    ObjectInputStream in;
    public ObjectOutputStream out;
    Socket socket;

    public ClientCommunicationInterface(Client client) throws IOException {
        this.client = client;
        socket = Util.getProxyClientSocket();
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    //send different types of messages
    synchronized void sendRequest(RequestMessage requestMessage) throws IOException {
        out.writeObject(requestMessage);
    }

    synchronized void sendRelease(ReleaseMessage message) throws IOException {
        out.writeObject(message);
    }

    synchronized  void sendMessage(YieldMessage yieldMessage) throws  IOException {
        out.writeObject(yieldMessage);
    }
    synchronized  void sendMessage(ReadMessage yieldMessage) throws  IOException {
        out.writeObject(yieldMessage);
    }
    synchronized  void sendMessage(FinishedMessage message) throws  IOException {
        System.out.println("finish sent from " + message.clientId);
        out.writeObject(message);
    }
    synchronized void sendCommitMessage(CommitMessage commitMessage) throws IOException{
        out.writeObject(commitMessage);
    }
    synchronized void sendAcknowledgement(AcknowledgementMessage message) throws IOException{
        out.writeObject(message);
    }

    //close streams
    void clean() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

    //wait for incoming messages
    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(5);

        Object object;
        while (true){
            try {
                object = in.readObject();
                if(object == null)
                    continue;
                pool.execute(new HandleClientReceivedMessageRunnable(client, object));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

}
