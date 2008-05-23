package tslab.factory;

import jpcap.packet.IPPacket;
import tslab.exception.WrongInputPacketException;

import java.net.UnknownHostException;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 11:24:14 PM
 */
public abstract class PacketFactory {
    protected InetAddress serverAddress;
    protected byte[] serverMac;
    protected int serverPort = -1;


    /**
     * use this initial factory
     *
     * @param serverAddress
     * @param serverMac
     * @throws UnknownHostException
     */
    public void initial(String serverAddress, byte[] serverMac) throws UnknownHostException {
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverMac = serverMac;
    }

    public void initial(InetAddress serverAddress, byte[] serverMac) {
        this.serverAddress = serverAddress;
        this.serverMac = serverMac;
    }

    /**
     *
     * @param serverAddress
     * @param serverPort the server port, -1 means use original packet's dst port.
     * @param serverMac
     */
    public void initial(InetAddress serverAddress, int serverPort, byte[] serverMac) {
        this.serverAddress = serverAddress;
        this.serverMac = serverMac;
        this.serverPort = serverPort;
    }

    public abstract IPPacket createPacket(IPPacket ipPacket) throws WrongInputPacketException;


    /**
     * Produce a packet <b>to server</b> according the incoming packet from client.
     *
     * @param ipPacket packet come from client
     * @return
     * @throws WrongInputPacketException
     */
    public abstract IPPacket toServer(IPPacket ipPacket) throws WrongInputPacketException;

    /**
     * Produce a packet <b>to client</b> according the incoming packet from server.
     *
     * @param ipPacket packet come from server
     * @return
     * @throws WrongInputPacketException
     */
    public abstract IPPacket toClient(IPPacket ipPacket) throws WrongInputPacketException;


}
