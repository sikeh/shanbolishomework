package tslab.util;

import java.net.InetAddress;

/**
 * Ftp data mapping ftp Server(initial) to client
 * <p/>
 * User: Shanbo Li and Sike Huang
 * Date: May 23, 2008
 * Time: 7:55:02 PM
 *
 * @author Shanbo Li
 */
public class FTPDataMapping1 extends FTPDataMapping {
    public FTPDataMapping1(InetAddress clientAddress, byte[] clientMac, int clientPort, int bouncerPortToFTPServer) {
        super(clientAddress, clientMac, clientPort, bouncerPortToFTPServer);
    }

    public FTPDataMapping1(int bouncerPortToFTPServer) {
        super(bouncerPortToFTPServer);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FTPDataMapping)) return false;

        FTPDataMapping that = (FTPDataMapping) o;

        if (bouncerPortToFTPServer != that.bouncerPortToFTPServer) return false;

        return true;
    }

    public int hashCode() {
        return bouncerPortToFTPServer;
    }

}
