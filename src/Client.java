import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Client {
    static Client client;
    int clientId;
    public ClientCommunicationInterface communicationInterface;
    int requestNum;
    int votesReceived;
    int numFails;
    HashSet<Integer> serversLocked;
    ArrayList<InquireMessage> inquireMessages;
    HashSet<Integer> serversResponded;
    int server1;
    int fileId;
    CountDownLatch done;
    boolean inCS;

    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length != 1)
            return;
        int id = Integer.parseInt(args[0]);
        client = new Client(id);
    }

    public Client(int id) throws IOException, InterruptedException {
        System.out.println("client " + id + " starts at time: " + System.currentTimeMillis());
        client = this;
        clientId = id;
        communicationInterface = new ClientCommunicationInterface(client);
        requestNum = 0;
        votesReceived = 0;
        serversLocked = new HashSet<>();
        serversResponded = new HashSet<>();
        numFails = 0;
        server1 =0;
        fileId = 0;
        inCS =false;

        Thread thread = new Thread(communicationInterface);
        thread.start();

        for(int i = 0 ; i < 5 ; i++){
            requestNum++;
            fileId = generateRequestId();
            requestMessage();
        }
    }

    private void requestMessage() throws IOException, InterruptedException {
        server1 = Util.hash(fileId);
        String message = "client " + clientId + "fileid "+ fileId + " request number " + requestNum;
        System.out.println(message);
        communicationInterface.sendRequest(
                new RequestMessage(clientId, fileId, server1, requestNum, message)
        );
        communicationInterface.sendRequest(
                new RequestMessage(clientId, fileId, Util.getServer2(server1) , requestNum, message)
        );
        communicationInterface.sendRequest(
                new RequestMessage(clientId, fileId, Util.getServer3(server1), requestNum, message)
        );

        long start = System.currentTimeMillis();
        while(getNumResponded() < 3 && (System.currentTimeMillis() - start) < Util.TIMEOUT_THRESHOLD){
        }
        System.out.println("servers responded number " + getNumResponded());
        enterCS();

        if(!inCS && getNumResponded() < 3){
            System.out.println("partitioned");
            if(getNumResponded() < 2){
                //operation failed, move on
            }
            else //we have access to two servers
                waitForGrant();

        }

        else if(!inCS){
            System.out.println("waiting for resource");
            waitForGrant();
        }

        System.out.println("done " + done);
        while (done.getCount() > 0){
            Thread.sleep(1000);
            System.out.println("done " + done);
        }
        cleanup();
    }

    private void waitForGrant() throws IOException, InterruptedException {
        System.out.println("waiting for Grant");
        while(votesReceived < 2 && !inCS){
            Thread.sleep(3000);
            System.out.println("still waiting " + votesReceived);

        }
        System.out.println("RECEIVED GRANT");
        enterCS();
    }

    synchronized private void cleanup() throws IOException {
        System.out.println("cleanup called");
        if(inquireMessages == null)
            inquireMessages = new ArrayList<>();
        sendRelease();
        votesReceived = 0;
        numFails = 0;
        serversLocked.clear();
        inquireMessages.clear();
        serversResponded.clear();
        server1 = 0;
        fileId = 0;
        inCS = false;
    }

    synchronized private void sendRelease() throws IOException {
        for(Integer server: serversResponded){
            communicationInterface.sendRelease(new ReleaseMessage(clientId, server, fileId, requestNum));
        }
    }

    synchronized public void enterCS() throws IOException {
        if(votesReceived >= 2 && !inCS){
            System.out.println("entered CS");
            for(Integer server : serversResponded){
                communicationInterface.sendCommitMessage(new CommitMessage(clientId, requestNum, server, fileId));
            }
            done = new CountDownLatch(serversResponded.size());
            inCS = true;
            System.out.println("exiting CS");
        }

    }

    synchronized void addReply(int serverId){
        if(serversResponded.contains(serverId))
            return;
        else
            serversResponded.add(serverId);
    }

    synchronized int getRequestNum(){
        return requestNum;
    }

    private int generateRequestId() {
        return 5;
//        Random r = new Random();
//        int from = 1;
//        int to = 71;
//        return r.nextInt(to-from) + from;
    }


    synchronized public void incrementVotesReceived(int serverId) {
        votesReceived++;
        serversLocked.add(serverId);
    }

    synchronized public void incrementFailsReceived() {
        numFails++;
    }

    synchronized public int getNumFails() {
        return numFails;
    }


    synchronized public int getNumResponded() {
        return serversResponded.size();
    }

    synchronized public void addInquireMessage(InquireMessage message) {
        inquireMessages.add(message);
    }

    synchronized public void handleInquires() throws IOException {
        System.out.println("checking inquires");
        if(inquireMessages == null || inquireMessages.isEmpty())
            return;

        if(numFails >= 2) {
            for (InquireMessage message : inquireMessages) {
                if(message.requestMessage.requestNum != requestNum)
                    continue;
                communicationInterface.sendMessage(new YieldMessage(message.requestMessage));
            }
            inquireMessages.clear();
        }
        else if(numFails + votesReceived < 3){
            System.out.println("error: fails and votes != 3");
        }

    }
}
