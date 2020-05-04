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

    //send various messages
    synchronized void sendMessage(ReplyMessage replyMessage) throws IOException {
        out.writeObject(replyMessage);
    }

    synchronized void sendMessage(WaitMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized void sendMessage(DoneMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized void sendMessage(FileContentsMessage message) throws IOException {
        out.writeObject(message);
    }
    synchronized void sendMessage(FailedMessage failedMessage) throws IOException {
        out.writeObject(failedMessage);
    }

    synchronized void sendMessage(InquireMessage inquireMessage) throws IOException {
        out.writeObject(inquireMessage);
    }
    //we will listen for incoming messages when this runnable is executed
    @Override
    public void run() {

        ExecutorService serverHandleIncomingMessages = Executors.newFixedThreadPool(5);

        Object message;
        long start = System.currentTimeMillis();
        while((System.currentTimeMillis() - start) < Util.TIMEOUT_THRESHOLD){
            try {
                message = in.readObject();
                if(message == null)
                    continue;

                start = System.currentTimeMillis();
                if(message instanceof RequestMessage){
                    serverHandleIncomingMessages.execute(new ServerHandleRequestMessageRunnable(server, (RequestMessage)message));
                }

                if(message instanceof YieldMessage){
                    serverHandleIncomingMessages.execute(new ServerHandleYieldMessageRunnable(server, (YieldMessage)message));
                }

                if(message instanceof CommitMessage){
                    serverHandleIncomingMessages.execute(new ServerHandleCommitMessageRunnable(server, (CommitMessage)message));
                }

                if(message instanceof ReleaseMessage){
                    serverHandleIncomingMessages.execute(new ServerHandleReleaseMessageRunnable(server, (ReleaseMessage)message));
                }

                if(message instanceof PartitionMessage){
                    server.cleanup(-1);
                }

                if(message instanceof FinishedMessage){
                    server.finishClient(((FinishedMessage) message).clientId);
                }

                if(message instanceof ReadMessage){
                    server.returnFileContents((ReadMessage)message);
                }

            } catch (IOException | ClassNotFoundException e) {
                continue;
            }
        }

        server.shutdown();
    }



}
