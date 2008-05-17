package tslab;

import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;
import jpcap.packet.IPPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.EthernetPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * Date: May 17, 2008
 * Time: 4:08:34 PM
 * @author Sike Huang and Shanbo Li
 */
public class PacketListener {
    private JpcapCaptor captor;
    private String listenIp;
    private String listenPort;

    public PacketListener(JpcapCaptor captor, String listenIp, String listenPort) {
        this.captor = captor;
        this.listenIp = listenIp;
        this.listenPort = listenPort;
    }

    public void receive() {
        captor.loopPacket(-1, new MyPacketRecevier(captor));
    }
}

class MyPacketRecevier implements PacketReceiver {
    private JpcapCaptor captor;

    public MyPacketRecevier(JpcapCaptor captor) {
        this.captor = captor;
    }

    public void receivePacket(Packet packet) {
//        System.out.println(packet);
        if (packet instanceof ICMPPacket) {
            ICMPPacket icmpIn = (ICMPPacket) packet;
            try {
                if (icmpIn.src_ip.equals(InetAddress.getByName("10.8.0.50"))) {
                    return;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            System.out.println(icmpIn);
            EthernetPacket ethIn = (EthernetPacket) icmpIn.datalink;

            ICMPPacket icmpOut = new ICMPPacket();
            icmpOut.type = ICMPPacket.ICMP_ECHOREPLY; // 0
            icmpOut.seq = icmpIn.seq;
            icmpOut.id = icmpIn.id;
            icmpOut.setIPv4Parameter(0,false,false,false,0,false,false,false,0,1010101,100, IPPacket.IPPROTO_ICMP, icmpIn.dst_ip, icmpIn.src_ip);
            EthernetPacket ethOut = new EthernetPacket();
            ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
            ethOut.src_mac = ethIn.dst_mac;
            ethOut.dst_mac = ethIn.src_mac;
            icmpOut.datalink = ethOut;
            icmpOut.data = icmpIn.data;
            captor.getJpcapSenderInstance().sendPacket(icmpOut);
        }
    }
}
