import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Proxy {
    static public HashMap<Integer, ArrayList<Integer>> disabledServerToServerChannels;
    static public HashMap<Integer, ArrayList<Integer>> disabledServerToClientChannels;

    static public HashMap<Integer, ProxyCommunicationInterface> serverConnections;
    static public HashMap<Integer, ProxyCommunicationInterface> clientConnections;

    public static void main(String[] args) throws IOException {
        disabledServerToClientChannels = new HashMap<>();
        disabledServerToClientChannels = new HashMap<>();
        serverConnections = new HashMap<>();
        clientConnections = new HashMap<>();

        for(int serverId = 1; serverId <= Util.NUM_SERVERS; serverId++){
            serverConnections.put(serverId, new ProxyCommunicationInterface(true, serverId));
        }

        for(int clientId = 1; clientId <= Util.NUM_CLIENTS; clientId++){
            clientConnections.put(clientId, new ProxyCommunicationInterface(false, clientId));
        }

        ExecutorService serverPool = Executors.newFixedThreadPool(7);
        for (ProxyCommunicationInterface runnable: serverConnections.values()
        ) {
            serverPool.execute(runnable);
        }

        ExecutorService clientPool = Executors.newFixedThreadPool(5);
        for (ProxyCommunicationInterface runnable: clientConnections.values()
        ) {
            clientPool.execute(runnable);
        }

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if(input.equals("part")){
            partition();
        }


    }

    private static void partition() {
        Scanner in = new Scanner(System.in);
        System.out.println("how many partitions will there be?");
        int numPart = in.nextInt();
        HashMap<Integer, HashSet<Integer>> partitionToServer = new HashMap<>();
        HashMap<Integer, HashSet<Integer>> partitionToClient = new HashMap<>();
        for(int i = 0 ; i < numPart; i++){
            partitionToClient.put(i+1, new HashSet<>());
            partitionToServer.put(i+1, new HashSet<>());
        }

        System.out.println("enter server partition data");
        for(int i = 0; i < Util.NUM_SERVERS; i++){
            int server = i+1;
            int partition = in.nextInt();
            partitionToServer.get(partition).add(server);
        }

        System.out.println("enter client paritition data");
        for(int i = 0; i < Util.NUM_CLIENTS; i++){
            int client = i+1;
            int partition = in.nextInt();
            partitionToClient.get(partition).add(client);
        }

        System.out.println(partitionToClient.toString());
        System.out.println(partitionToServer.toString());

        for(Integer part1 : partitionToServer.keySet()){
            for(Integer part2 : partitionToServer.keySet()){
                if(part1 < part2){
                    for (Integer server : partitionToServer.get(part1)) {
                        for(Integer server2 : partitionToServer.get(part2)){
                            System.out.println("disabling server " + server + " to server " + server2);
                            disableServerToServerConnection(server, server2);
                        }
                    }
                }
            }
        }

        for(Integer part1 : partitionToServer.keySet()){
            for(Integer part2 : partitionToClient.keySet()){
                if(part1 != part2){
                    for(Integer server: partitionToServer.get(part1)){
                        for(Integer client : partitionToClient.get(part2)){
                            System.out.println("disabliang server " + server + " to client " + client );
                            disableServerToClientConnection(server, client);
                        }
                    }
                }
            }
        }

    }

    private static void disableConnections() {
        disableServerToServerConnection(1, 2);
        disableServerToServerConnection(1, 3);
        disableServerToServerConnection(1, 4);
        disableServerToServerConnection(1, 5);
        disableServerToServerConnection(1, 6);
        disableServerToServerConnection(1, 7);

        disableServerToClientConnection(1, 3);
        disableServerToClientConnection(1, 4);
        disableServerToClientConnection(1, 5);

        disableServerToClientConnection(2, 1);
        disableServerToClientConnection(2, 2);

        disableServerToClientConnection(3, 1);
        disableServerToClientConnection(3, 2);

        disableServerToClientConnection(4, 1);
        disableServerToClientConnection(4, 2);

        disableServerToClientConnection(5, 1);
        disableServerToClientConnection(5, 2);

        disableServerToClientConnection(6, 1);
        disableServerToClientConnection(6, 2);

        disableServerToClientConnection(7, 1);
        disableServerToClientConnection(7, 2);
    }


    public static void disableServerToServerConnection(int serverA, int serverB){
        if(disabledServerToServerChannels == null)
            disabledServerToServerChannels = new HashMap<>();

        ArrayList<Integer> serverAChannels;
        ArrayList<Integer> serverBChannels;
        if(disabledServerToServerChannels.containsKey(serverA))
            serverAChannels = disabledServerToServerChannels.get(serverA);
        else
            serverAChannels = new ArrayList<>();

        if(disabledServerToServerChannels.containsKey(serverB))
            serverBChannels = disabledServerToServerChannels.get(serverB);
        else
            serverBChannels = new ArrayList<>();

        if(!serverAChannels.contains(serverB))
            serverAChannels.add(serverB);

        if(!serverBChannels.contains(serverA))
            serverBChannels.add(serverA);

        disabledServerToServerChannels.put(serverA, serverAChannels);
        disabledServerToServerChannels.put(serverB, serverBChannels);
    }

    public static void disableServerToClientConnection(int server, int client){
        if(disabledServerToClientChannels == null)
            disabledServerToServerChannels = new HashMap<>();

        ArrayList<Integer> serverChannels;

        if(disabledServerToClientChannels.containsKey(server))
            serverChannels = disabledServerToClientChannels.get(server);
        else
            serverChannels = new ArrayList<>();

        if(!serverChannels.contains(client))
            serverChannels.add(client);

        disabledServerToClientChannels.put(server, serverChannels);

    }

//    public static boolean sendServerToServerMessage(int senderServerId, int receiverServerId, MyMessage myMessage) throws IOException {
//        if(disabledServerToServerChannels == null){
//            serverConnections.get(receiverServerId).sendMessage(myMessage);
//            return true;
//        }
//
//        else if(disabledServerToServerChannels.containsKey(senderServerId)){
//            ArrayList<Integer> banner = disabledServerToServerChannels.get(senderServerId);
//            if(!banner.contains(receiverServerId)){
//                serverConnections.get(receiverServerId).sendMessage(myMessage);
//                return true;
//            }
//        }
//
//        return false;
//    }

    public static  boolean sendServerToClientMessage(ReplyMessage replyMessage) throws IOException {
        if(disabledServerToClientChannels.containsKey(replyMessage.serverId) &&
                disabledServerToServerChannels.get(replyMessage.serverId).contains(replyMessage.clientId)){
            return false;
        }
        clientConnections.get(replyMessage.clientId).sendMessage(replyMessage);
        return true;
    }

    public static  boolean sendServerToClientMessage(WaitMessage replyMessage) throws IOException {
        if(disabledServerToClientChannels.containsKey(replyMessage.serverId) &&
                disabledServerToServerChannels.get(replyMessage.serverId).contains(replyMessage.clientId)){
            return false;
        }
        clientConnections.get(replyMessage.clientId).sendMessage(replyMessage);
        return true;
    }

    public static  boolean sendServerToClientMessage(DoneMessage message) throws IOException {
        if(disabledServerToClientChannels.containsKey(message.serverId) &&
                disabledServerToServerChannels.get(message.serverId).contains(message.clientId))
            return false;
        clientConnections.get(message.clientId).sendMessage(message);
        return true;
    }

    public static  boolean sendServerToClientMessage(FailedMessage failedMessage) throws IOException {
        if(disabledServerToClientChannels.containsKey(failedMessage.serverId) &&
                disabledServerToServerChannels.get(failedMessage.serverId).contains(failedMessage.requestMessage.clientId))
            return false;
        clientConnections.get(failedMessage.requestMessage.clientId).sendMessage(failedMessage);
        return true;
    }

    public static  boolean sendServerToClientMessage(InquireMessage inquireMessage) throws IOException {
        if(disabledServerToClientChannels.containsKey(inquireMessage.serverId) &&
                disabledServerToServerChannels.get(inquireMessage.serverId).contains(inquireMessage.clientReplied))
            return false;
        clientConnections.get(inquireMessage.clientReplied).sendMessage(inquireMessage);
        return true;
    }


    public static boolean sendClientRequestToServerMessage(RequestMessage message) throws IOException {
        int server = message.serverId;
        int client = message.clientId;
        if(disabledServerToClientChannels.containsKey(server) &&
                disabledServerToClientChannels.get(server).contains(client)){
            return false;
        }

        serverConnections.get(server).sendMessage(message);
        return true;
    }

    public static void sendClientYieldToServerMessage(YieldMessage yieldMessage) throws IOException {
        int server = yieldMessage.requestMessage.serverId;
        int client = yieldMessage.requestMessage.clientId;
        if(disabledServerToClientChannels.containsKey(server) &&
                disabledServerToClientChannels.get(server).contains(client)){
            return;
        }

        serverConnections.get(server).sendMessage(yieldMessage);
    }

    public static void sendClientCommitToServerMessage(CommitMessage message) throws IOException {
        int server = message.serverId;
        int client = message.clientId;
        if(disabledServerToClientChannels.containsKey(server) &&
                disabledServerToClientChannels.get(server).contains(client)){
            return;
        }

        serverConnections.get(server).sendMessage(message);
    }
    public static void sendClientReleaseToServerMessage(ReleaseMessage message) throws IOException {
        int server = message.serverId;
        int client = message.clientId;
        if(disabledServerToClientChannels.containsKey(server) &&
                disabledServerToClientChannels.get(server).contains(client)){
            return;
        }

        serverConnections.get(server).sendMessage(message);
    }
}
