import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
/*
 * Server class
 * Called from command line to create a server process
 * needs to be given a number as the server id
 */

public class Server {
    private static Server server;
    int serverId;
    ServerCommunicationInterface communicationInterface;
    private HashMap<Integer, Integer> fileToVoteCastClient;
    private HashMap<Integer, PriorityQueue<RequestMessage>> requestQueue;
    private HashMap<Integer, RequestMessage> currentRequestMessage;
    private HashMap<Integer, FileObject> files;
    private HashSet<Integer> finishedClients;

    //get server id from command line and instantiate server
    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length != 1)
            return;
        int id = Integer.parseInt(args[0]);
        server = new Server(id);
    }

    //constructor sets variables and sets up sockets
    Server(int serverId) throws IOException {
        System.out.println("server " + serverId + " starts at time: " + System.currentTimeMillis());
        server = this;
        this.serverId = serverId;
        communicationInterface = new ServerCommunicationInterface(server);
        Thread t = new Thread(communicationInterface);
        t.start();
        fileToVoteCastClient = new HashMap<>();
        requestQueue = new HashMap<>();
        currentRequestMessage = new HashMap<>();
        files = new HashMap<>();
        finishedClients = new HashSet<>();
    }

    synchronized public boolean replySentForFile(int fileId){
        if(fileToVoteCastClient == null)
            fileToVoteCastClient = new HashMap<>();
        return fileToVoteCastClient.containsKey(fileId);
    }

    synchronized public int getClientThatReceivedVote(int fileId){
        return fileToVoteCastClient.get(fileId);
    }

    synchronized public void queueRequest(RequestMessage requestMessage) {
        PriorityQueue<RequestMessage> queue = null;
        if(requestQueue == null)
            requestQueue = new HashMap<>();
        if(requestQueue.containsKey(requestMessage.objectToEditId))
            queue = requestQueue.get(requestMessage.objectToEditId);
        if(queue == null)
            queue = new PriorityQueue<>();
        queue.add(requestMessage);
        requestQueue.put(requestMessage.objectToEditId, queue);

    }

    synchronized public void receiveYield(YieldMessage message) throws IOException {
        int fileId = message.requestMessage.objectToEditId;
        RequestMessage currentRequest = currentRequestMessage.get(fileId);
        castVote(fileId);
        queueRequest(currentRequest);
    }

    synchronized public void receiveRequest(RequestMessage requestMessage) throws IOException {
        //we must queue the request
        queueRequest(requestMessage);
        if(replySentForFile(requestMessage.objectToEditId)){
            int clientReplied = getClientThatReceivedVote(requestMessage.objectToEditId);
            if(finishedClients.contains(clientReplied)){
                castVote(requestMessage.objectToEditId);
            }
            if(clientReplied < requestMessage.clientId) // send a failed message to the client
            {
                communicationInterface.sendMessage(new FailedMessage(serverId, requestMessage));
            }
            //else if we have sent it to a client with a lower priority
            else if(clientReplied > requestMessage.clientId) // send an inquire to the client we have sent a reply to{
            {
                communicationInterface.sendMessage(new InquireMessage(serverId, clientReplied, currentRequestMessage.get(requestMessage.objectToEditId)));
                communicationInterface.sendMessage(new WaitMessage(requestMessage.clientId, requestMessage.serverId, requestMessage.requestNum, requestMessage.objectToEditId));
            }
            else {
                if(currentRequestMessage.get(requestMessage.objectToEditId).requestNum == requestMessage.requestNum) {
                    System.out.println("error: two requests from same client");
                }
            }

            checkToCast();
        }

        //if we haven't sent a reply, set reply sent for this file to the client you are sending the reply to
        else{
            castVote(requestMessage.objectToEditId);
        }


    }

    synchronized void castVote(int fileId) throws IOException {
        if(currentRequestMessage == null)
            currentRequestMessage = new HashMap<>();
        if(requestQueue.containsKey(fileId)) {
            RequestMessage requestMessage = requestQueue.get(fileId).poll();
            if (requestMessage == null)
                return;
            fileToVoteCastClient.put(requestMessage.objectToEditId, requestMessage.clientId);
            currentRequestMessage.put(requestMessage.objectToEditId, requestMessage);
            communicationInterface.sendMessage(new ReplyMessage(serverId, requestMessage.clientId, requestMessage));
        }
        checkToCast();
    }

    synchronized void commit(CommitMessage message) throws IOException {
        RequestMessage current = currentRequestMessage.get(message.fileId);
        RequestMessage toCommit = current;
        if(current.clientId != message.clientId || current.requestNum != message.requestNum){
            for(RequestMessage rm : requestQueue.get(message.fileId)){
                if(rm.clientId == message.clientId && rm.requestNum == message.requestNum)
                    toCommit = rm;
            }

            if(toCommit == current){
                return;
            }
        }

        if(!files.containsKey(message.fileId)){
            FileObject file = new FileObject(toCommit.objectToEditId, serverId);
            files.put(message.fileId, file);
        }

        files.get(message.fileId).commit(toCommit.message);

        requestQueue.get(message.fileId).remove(toCommit);

    }

    synchronized public void release(ReleaseMessage message) throws IOException {
        if(requestQueue.containsKey(message.fileId)){
            RequestMessage commited = null;
            for(RequestMessage rm : requestQueue.get(message.fileId)){
                if(rm.clientId == message.clientId && rm.objectToEditId == message.fileId && rm.requestNum == message.requestNum)
                    commited = rm;
            }
            if(commited != null)
                requestQueue.get(message.fileId).remove(commited);
        }
        if(currentRequestMessage == null || fileToVoteCastClient == null)
            return;
        if(fileToVoteCastClient.containsKey(message.fileId) && fileToVoteCastClient.get(message.fileId) != message.clientId)
            return;
        cleanup(message.fileId);
        castVote(message.fileId);
        checkToCast();
    }

    synchronized public void cleanup(int fileId) {
        if(fileId == -1){
            fileToVoteCastClient.clear();
            currentRequestMessage.clear();
            requestQueue.clear();
        }
        fileToVoteCastClient.remove(fileId);
        currentRequestMessage.remove(fileId);

    }

    synchronized public void finishClient(int clientId) throws IOException {
        ArrayList<Integer> filesToRemove = new ArrayList<>();
        for(Integer file : fileToVoteCastClient.keySet()){
            if(fileToVoteCastClient.get(file) == clientId)
                filesToRemove.add(file);
        }
        for(Integer file : filesToRemove)
            fileToVoteCastClient.remove(file);

        ArrayList<RequestMessage> requestToRemove = new ArrayList<>();
        for(Integer fileId : requestQueue.keySet()){
            for(RequestMessage rm: requestQueue.get(fileId)){
                if (rm.clientId == clientId)
                    requestToRemove.add(rm);
            }
            for(RequestMessage rm: requestToRemove)
                requestQueue.get(fileId).remove(rm);
            requestToRemove.clear();
        }
        filesToRemove.clear();
        for(Integer file : currentRequestMessage.keySet()){
            if(currentRequestMessage.get(file).clientId == clientId)
                filesToRemove.add(file);
        }
        for(Integer file : filesToRemove)
            fileToVoteCastClient.remove(file);

        finishedClients.add(clientId);
        checkToCast();
    }

    public void shutdown(){
        System.exit(0);
    }
    synchronized  void checkToCast() throws IOException {
        for(Integer fileId : requestQueue.keySet()){
            if(!fileToVoteCastClient.containsKey(fileId))
                castVote(fileId);
        }
    }

    synchronized public void returnFileContents(ReadMessage message) throws IOException {
        if(!files.containsKey(message.fileId)){
            communicationInterface.sendMessage(new FileContentsMessage(message.clientId, message.serverId, null));
            return;
        }

        String toReturn = files.get(message.fileId).getFileContents();
        communicationInterface.sendMessage(new FileContentsMessage(message.clientId, message.serverId, toReturn));
    }
}
