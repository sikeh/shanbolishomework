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
    private InetAddress serverAddress;
    private byte[] serverMac;

    private final static ICMPFactory instance = new ICMPFactory();

    private ICMPFactory() {

    }

    /**
     * Get a instance of ICMPFactory, pass serverAddress and serverMac to it
     *
     * @param serverAddress
     * @param serverMac
     * @return
     */
    public static ICMPFactory getInstance(String serverAddress, byte[] serverMac) throws UnknownHostException {
        if (instance.serverAddress == null && instance.serverMac == null) {
            instance.serverAddress = InetAddress.getByName(serverAddress);
            instance.serverMac = serverMac;
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
        //TODO mac address to server is not correct here.
        EthernetPacket ethIn = (EthernetPacket) icmpIn.datalink;
        icmpOut.type = ICMPPacket.ICMP_ECHOREPLY; // 0
        icmpOut.id = icmpIn.id;
        icmpOut.seq = icmpIn.seq;
        icmpOut.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_ICMP, icmpIn.dst_ip, icmpIn.src_ip);
        EthernetPacket ethOut = new EthernetPacket();
        ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
        ethOut.src_mac = ethIn.dst_mac;
        ethOut.dst_mac = ethIn.src_mac;
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


        return outPacket;
    }
}
