package tslab.factory;

import jpcap.packet.*;
import tslab.exception.WrongInputPacketException;
import tslab.util.ICMPMapping;
import tslab.util.TCPMapping;

import java.net.InetAddress;
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
     List<TCPMapping> sessions = new ArrayList<TCPMapping>();
    private final static TCPFactory instance = new TCPFactory();


    private TCPFactory() {
    }

    /**
     * Get a instance of TCPFactory, pass serverAddress and serverMac to it
     */
    public static TCPFactory getInstance(){
        return instance;
    }

     public IPPacket toServer(IPPacket ipPacket) throws WrongInputPacketException {
         TCPPacket icmpIn;
        TCPPacket icmpOut = null;
        if (ipPacket instanceof TCPPacket) {
            icmpIn = (TCPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet.\nPlease check if the incoming packet is the correct type.");
        }

        //produce packet to server
        EthernetPacket ethIn = (EthernetPacket) icmpIn.datalink;
        icmpOut.type = ICMPPacket.ICMP_ECHOREPLY; // 0
        icmpOut.seq = icmpIn.seq;
        icmpOut.id = icmpIn.id;
        icmpOut.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_ICMP, this.bouncerAddress, this.serverAddress);
        EthernetPacket ethOut = new EthernetPacket();
        ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
        ethOut.src_mac = this.bouncerMac;
        ethOut.dst_mac = this.serverMac;
        icmpOut.datalink = ethOut;
        icmpOut.data = icmpIn.data;




        TCPPacket p=new TCPPacket(12,34,56,78,false,false,false,false,true,true,true,true,10,10);
                p.setIPv4Parameter(0,false,false,false,0,false,false,false,0,1010101,100,IPPacket.IPPROTO_TCP,
                    InetAddress.getByName("www.microsoft.com"),InetAddress.getByName("www.google.com"));
                p.data=("data").getBytes();

                EthernetPacket ether=new EthernetPacket();
                ether.frametype= EthernetPacket.ETHERTYPE_IP;
                ether.src_mac=new byte[]{(byte)0,(byte)1,(byte)2,(byte)3,(byte)4,(byte)5};
                ether.dst_mac=new byte[]{(byte)0,(byte)6,(byte)7,(byte)8,(byte)9,(byte)10};
                p.datalink=ether;
         return null;
    }


    public IPPacket toClient(IPPacket ipPacket) throws WrongInputPacketException {
        //TODO: complete this method.
        throw new UnsupportedOperationException("To be completed.");
    }


}
