package tslab;

import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;
import jpcap.packet.IPPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.EthernetPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;

import tslab.factory.ICMPFactory;
import tslab.exception.WrongInputPacketException;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * Date: May 17, 2008
 * Time: 4:08:34 PM
 *
 * @author Sike Huang and Shanbo Li
 */
public class PacketListener {
    private JpcapCaptor captor;
    private String serverIp;
    private String serverPort;
    private InetAddress bouncerAddress;
    private byte[] bouncerMac;
    private InetAddress serverAddress;
    private byte[] serverMac;
    private ICMPFactory icmpFactory;

    public PacketListener(JpcapCaptor captor, String serverIp, String serverPort) {
        this.captor = captor;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        icmpFactory = ICMPFactory.getInstance();
    }

    public PacketListener(JpcapCaptor captor, InetAddress bouncerAddress, byte[] bouncerMac, InetAddress serverAddress, byte[] serverMac) {
        this.captor = captor;
        this.bouncerAddress = bouncerAddress;
        this.bouncerMac = bouncerMac;
        this.serverAddress = serverAddress;
        this.serverMac = serverMac;
        icmpFactory = ICMPFactory.getInstance();
        icmpFactory.initial(serverAddress, serverMac);
    }

    public void receive() {
        captor.loopPacket(-1, new MyPacketRecevier(captor, bouncerAddress, bouncerMac, serverAddress, serverMac, icmpFactory));
    }
}

class MyPacketRecevier implements PacketReceiver {
    private JpcapCaptor captor;
    private InetAddress bouncerAddress;
    private byte[] bouncerMac;
    private InetAddress serverAddress;
    private byte[] serverMac;
    private ICMPFactory icmpFactory;

    MyPacketRecevier(JpcapCaptor captor, InetAddress bouncerAddress, byte[] bouncerMac, InetAddress serverAddress, byte[] serverMac, ICMPFactory icmpFactory) {
        this.captor = captor;
        this.bouncerAddress = bouncerAddress;
        this.bouncerMac = bouncerMac;
        this.serverAddress = serverAddress;
        this.serverMac = serverMac;
        this.icmpFactory = icmpFactory;
    }

    public void receivePacket(Packet packet) {
//        System.out.println(packet);
        if (packet instanceof ICMPPacket) {
            ICMPPacket icmpIn = (ICMPPacket) packet;
            System.out.println("icmp_echo -> " + icmpIn);
            switch (icmpIn.type) {
                case ICMPPacket.ICMP_ECHO:
                    // coming from client
                    try {
                        captor.getJpcapSenderInstance().sendPacket(icmpFactory.toServer(icmpIn));
                    } catch (WrongInputPacketException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    break;
                case ICMPPacket.ICMP_ECHOREPLY:
                    // coming from server
                    try {
                        IPPacket ipPacket = icmpFactory.toClient(icmpIn);
                        captor.getJpcapSenderInstance().sendPacket(ipPacket);
                    } catch (WrongInputPacketException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    break;
            }
        }
    }
}
