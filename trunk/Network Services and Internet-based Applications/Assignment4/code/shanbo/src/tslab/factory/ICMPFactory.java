package tslab.factory;

import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.EthernetPacket;
import tslab.exception.WrongInputPacketException;
import tslab.util.ICMPMapping;

import java.util.List;
import java.util.ArrayList;


/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 9:30:16 PM
 */
public class ICMPFactory extends PacketFactory {

    List<ICMPMapping> sessions = new ArrayList<ICMPMapping>();


    private final static ICMPFactory instance = new ICMPFactory();


    private ICMPFactory() {
    }

    /**
     * Get a instance of ICMPFactory, pass serverAddress and serverMac to it
     */
    public static ICMPFactory getInstance(){
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
        ICMPPacket icmpOut = new ICMPPacket();
        if (ipPacket instanceof ICMPPacket) {
            icmpIn = (ICMPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet.\nPlease check if the incoming packet is the correct type.");
        }

        //produce packet to server
        EthernetPacket ethIn = (EthernetPacket) icmpIn.datalink;
        icmpOut.type = ICMPPacket.ICMP_ECHO; // 0
        icmpOut.seq = icmpIn.seq;
        icmpOut.id = icmpIn.id;
        icmpOut.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_ICMP, icmpIn.dst_ip, this.serverAddress);
        EthernetPacket ethOut = new EthernetPacket();
        ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
        ethOut.src_mac = ethIn.dst_mac;
        ethOut.dst_mac = this.serverMac;
        icmpOut.datalink = ethOut;
        icmpOut.data = icmpIn.data;

        //add packet to sesson mapping
        sessions.add(new ICMPMapping(icmpIn.src_ip, ethIn.src_mac, icmpIn.seq, icmpIn.id));

        return icmpOut;
    }

    /**
     * Produce a packet <b>to client</b> according the incoming packet from server.
     *
     * @param ipPacket the packet which comes from server
     * @return a packet wich will be send <b>to client</b> according the incoming packet from server
     * @throws WrongInputPacketException incoming packet is not a ICMP packet.<br/>Or the package infomation is not in the session.
     */
    public IPPacket toClient(IPPacket ipPacket) throws WrongInputPacketException {
        ICMPPacket icmpIn;
        ICMPPacket icmpOut = new ICMPPacket();
        if (ipPacket instanceof ICMPPacket) {
            icmpIn = (ICMPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet.\nPlease check if the incoming packet is the correct type.");
        }
        //Find mapping in sessions and get useful infomation
        ICMPMapping mapping = new ICMPMapping(icmpIn.id, icmpIn.seq);
        if (!sessions.contains(mapping)) {
            throw new WrongInputPacketException("Can not produce a packet according incoming packet.\nNo record found in session.\n");
        }
        ICMPMapping record = sessions.get(sessions.indexOf(mapping));

        EthernetPacket ethIn = (EthernetPacket) icmpIn.datalink;
        icmpOut.type = ICMPPacket.ICMP_ECHOREPLY; // 0
        icmpOut.id = icmpIn.id;
        icmpOut.seq = icmpIn.seq;
        icmpOut.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_ICMP,icmpIn.dst_ip , record.getClientAddress());
        EthernetPacket ethOut = new EthernetPacket();
        ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
        ethOut.src_mac = ethIn.dst_mac;
        ethOut.dst_mac = record.getClientMac();
        icmpOut.datalink = ethOut;
        icmpOut.data = icmpIn.data;

        //remove mapping from session
        sessions.remove(record);
        return icmpOut;
    }
}
