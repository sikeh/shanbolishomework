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
    private byte[] clientMac;
    private InetAddress serverAddress;
    private int clientPort;
    private int serverPort;
    private int bouncerPortToServer;
    private int bouncerPortToClient;

    public TCPMapping(InetAddress clientAddress, byte[] clientMac, int clientPort, int bouncerPortToClient, int bouncerPortToServer, InetAddress serverAddress, int serverPort) {
        this.clientAddress = clientAddress;
        this.clientMac = clientMac;
        this.clientPort = clientPort;
        this.bouncerPortToServer = bouncerPortToServer;
        this.bouncerPortToClient = bouncerPortToClient;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }

    public TCPMapping(int bouncerPortToServer, int serverPort) {
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

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getBouncerPortToServer() {
        return bouncerPortToServer;
    }

    public void setBouncerPortToServer(int bouncerPortToServer) {
        this.bouncerPortToServer = bouncerPortToServer;
    }

    public int getBouncerPortToClient() {
        return bouncerPortToClient;
    }

    public void setBouncerPortToClient(int bouncerPortToClient) {
        this.bouncerPortToClient = bouncerPortToClient;
    }

    public byte[] getClientMac() {
        return clientMac;
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
        result = 31 * result + (int) clientPort;
        result = 31 * result + (int) serverPort;
        result = 31 * result + (int) bouncerPortToServer;
        result = 31 * result + (int) bouncerPortToClient;
        return result;
    }
}
