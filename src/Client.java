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
    boolean partitioned;
    boolean readMessageReply;
    FileContentsMessage file;
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
        partitioned = false;
        readMessageReply = false;
        file = null;

        Thread thread = new Thread(communicationInterface);
        thread.start();
        while(!partitioned && requestNum < 15){
            requestNum++;
            fileId = generateRequestId();
            requestMessage();
        }

        if(partitioned)
            communicationInterface.sendAcknowledgement(new AcknowledgementMessage());
        while (partitioned){
            Thread.sleep(1000);
        }

        for(int i = 0 ;i < 5; i++){
            requestNum++;
            fileId = generateRequestId();
            requestMessage();
        }

        readFile();
        System.out.println("done");
        communicationInterface.sendMessage(new FinishedMessage(clientId));
        shutdown();
    }

    private void readFile() throws IOException, InterruptedException {
        int fileId = generateRequestId();
        ArrayList<Integer> servers = new ArrayList<>();
        server1 = Util.hash(fileId);
        servers.add(server1);
        servers.add(Util.getServer2(server1));
        servers.add(Util.getServer3(server1));
        Random ran = new Random();
        int serverToRead = servers.get(ran.nextInt(servers.size()));
        System.out.println("Attemting to read contents of file " + fileId + " on server " + server1);
        communicationInterface.sendMessage(new ReadMessage(clientId, serverToRead, fileId));
        long start = System.currentTimeMillis();
        while(!readMessageReply && (System.currentTimeMillis() - start) < Util.TIMEOUT_THRESHOLD){
            Thread.sleep(1000);
        }
        if(!readMessageReply)
            System.out.println("The random server chosen to read this message on was not available");
        else if(readMessageReply && file.message == null)
            System.out.println("This file does not exist");
        else
            System.out.println("The file contained the following contents \n" + file.message);
    }

    private void shutdown() {
        System.exit(0);
    }

    private void requestMessage() throws IOException, InterruptedException {
        server1 = Util.hash(fileId);
        String message = "client " + clientId + ": --message #" + requestNum + " , servers " + server1 + ", "
                + Util.getServer2(server1) + ", & " + Util.getServer3(server1);
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
        enterCS();

        if(!inCS && getNumResponded() < 3){
            if(getNumResponded() < 2){
                //operation failed, move on
                System.out.println("write for file object number " + fileId + " cannot be performed " +
                        "because the appropriate servers aren't accessible");
                cleanup();
                return;
            }
            else //we have access to two servers
                waitForGrant();

        }

        else if(!inCS){
            waitForGrant();
        }

        while (done.getCount() > 0){
            Thread.sleep(1000);
        }
        cleanup();
    }

    private void waitForGrant() throws IOException, InterruptedException {
        while(votesReceived < 2 && !inCS){
            Thread.sleep(1000);

        }
        enterCS();
    }

    synchronized private void cleanup() throws IOException {
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
            for(Integer server : serversResponded){
                communicationInterface.sendCommitMessage(new CommitMessage(clientId, requestNum, server, fileId));
            }
            done = new CountDownLatch(serversResponded.size());
            inCS = true;
        }

    }

    synchronized void addReply(int serverId) throws IOException {
        if(serversResponded.contains(serverId))
            return;
        else
            serversResponded.add(serverId);
        handleInquires();
    }

    synchronized int getRequestNum(){
        return requestNum;
    }

    private int generateRequestId() {
        Random r = new Random();
        int from = 1;
        int to = 7;
        return r.nextInt(to-from) + from;
    }


    synchronized public void incrementVotesReceived(int serverId) {
        votesReceived++;
        serversLocked.add(serverId);
    }

    synchronized public void incrementFailsReceived() {
        numFails++;
    }

    synchronized public int getNumResponded() {
        return serversResponded.size();
    }

    public void addInquireMessage(InquireMessage message) {
        for(InquireMessage im : inquireMessages){
            if(im.requestMessage.requestNum == message.requestMessage.requestNum
            && im.requestMessage.clientId == message.requestMessage.clientId)
                break;
        }
        inquireMessages.add(message);
    }

    public void handleInquires() throws IOException {
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
        }

    }

    public void partition() {
        partitioned = !partitioned;
    }
}
