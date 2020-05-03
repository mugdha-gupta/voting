import java.io.IOException;

public class ServerHandleReleaseMessageRunnable implements Runnable {
    Server server;
    ReleaseMessage message;

    public ServerHandleReleaseMessageRunnable(Server server, ReleaseMessage message) {
        this.server = server;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            server.release(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
