import java.io.IOException;
/*
 * Server class
 * Called from command line to create a server process
 * needs to be given a number as the server id
 */

public class Server {
    static Server server;
    int serverId;
    private ServerCommunicationInterface communicationInterface;

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
        communicationInterface.run();
    }

}
