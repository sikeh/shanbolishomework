package tslab.util;

import java.net.InetAddress;


/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 11:03:18 PM
 */
public class TCPMapping {
    private InetAddress clientAddress;
    private InetAddress serverAddress;
    private InetAddress bouncerAddress;
    private short clientPort;
    private short serverPort;
    private short bouncerPortToServer;
    private short bouncerPortToClient;

    public TCPMapping(InetAddress clientAddress, InetAddress bouncerAddress, short clientPort, short bouncerPortToServer, short bouncerPortToClient, short serverPort, InetAddress serverAddress) {
        this.clientAddress = clientAddress;
        this.bouncerAddress = bouncerAddress;
        this.clientPort = clientPort;
        this.bouncerPortToServer = bouncerPortToServer;
        this.bouncerPortToClient = bouncerPortToClient;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }

    public TCPMapping(short bouncerPortToServer, short serverPort) {
        this.bouncerPortToServer = bouncerPortToServer;
        this.serverPort = serverPort;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(InetAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    public InetAddress getBouncerAddress() {
        return bouncerAddress;
    }

    public void setBouncerAddress(InetAddress bouncerAddress) {
        this.bouncerAddress = bouncerAddress;
    }

    public short getClientPort() {
        return clientPort;
    }

    public void setClientPort(short clientPort) {
        this.clientPort = clientPort;
    }

    public short getServerPort() {
        return serverPort;
    }

    public void setServerPort(short serverPort) {
        this.serverPort = serverPort;
    }

    public short getBouncerPortToServer() {
        return bouncerPortToServer;
    }

    public void setBouncerPortToServer(short bouncerPortToServer) {
        this.bouncerPortToServer = bouncerPortToServer;
    }

    public short getBouncerPortToClient() {
        return bouncerPortToClient;
    }

    public void setBouncerPortToClient(short bouncerPortToClient) {
        this.bouncerPortToClient = bouncerPortToClient;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TCPMapping that = (TCPMapping) o;

        if (bouncerPortToServer != that.bouncerPortToServer) return false;
        if (serverPort != that.serverPort) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (clientAddress != null ? clientAddress.hashCode() : 0);
        result = 31 * result + (serverAddress != null ? serverAddress.hashCode() : 0);
        result = 31 * result + (bouncerAddress != null ? bouncerAddress.hashCode() : 0);
        result = 31 * result + (int) clientPort;
        result = 31 * result + (int) serverPort;
        result = 31 * result + (int) bouncerPortToServer;
        result = 31 * result + (int) bouncerPortToClient;
        return result;
    }
}
