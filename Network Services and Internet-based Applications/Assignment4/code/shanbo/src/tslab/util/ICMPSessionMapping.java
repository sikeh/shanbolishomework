package tslab.util;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 11:02:56 PM
 */
public class ICMPSessionMapping {
    private InetAddress clientAddress;
    private InetAddress serverAddress;
    private short seq;
    private short id;

    /**
     * Initial a instance of ICMPSessionMapping<br/>
     * Use when comes a new packet from client.
     * @param clientAddress the client address
     * @param id ICMP packet id
     * @param seq ICMP packet seq
     */
    public ICMPSessionMapping(InetAddress clientAddress, short id, short seq) {
        this.id = id;
        this.seq = seq;
        this.clientAddress = clientAddress;
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

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ICMPSessionMapping that = (ICMPSessionMapping) o;

        if (id != that.id) return false;
        if (seq != that.seq) return false;
        if (clientAddress != null ? !clientAddress.equals(that.clientAddress) : that.clientAddress != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (clientAddress != null ? clientAddress.hashCode() : 0);
        result = 31 * result + (int) seq;
        result = 31 * result + (int) id;
        return result;
    }
}
