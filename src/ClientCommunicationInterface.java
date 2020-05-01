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


    synchronized void sendMessage(MyMessage myMessage) throws IOException {
        out.writeObject(myMessage);
    }

    //we will listen for incoming messages when this runnable is executed
    @Override
    public void run() {

        Object message;
        while(true){
            try {
                message = in.readObject();
                if(message == null)
                    continue;

                if(message instanceof ClientMessage)
                    System.out.println(((ClientMessage) message).message);

            } catch (IOException | ClassNotFoundException e) {
                continue;
            }
        }
    }

    private void sendToAll() throws IOException {
        for(int i = 1; i <= 7 ; i++){
            sendMessage(new MyMessage(client.clientId, i, "hello world"));
        }
    }

    //close streams
    void clean() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

}
