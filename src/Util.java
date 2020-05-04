
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
    static String proxyAddress = "dc10.utdallas.edu";

    static final int NUM_SERVERS = 7;
    static final int NUM_CLIENTS = 5;

    //Ports that the Server listens on to connect to the proxy
    static final int SERVER_PROXY_LISTENING_PORT = 13000;
    static final int CLIENT_PROXY_LISTENING_PORT = 14000;

    //timeout
    static final int TIMEOUT_THRESHOLD = 15000;//we will wait 15 at most

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

    //hash function that indicates server location for a file
    static int hash(int id){
        int ret = id%NUM_SERVERS;
        return ret+1;
    }

    static int getServer2(int id){
        int ret = (id+1)%NUM_SERVERS;
        if(ret == 0)
            return NUM_SERVERS;
        else
            return ret;
    }

    static int getServer3(int id){
        int ret = (id+2)%NUM_SERVERS;
        if(ret == 0)
            return NUM_SERVERS;
        else
            return ret;
    }

}
