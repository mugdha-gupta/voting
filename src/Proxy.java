import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Proxy {
    static HashMap<Integer, ArrayList<Integer>> disabledServerToServerChannels;
    static HashMap<Integer, ArrayList<Integer>> disabledServerToClientChannels;

    private HashMap<Integer, ProxyCommunicationInterface> serverConnections;
    private HashMap<Integer, ProxyCommunicationInterface> clientConnections;

    public Proxy() throws IOException {
        disabledServerToClientChannels = new HashMap<>();
        disabledServerToClientChannels = new HashMap<>();

        for(int serverId = 1; serverId <= Util.NUM_SERVERS; serverId++){
            serverConnections.put(serverId, new ProxyCommunicationInterface(true, serverId));
        }

        for(int clientId = 1; clientId <= Util.NUM_SERVERS; clientId++){
            clientConnections.put(clientId, new ProxyCommunicationInterface(false, clientId));
        }

        for (Integer serverId : serverConnections.keySet()) {
            serverConnections.get(serverId).out.write(serverId);
        }

        for (Integer serverId : serverConnections.keySet()) {
            serverConnections.get(serverId).out.write(serverId);
        }

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

    public boolean sendServerToServerMessage(int senderServerId, int receiverServerId){
        //TODO: implement
        return false;
    }

    public boolean sendServerToClientMessage(int serverId, int clientId){
        //TODO: implement
        return false;
    }

    public boolean sendClientToServerMessage(int clientId, int serverId){
        //TODO: implement
        return false;
    }

}
