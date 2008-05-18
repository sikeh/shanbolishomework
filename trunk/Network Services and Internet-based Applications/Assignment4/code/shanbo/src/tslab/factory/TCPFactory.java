package tslab.factory;

import jpcap.packet.*;
import tslab.exception.WrongInputPacketException;
import tslab.util.TCPMapping;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 9:30:36 PM
 */
public class TCPFactory extends PacketFactory {

    //TODO: create package

    private static int sourcePort = 11240;
    List<TCPMapping> sessions = new ArrayList<TCPMapping>();
    private final static TCPFactory instance = new TCPFactory();


    private TCPFactory() {
    }

    /**
     * Get a instance of TCPFactory, pass serverAddress and serverMac to it
     */
    public static TCPFactory getInstance() {
        return instance;
    }

    /**
     * Create a packet according incomming packet
     *
     * @param ipPacket
     * @return new packet for sending
     * @throws WrongInputPacketException
     */
    public IPPacket createPacket(IPPacket ipPacket) throws WrongInputPacketException {
        TCPPacket tcpIn;
        if (ipPacket instanceof TCPPacket) {
            tcpIn = (TCPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet.\nPlease check if the incoming packet is the correct type.");
        }
        TCPMapping checkMapping = new TCPMapping(tcpIn.src_ip, tcpIn.src_port, tcpIn.dst_port);
        if (sessions.contains(checkMapping)){
            return toClient(ipPacket);
        }   else{
            return toServer(ipPacket);
        }
    }

    public IPPacket toServer(IPPacket ipPacket) throws WrongInputPacketException {
        //bouncer's source port for server
        sourcePort++;
        TCPPacket tcpIn;

        if (ipPacket instanceof TCPPacket) {
            tcpIn = (TCPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet.\nPlease check if the incoming packet is the correct type.");
        }

        //TODO check ack number here
        TCPPacket tcpOut = new TCPPacket(sourcePort, tcpIn.dst_port, tcpIn.sequence, tcpIn.ack_num, tcpIn.urg,
                tcpIn.ack, tcpIn.psh, tcpIn.rst, tcpIn.syn, tcpIn.fin, tcpIn.rsv1, tcpIn.rsv2, tcpIn.window, tcpIn.urgent_pointer);

        //produce packet to server
        EthernetPacket ethIn = (EthernetPacket) tcpIn.datalink;
        tcpOut.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_TCP, tcpIn.dst_ip, this.serverAddress);
        EthernetPacket ethOut = new EthernetPacket();
        ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
        ethOut.src_mac = ethIn.dst_mac;
        ethOut.dst_mac = this.serverMac;
        tcpOut.datalink = ethOut;
        tcpOut.data = tcpIn.data;

        sessions.add(new TCPMapping(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port));

        return tcpOut;
    }


    public IPPacket toClient(IPPacket ipPacket) throws WrongInputPacketException {
        TCPPacket tcpIn;
        if (ipPacket instanceof TCPPacket) {
            tcpIn = (TCPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet.\nPlease check if the incoming packet is the correct type.");
        }


        TCPMapping mapping = new TCPMapping(tcpIn.src_ip,tcpIn.src_port, tcpIn.dst_port);
        if (!sessions.contains(mapping)) {
            throw new WrongInputPacketException("Can not produce a packet according incoming packet.\nNo record found in session.\n");
        }
        TCPMapping record = sessions.get(sessions.indexOf(mapping));

        //TODO check ack number here
        TCPPacket tcpOut = new TCPPacket(record.getClientPort(), record.getBouncerPortToClient(), tcpIn.sequence, tcpIn.ack_num, tcpIn.urg,
                tcpIn.ack, tcpIn.psh, tcpIn.rst, tcpIn.syn, tcpIn.fin, tcpIn.rsv1, tcpIn.rsv2, tcpIn.window, tcpIn.urgent_pointer);

        //produce packet to server
        EthernetPacket ethIn = (EthernetPacket) tcpIn.datalink;
        tcpOut.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_TCP, tcpIn.dst_ip, record.getClientAddress());
        EthernetPacket ethOut = new EthernetPacket();
        ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
        ethOut.src_mac = ethIn.dst_mac;
        ethOut.dst_mac = record.getClientMac();
        tcpOut.datalink = ethOut;
        tcpOut.data = tcpIn.data;

        sessions.remove(record);

        return tcpOut;
    }


}
