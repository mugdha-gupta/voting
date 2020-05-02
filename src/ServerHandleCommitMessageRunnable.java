import java.io.IOException;

public class ServerHandleCommitMessageRunnable implements Runnable {
    Server server;
    CommitMessage message;

    public ServerHandleCommitMessageRunnable(Server server, CommitMessage message) {
        this.server = server;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            server.commit(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            server.communicationInterface.sendMessage(new DoneMessage(message.clientId, message.serverId, message.fileId, message.requestNum));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
