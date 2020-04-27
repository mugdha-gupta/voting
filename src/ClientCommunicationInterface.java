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
    ObjectOutputStream out;
    Socket socket;

    public ClientCommunicationInterface(Client client) throws IOException {
        this.client = client;
        socket = Util.getProxyClientSocket();
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    void sendMessage(GenericMessage message) throws IOException {
        out.writeObject(message);
    }

    void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }

    void sendMessage(Object message) throws IOException {
        out.writeObject(message);
    }
    //we will listen for incoming messages when this runnable is executed
    @Override
    public void run() {

        try {
            sendMessage(new GenericMessage(client.clientId));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object message;
        while(true){
            try {
                message = in.readObject();
                if(message == null)
                    continue;

                if(message instanceof  GenericMessage && ((GenericMessage) message).id == -1){
                    sendToAll();
                }


            } catch (IOException | ClassNotFoundException e) {
                continue;
            }
        }
    }

    private void sendToAll() throws IOException {
        for(int i = 1; i <= 7 ; i++){
            sendMessage(new Message(client.clientId, i, "client " + client.clientId + "'s message to server " +
                    " " + i + " was successful"));
        }
    }

    //close streams
    void clean() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

}
