import java.io.IOException;

public class ServerHandleYieldMessageRunnable implements Runnable {
    Server server;
    YieldMessage message;

    public ServerHandleYieldMessageRunnable(Server server, YieldMessage message) {
        this.server = server;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            server.receiveYield(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
