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

    public void sendMessage(GenericMessage message) throws IOException {
        out.writeObject(message);
    }

    public void sendMessage(Object message) throws IOException {
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

    //close streams
    void clean() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

}
