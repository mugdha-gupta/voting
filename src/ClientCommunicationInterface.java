import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

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
    }

    synchronized void sendRelease(ReleaseMessage message) throws IOException {
        out.writeObject(message);
    }

    synchronized  void sendMessage(YieldMessage yieldMessage) throws  IOException {
        out.writeObject(yieldMessage);
    }

    synchronized void sendCommitMessage(CommitMessage commitMessage) throws IOException{
        out.writeObject(commitMessage);
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

        Object object = null;
        while (true){
            try {
                object = in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(object == null)
                continue;

            pool.execute(new HandleClientReceivedMessageRunnable(client, object));

        }
    }

}
