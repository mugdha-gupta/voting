
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Util class
 * This class holds commonly used information and methods
 */
public class Util {
    static String[] serverAddresses = {"dc01.utdallas.edu", "dc02.utdallas.edu", "dc03.utdallas.edu", "dc04.utdallas.edu", "dc05.utdallas.edu", "dc06.utdallas.edu", "dc07.utdallas.edu"};
    static String proxyAddress = "dc10.utdallas.edu";

    static final int NUM_SERVERS = 7;
    static final int NUM_CLIENTS = 5;

    //Ports that the Server listens on to connect to the proxy
    static final int SERVER_PROXY_LISTENING_PORT = 13000;
    static final int CLIENT_PROXY_LISTENING_PORT = 14000;

    //timeout
    static final int TIMEOUT_THRESHOLD = 5000;//we will wait 5 at most

    //returns the socket to the proxy for the server
    static Socket getProxyServerSocket() throws IOException {
        //create local socket
        Socket socket = new Socket();
        socket.setReuseAddress(true);
        InetSocketAddress localInsa = new InetSocketAddress(InetAddress.getLocalHost(), Util.SERVER_PROXY_LISTENING_PORT);
        socket.bind(localInsa);

        //set remote address and connect
        InetSocketAddress remoteInsa = new InetSocketAddress(proxyAddress, Util.SERVER_PROXY_LISTENING_PORT);
        socket.connect(remoteInsa);
        return socket;
    }

    static Socket getProxyClientSocket() throws IOException {
        //create local socket
        Socket socket = new Socket();
        socket.setReuseAddress(true);
        InetSocketAddress localInsa = new InetSocketAddress(InetAddress.getLocalHost(), Util.CLIENT_PROXY_LISTENING_PORT);
        socket.bind(localInsa);

        //set remote address and connect
        InetSocketAddress remoteInsa = new InetSocketAddress(proxyAddress, Util.CLIENT_PROXY_LISTENING_PORT);
        socket.connect(remoteInsa);
        return socket;
    }

    //Get socket on Proxy side as Server
    static Socket getProxySideSocket(int remotePort) throws IOException {
        ServerSocket listener = new ServerSocket(remotePort);
        Socket socket = listener.accept();
        listener.close();
        return socket;
    }

    static int hash(int id){
        return id%NUM_SERVERS;
    }

}
