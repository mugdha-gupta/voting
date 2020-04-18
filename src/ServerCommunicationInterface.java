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
public class ServerCommunicationInterface implements Runnable {
    Server server;
    ObjectInputStream in;
    ObjectOutputStream out;
    Socket socket;

    public ServerCommunicationInterface(Server server) throws IOException {
        this.server = server;
        socket = Util.getProxyServerSocket();
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    //we will listen for incoming messages when this runnable is executed
    @Override
    public void run() {

        //we don't want to create too many threads so restict the thread pool for message handling
        ExecutorService pool = Executors.newFixedThreadPool(10);
        int message;
        while(true){
            try {
                message = in.read();
                System.out.println("server " + server.serverId + " received " + message);
                out.write(server.serverId);

            } catch (IOException e) {
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
