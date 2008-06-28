package tslab.util;

import java.net.InetAddress;

/**
 * User: Shanbo Li
 * Date: May 19, 2008
 * Time: 11:11:36 PM
 *
 * @author Shanbo Li
 */
public class TCPMapping1 extends TCPMapping {
    public TCPMapping1(InetAddress clientAddress, byte[] clientMac, int clientPort, int bouncerPortToClient, int bouncerPortToServer, InetAddress serverAddress, int serverPort) {
        super(clientAddress, clientMac, clientPort, bouncerPortToClient, bouncerPortToServer, serverAddress, serverPort);
    }

    public TCPMapping1(InetAddress clientAddress, int clientPort, int serverPort) {
        super(clientAddress, clientPort, serverPort);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TCPMapping)) return false;

        TCPMapping that = (TCPMapping) o;

        if (clientPort != that.clientPort) return false;
        if (serverPort != that.serverPort) return false;
        if (clientAddress != null ? !clientAddress.equals(that.clientAddress) : that.clientAddress != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (clientAddress != null ? clientAddress.hashCode() : 0);
        result = 31 * result + clientPort;
        result = 31 * result + serverPort;
        return result;
    }


}
