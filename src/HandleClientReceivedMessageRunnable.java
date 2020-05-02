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
            return;
        }

        if(returnMessage instanceof FailedMessage){
            FailedMessage message = (FailedMessage)returnMessage;
            System.out.println("received failed from server "+ message.serverId);
            if(requestNum > message.requestMessage.requestNum)
                return;
            client.incrementFailsReceived();
            client.addReply(message.requestMessage.serverId);
            return;
        }

        if(returnMessage instanceof InquireMessage){
            InquireMessage message = (InquireMessage)returnMessage;
            System.out.println("received inquire from server "+ message.serverId);
            if(message.requestMessage == null)
                return;
            if(requestNum > message.requestMessage.requestNum)
                return;

            client.addInquireMessage(message);
            try {
                client.handleInquires();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(returnMessage instanceof WaitMessage){
            WaitMessage message = (WaitMessage) returnMessage;
            if(requestNum > message.requestNum)
                return;
            client.addReply(message.serverId);
            return;
        }
        if(returnMessage instanceof DoneMessage){
            DoneMessage message = (DoneMessage)returnMessage;
            if(requestNum > message.requestNum)
                return;
            client.done.countDown();
            return;
        }

        if(returnMessage instanceof PartitionMessage){
            client.partition();
            return;
        }



    }

}
