package tslab.util;

import java.util.Arrays;
import java.net.InetAddress;

/**
 * FTP data mapping client to ftp server
 * <p/>
 * User: Shanbo Li and Sike Huang
 * Date: May 23, 2008
 * Time: 7:55:18 PM
 *
 * @author Shanbo Li and Sike Huang
 */
public class FTPDataMapping3 extends FTPDataMapping {

    public FTPDataMapping3(InetAddress clientAddress, byte[] clientMac, int clientPort, int bouncerPortToFTPServer) {
        super(clientAddress, clientMac, clientPort, bouncerPortToFTPServer);
    }

    public FTPDataMapping3(InetAddress clientAddress, int clientPort) {
        super(clientAddress, clientPort);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FTPDataMapping)) return false;

        FTPDataMapping that = (FTPDataMapping) o;

        if (clientPort != that.clientPort) return false;
        if (clientAddress != null ? !clientAddress.equals(that.clientAddress) : that.clientAddress != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (clientAddress != null ? clientAddress.hashCode() : 0);
        result = 31 * result + clientPort;
        return result;
    }
}
