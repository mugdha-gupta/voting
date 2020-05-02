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
            if(requestNum > message.requestMessage.requestNum)
                return;
            client.incrementVotesReceived(message.serverId);
            client.addReply(message.requestMessage.serverId);
        }

        if(returnMessage instanceof FailedMessage){
            FailedMessage message = (FailedMessage)returnMessage;
            if(requestNum > message.requestMessage.requestNum)
                return;
            client.incrementFailsReceived();
            client.addReply(message.requestMessage.serverId);
        }

        if(returnMessage instanceof InquireMessage){
            InquireMessage message = (InquireMessage)returnMessage;
            if(requestNum > message.requestMessage.requestNum)
                return;
            client.addInquireMessage(message);
        }

        if(returnMessage instanceof DoneMessage){
            DoneMessage message = (DoneMessage)returnMessage;
            if(requestNum > message.requestNum)
                return;
            client.done(message);
        }

        if(client.getNumResponded() == 3){
            try {
                client.handleInquires();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            client.enterCS();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
