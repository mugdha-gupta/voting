
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


    synchronized void sendRequest(RequestMessage requestMessage) throws IOException {
        out.writeObject(requestMessage);
        System.out.println("sent request");
    }

    synchronized void sendRelease(ReleaseMessage message) throws IOException {
        out.writeObject(message);
        System.out.println("sent release");
    }

    synchronized  void sendMessage(YieldMessage yieldMessage) throws  IOException {
        out.writeObject(yieldMessage);
        System.out.println("send yield");
    }

    synchronized void sendCommitMessage(CommitMessage commitMessage) throws IOException{
        out.writeObject(commitMessage);
        System.out.println("send commit");
    }

    //close streams
    void clean() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(5);

        Object object;
        while (true){
            try {
                object = in.readObject();
                System.out.println("reading messages");
                if(object == null)
                    continue;
                System.out.println("received a message and calling runnalbe");
                pool.execute(new HandleClientReceivedMessageRunnable(client, object));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

}
