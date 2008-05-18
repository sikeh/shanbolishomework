package tslab.util;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 11:02:56 PM
 */
public class ICMPMapping {
    private InetAddress clientAddress;
    private byte[] clientMac;
    private short seq;
    private short id;

    /**
     * Initial a instance of ICMPSessionMapping<br/>
     * Use when comes a new packet from client.
     * @param clientAddress
     * @param clientMac
     * @param seq
     * @param id
     */
    public ICMPMapping(InetAddress clientAddress, byte[] clientMac, short seq, short id) {
        this.clientAddress = clientAddress;
        this.clientMac = clientMac;
        this.seq = seq;
        this.id = id;
    }

    /**
     * 
     * @param id
     * @param seq
     */
    public ICMPMapping(short id, short seq) {
        this.seq = seq;
        this.id = id;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public byte[] getClientMac() {
        return clientMac;
    }

    public void setClientMac(byte[] clientMac) {
        this.clientMac = clientMac;
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

        ICMPMapping that = (ICMPMapping) o;

        if (id != that.id) return false;
        if (seq != that.seq) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (int) seq;
        result = 31 * result + (int) id;
        return result;
    }
}
