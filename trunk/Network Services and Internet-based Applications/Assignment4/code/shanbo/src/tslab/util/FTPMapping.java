package tslab.util;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 11:03:36 PM
 */
public class FTPMapping {
    private int clientPort;
    private InetAddress clientIP;
    private byte[] clientMac;
    private int bouncerPortToFTP;

    public FTPMapping(int clientPort, InetAddress clientIP, byte[] clientMac, int bouncerPortToFTP) {
        this.clientPort = clientPort;
        this.clientIP = clientIP;
        this.clientMac = clientMac;
        this.bouncerPortToFTP = bouncerPortToFTP;
    }

    public FTPMapping(int bouncerPortToFTP) {
        this.bouncerPortToFTP = bouncerPortToFTP;
    }

    public int getClientPort() {
        return clientPort;
    }

    public InetAddress getClientIP() {
        return clientIP;
    }

    public byte[] getClientMac() {
        return clientMac;
    }

    public int getBouncerPortToFTP() {
        return bouncerPortToFTP;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FTPMapping)) return false;

        FTPMapping that = (FTPMapping) o;

        if (bouncerPortToFTP != that.bouncerPortToFTP) return false;

        return true;
    }

    public int hashCode() {
        return bouncerPortToFTP;
    }
}
