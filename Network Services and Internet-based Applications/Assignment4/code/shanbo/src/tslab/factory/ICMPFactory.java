package tslab.factory;

import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.EthernetPacket;
import tslab.exception.WrongInputPacketException;
import tslab.util.ICMPSessionMapping;

import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 9:30:16 PM
 */
public class ICMPFactory implements PacketFactory {

    List<ICMPSessionMapping> sessions = new ArrayList<ICMPSessionMapping>();
    private InetAddress bouncerAddress;
    private byte[] bouncerMac;
    private InetAddress serverAddress;
    private byte[] serverMac;

    private final static ICMPFactory instance = new ICMPFactory();
    private static boolean isInitialed = false;

    private ICMPFactory() {

    }

    /**
     * Get a instance of ICMPFactory, pass serverAddress and serverMac to it
     *
     * @param bouncerAddress
     * @param bouncerMac
     * @param serverAddress
     * @param serverMac
     * @return
     * @throws UnknownHostException
     */
    public static ICMPFactory getInstance(String bouncerAddress, byte[] bouncerMac, String serverAddress, byte[] serverMac) throws UnknownHostException {
        if (!isInitialed) {
            instance.bouncerAddress = InetAddress.getByName(bouncerAddress);
            instance.bouncerMac = bouncerMac;
            instance.serverAddress = InetAddress.getByName(serverAddress);
            instance.serverMac = serverMac;
            isInitialed = true;
        }
        return instance;
    }

    /**
     * Produce a packet <b>to server</b> according the incoming packet from client.
     *
     * @param ipPacket the packet which comes from client
     * @return a packet which will be send <b>to server</b> according the incoming packet from client.
     * @throws WrongInputPacketException incoming packet is not a ICMP packet.
     */
    public IPPacket toServer(IPPacket ipPacket) throws WrongInputPacketException {
        ICMPPacket icmpIn;
        ICMPPacket icmpOut = null;
        if (ipPacket instanceof ICMPPacket) {
            icmpIn = (ICMPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet");
        }

        //add packet to sesson mapping
        sessions.add(new ICMPSessionMapping(icmpIn.src_ip, icmpIn.id, icmpIn.seq));

        //produce packet to server
        EthernetPacket ethIn = (EthernetPacket) icmpIn.datalink;
        icmpOut.type = ICMPPacket.ICMP_ECHOREPLY; // 0
        icmpOut.id = icmpIn.id;
        icmpOut.seq = icmpIn.seq;
        icmpOut.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_ICMP, instance.bouncerAddress, instance.serverAddress);
        EthernetPacket ethOut = new EthernetPacket();
        ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
        ethOut.src_mac = instance.bouncerMac;
        ethOut.dst_mac = instance.serverMac;
        icmpOut.datalink = ethOut;
        icmpOut.data = icmpIn.data;
        return icmpOut;
    }

    /**
     * Produce a packet <b>to client</b> according the incoming packet from server.
     *
     * @param ipPacket the packet which comes from server
     * @return a packet wich will be send <b>to client</b> according the incoming packet from server
     * @throws WrongInputPacketException incoming packet is not a ICMP packet.
     */
    public IPPacket toClient(IPPacket ipPacket) throws WrongInputPacketException {
        ICMPPacket inPacket;
        ICMPPacket outPacket = null;
        if (ipPacket instanceof ICMPPacket) {
            inPacket = (ICMPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet");
        }
        //TODO Find mapping in sessions and get useful infomation

        //TODO Produce packet


        return outPacket;
    }
}
