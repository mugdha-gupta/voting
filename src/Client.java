import java.io.IOException;

public class Client {
    static Client client;
    int clientId;
    private ClientCommunicationInterface communicationInterface;

    public static void main(String[] args) throws IOException {
        if(args.length != 1)
            return;
        int id = Integer.parseInt(args[0]);
        client = new Client(id);
    }

    public Client(int id) throws IOException {
        System.out.println("client " + id + " starts at time: " + System.currentTimeMillis());
        client = this;
        clientId = id;
        communicationInterface = new ClientCommunicationInterface(client);
        communicationInterface.run();
    }
}
