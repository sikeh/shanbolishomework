package tslab.util;

import java.net.InetAddress;

/**
 * User: Shanbo Li
 * Date: May 23, 2008
 * Time: 7:51:43 PM
 *
 * @author Shanbo Li
 */
public abstract class FTPDataMapping {
    protected InetAddress clientAddress;
    protected byte[] clientMac;
    protected int clientPort;
    protected int bouncerPortToFTPServer;

    protected FTPDataMapping(InetAddress clientAddress, byte[] clientMac, int clientPort, int bouncerPortToFTPServer) {
        this.clientAddress = clientAddress;
        this.clientMac = clientMac;
        this.clientPort = clientPort;
        this.bouncerPortToFTPServer = bouncerPortToFTPServer;
    }

    protected FTPDataMapping(int bouncerPortToFTPServer) {
        this.bouncerPortToFTPServer = bouncerPortToFTPServer;
    }

    protected FTPDataMapping(InetAddress clientAddress, int clientPort) {
        this.clientPort = clientPort;
        this.clientAddress = clientAddress;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public byte[] getClientMac() {
        return clientMac;
    }

    public int getClientPort() {
        return clientPort;
    }

    public int getBouncerPortToFTPServer() {
        return bouncerPortToFTPServer;
    }

   
}
