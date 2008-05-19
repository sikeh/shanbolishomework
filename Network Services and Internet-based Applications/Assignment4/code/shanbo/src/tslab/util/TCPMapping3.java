package tslab.util;

import java.net.InetAddress;

/**
 * User: Shanbo Li
 * Date: May 19, 2008
 * Time: 11:11:51 PM
 *
 * @author Shanbo Li
 */
public class TCPMapping3 extends TCPMapping {
    public TCPMapping3(InetAddress clientAddress, byte[] clientMac, int clientPort, int bouncerPortToClient, int bouncerPortToServer, InetAddress serverAddress, int serverPort) {
        super(clientAddress, clientMac, clientPort, bouncerPortToClient, bouncerPortToServer, serverAddress, serverPort);
    }

    public TCPMapping3(int bouncerPortToServer) {
        super(bouncerPortToServer);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TCPMapping)) return false;

        TCPMapping that = (TCPMapping) o;

        if (bouncerPortToServer != that.bouncerPortToServer) return false;

        return true;
    }

    public int hashCode() {
        return bouncerPortToServer;
    }
}
