package tslab.util;

import java.net.InetAddress;


/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 11:03:18 PM
 */
public abstract class TCPMapping {
    protected InetAddress clientAddress;
    protected byte[] clientMac;
    protected InetAddress serverAddress;
    protected int clientPort;
    protected int serverPort;
    protected int bouncerPortToServer;
    protected int bouncerPortToClient;
    protected boolean needAdjust;
    protected int interval;


    public TCPMapping(InetAddress clientAddress, byte[] clientMac, int clientPort, int bouncerPortToClient, int bouncerPortToServer, InetAddress serverAddress, int serverPort) {
        this.clientAddress = clientAddress;
        this.clientMac = clientMac;
        this.clientPort = clientPort;
        this.bouncerPortToServer = bouncerPortToServer;
        this.bouncerPortToClient = bouncerPortToClient;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }

    public TCPMapping(InetAddress clientAddress, int clientPort, int serverPort) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.serverPort = serverPort;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isNeedAdjust() {
        return needAdjust;
    }

    public void setNeedAdjust(boolean needAdjust) {
        this.needAdjust = needAdjust;
    }

    public TCPMapping(int bouncerPortToServer) {
        this.bouncerPortToServer = bouncerPortToServer;
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


}
