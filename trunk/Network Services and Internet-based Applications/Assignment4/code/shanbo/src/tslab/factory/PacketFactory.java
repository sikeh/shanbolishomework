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
    protected InetAddress bouncerAddress;
    protected byte[] bouncerMac;
    protected InetAddress serverAddress;
    protected byte[] serverMac;


    /**
     * use this initial factory
     * @param bouncerAddress
     * @param bouncerMac
     * @param serverAddress
     * @param serverMac
     * @throws UnknownHostException
     */
    public void initial(String bouncerAddress, byte[] bouncerMac, String serverAddress, byte[] serverMac) throws UnknownHostException {
           this.bouncerAddress = InetAddress.getByName(bouncerAddress);
           this.bouncerMac = bouncerMac;
           this.serverAddress = InetAddress.getByName(serverAddress);
           this.serverMac = serverMac;
    }

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
