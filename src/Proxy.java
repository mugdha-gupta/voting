import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Proxy {
    static HashMap<Integer, ArrayList<Integer>> disabledServerToServerChannels;
    static HashMap<Integer, ArrayList<Integer>> disabledServerToClientChannels;

    static private HashMap<Integer, ProxyCommunicationInterface> serverConnections;
    static private HashMap<Integer, ProxyCommunicationInterface> clientConnections;

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
        String input = scanner.next();
        if(input == "part"){
            disableConnections();
        }

        sendToAll(new GenericMessage(-1));

    }

    private static void sendToAll(GenericMessage message) throws IOException {
        for (ProxyCommunicationInterface runnable: serverConnections.values()
        ) {
            runnable.sendMessage(message);
        }
        for (ProxyCommunicationInterface runnable: clientConnections.values()
        ) {
            runnable.sendMessage(message);
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
        ArrayList<Integer> serverChannels;

        if(disabledServerToClientChannels.containsKey(server))
            serverChannels = disabledServerToClientChannels.get(server);
        else
            serverChannels = new ArrayList<>();

        if(!serverChannels.contains(client))
            serverChannels.add(client);

        disabledServerToClientChannels.put(server, serverChannels);

    }

    public static boolean sendServerToServerMessage(int senderServerId, int receiverServerId, Object message) throws IOException {
        if(disabledServerToServerChannels.containsKey(senderServerId) && disabledServerToServerChannels.get(senderServerId).contains(receiverServerId))
            return false;
        serverConnections.get(receiverServerId).sendMessage(message);
        return true;
    }

    public static  boolean sendServerToClientMessage(int serverId, int clientId, Object message) throws IOException {
        if(disabledServerToClientChannels.containsKey(serverId) && disabledServerToServerChannels.get(serverId).contains(clientId))
            return false;
        clientConnections.get(clientId).sendMessage(message);
        return true;
    }

    public static  boolean sendClientToServerMessage(int clientId, int serverId, Object message) throws IOException {
        if(disabledServerToClientChannels.containsKey(serverId) && disabledServerToServerChannels.get(serverId).contains(clientId))
            return false;
        serverConnections.get(serverId).sendMessage(message);
        return true;
    }

}
