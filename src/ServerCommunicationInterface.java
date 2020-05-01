import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

    synchronized void sendMessage(MyMessage myMessage) throws IOException {
        out.writeObject(myMessage);
    }
    synchronized void sendMessage(ClientMessage myMessage) throws IOException {
        out.writeObject(myMessage);
    }
    //we will listen for incoming messages when this runnable is executed
    @Override
    public void run() {

        try {
            sendToAllClients();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object message;
        while(true){
            try {
                message = in.readObject();
                if(message == null)
                    continue;

                if(message instanceof MyMessage)
                    System.out.println(((MyMessage) message).message);

            } catch (IOException | ClassNotFoundException e) {
                continue;
            }
        }
    }

    private void sendToAllServers() throws IOException {
        for(int i = 1; i <= Util.NUM_SERVERS ; i++){
            if(i == server.serverId)
                continue;
            sendMessage(new MyMessage(server.serverId, i, "server " + server.serverId ));

        }
    }

    private void sendToAllClients() throws IOException {
        for(int i = 1; i <= Util.NUM_CLIENTS ; i++){
            if(i == server.serverId)
                continue;
            sendMessage(new ClientMessage(server.serverId, i, "server " + server.serverId ));

        }
    }

    //close streams
    void clean() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

}
