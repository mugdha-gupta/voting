import java.io.IOException;

public class HandleClientReceivedMessageRunnable implements Runnable {
    Client client;
    Object returnMessage;

    public HandleClientReceivedMessageRunnable(Client client, Object message) {
        this.client = client;
        this.returnMessage = message;
    }

    @Override
    public void run() {
        if(returnMessage == null)
            System.out.println("error: received null message in reply to request");
        int requestNum = client.getRequestNum();

        if(returnMessage instanceof ReplyMessage){
            ReplyMessage message = (ReplyMessage) returnMessage;
            System.out.println("received reply from server " + message.serverId);
            if(requestNum > message.requestMessage.requestNum)
                return;
            client.incrementVotesReceived(message.serverId);
            client.addReply(message.requestMessage.serverId);
        }

        if(returnMessage instanceof FailedMessage){
            FailedMessage message = (FailedMessage)returnMessage;
            System.out.println("received failed from server "+ message.serverId);
            if(requestNum > message.requestMessage.requestNum)
                return;
            client.incrementFailsReceived();
            client.addReply(message.requestMessage.serverId);
        }

        if(returnMessage instanceof InquireMessage){
            InquireMessage message = (InquireMessage)returnMessage;
            System.out.println("received inquire from server "+ message.serverId);
            if(message.requestMessage == null)
                return;
            if(requestNum > message.requestMessage.requestNum)
                return;
            client.addInquireMessage(message);
        }

        if(returnMessage instanceof WaitMessage){
            WaitMessage message = (WaitMessage) returnMessage;
            if(requestNum > message.requestNum)
                return;
            client.addReply(message.serverId);
        }
        if(returnMessage instanceof DoneMessage){
            DoneMessage message = (DoneMessage)returnMessage;
            System.out.println("received done from server "+ message.serverId);
            if(requestNum > message.requestNum)
                return;
            client.done.countDown();
        }

        if(client.getNumResponded() >= 3){
            try {
                client.handleInquires();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(client.votesReceived >= 2){
            try {
                client.enterCS();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
