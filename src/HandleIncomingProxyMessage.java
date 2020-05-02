
import java.io.IOException;

public class HandleIncomingProxyMessage implements  Runnable {

    Object message;
    int localId;
    boolean isServerConnection;

    public HandleIncomingProxyMessage(Object message, int localId, boolean isServerConnection) {
        this.message = message;
        this.localId = localId;
        this.isServerConnection = isServerConnection;
    }

    @Override
    public void run() {
        if(message instanceof  WaitMessage){
            WaitMessage mess = (WaitMessage) message;
            try {
                Proxy.sendServerToClientMessage(mess);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //reply message from server to client
        if(message instanceof  ReplyMessage){

            ReplyMessage mess= (ReplyMessage) message;
            try {
                Proxy.sendServerToClientMessage(mess);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //failed to get process vote from server to client
        if(message instanceof FailedMessage){
            FailedMessage mess = (FailedMessage) message;
            try {
                Proxy.sendServerToClientMessage(mess);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //asking lower prioirity client if they can change their vote
        if(message instanceof InquireMessage){
            InquireMessage mess = (InquireMessage) message;
            try {
                Proxy.sendServerToClientMessage(mess);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //request message from client to server
        if(message instanceof RequestMessage){
            RequestMessage requestMessage = (RequestMessage) message;
            boolean success;
            try {
                success = Proxy.sendClientRequestToServerMessage(requestMessage);
                if(!success) {
                    //TODO:
                    //Proxy.clientConnections.get(requestMessage.clientId).sendMessage(
                            //new CantReachServerErrorMessage(requestMessage));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //yield from client to the server
        if(message instanceof  YieldMessage){
            YieldMessage yieldMessage = (YieldMessage) message;
            try {
                Proxy.sendClientYieldToServerMessage(yieldMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if(message instanceof CommitMessage){
            CommitMessage commitMessage = (CommitMessage) message;
            try {
                Proxy.sendClientCommitToServerMessage(commitMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(message instanceof  DoneMessage){
            DoneMessage doneMessage = (DoneMessage) message;
            try {
                Proxy.sendServerToClientMessage(doneMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(message instanceof ReleaseMessage){
            ReleaseMessage releaseMessage = (ReleaseMessage) message;
            try {
                Proxy.sendClientReleaseToServerMessage(releaseMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(message instanceof AcknowledgementMessage){
            System.out.println("received ack from " + localId);
            Proxy.partitionReceived.countDown();
        }

    }
}
