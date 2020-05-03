import java.io.File;
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
            try {
                client.addReply(message.requestMessage.serverId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if(returnMessage instanceof FailedMessage){
            FailedMessage message = (FailedMessage)returnMessage;
            if(requestNum > message.requestMessage.requestNum)
                return;
            client.incrementFailsReceived();
            try {
                client.addReply(message.requestMessage.serverId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if(returnMessage instanceof InquireMessage){
            InquireMessage message = (InquireMessage)returnMessage;
            if(message.requestMessage == null)
                return;
            if(requestNum > message.requestMessage.requestNum)
                return;

            if(client.numFails >= 2){
                try {
                    client.communicationInterface.sendMessage(new YieldMessage(message.requestMessage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            client.addInquireMessage(message);
        }

        if(returnMessage instanceof WaitMessage){
            WaitMessage message = (WaitMessage) returnMessage;
            if(requestNum > message.requestNum)
                return;
            try {
                client.addReply(message.serverId);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        if(returnMessage instanceof FileContentsMessage){
            FileContentsMessage message = (FileContentsMessage) returnMessage;
            if(message.message == null)
                System.out.println("This");
        }


    }

}
