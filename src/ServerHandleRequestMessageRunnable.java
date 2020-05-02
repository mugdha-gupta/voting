import java.io.IOException;

public class ServerHandleRequestMessageRunnable implements  Runnable {
    Server server;
    RequestMessage requestMessage;

    public ServerHandleRequestMessageRunnable(Server server, RequestMessage requestMessage) {
        this.server = server;
        this.requestMessage = requestMessage;
    }

    @Override
    public void run() {

        System.out.println("server " + server.serverId + " received request from client " + requestMessage.clientId );
        try {
            server.receiveRequest(requestMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
