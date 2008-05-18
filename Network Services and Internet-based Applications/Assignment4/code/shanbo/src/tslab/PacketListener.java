package tslab;

import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.*;

import java.net.InetAddress;

import tslab.factory.ICMPFactory;
import tslab.factory.PacketFactory;
import tslab.factory.TCPFactory;
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
    private InetAddress bouncerAddress;
    private byte[] bouncerMac;
    private InetAddress serverAddress;
    private byte[] serverMac;
    private ICMPFactory icmpFactory;
    private TCPFactory tcpFactory;

    public PacketListener(JpcapCaptor captor, InetAddress bouncerAddress, byte[] bouncerMac, InetAddress serverAddress, byte[] serverMac) {
        this.captor = captor;
        this.bouncerAddress = bouncerAddress;
        this.bouncerMac = bouncerMac;
        this.serverAddress = serverAddress;
        this.serverMac = serverMac;
        icmpFactory = ICMPFactory.getInstance();
        icmpFactory.initial(serverAddress, serverMac);
        tcpFactory = TCPFactory.getInstance();
        tcpFactory.initial(serverAddress, serverMac);
    }

    public void receive() {
        captor.loopPacket(-1, new MyPacketRecevier(captor, bouncerAddress, bouncerMac, serverAddress, serverMac, icmpFactory, tcpFactory));
    }
}

class MyPacketRecevier implements PacketReceiver {
    private JpcapCaptor captor;
    private InetAddress bouncerAddress;
    private byte[] bouncerMac;
    private InetAddress serverAddress;
    private byte[] serverMac;
    private ICMPFactory icmpFactory;
    private TCPFactory tcpFactory;

    MyPacketRecevier(JpcapCaptor captor, InetAddress bouncerAddress, byte[] bouncerMac, InetAddress serverAddress, byte[] serverMac, ICMPFactory icmpFactory, TCPFactory tcpFactory) {
        this.captor = captor;
        this.bouncerAddress = bouncerAddress;
        this.bouncerMac = bouncerMac;
        this.serverAddress = serverAddress;
        this.serverMac = serverMac;
        this.icmpFactory = icmpFactory;
        this.tcpFactory = tcpFactory;
    }

    public void receivePacket(Packet packet) {
//        System.out.println(packet);
        PacketFactory factory = null;
        if (packet instanceof ICMPPacket) {
            factory = icmpFactory;
        } else if (packet instanceof TCPPacket) {
            factory = tcpFactory;
        } else {

        }

        if (packet instanceof IPPacket) {
            try {
                IPPacket ipPacket = factory.toServer((IPPacket) packet);
                captor.getJpcapSenderInstance().sendPacket(ipPacket);
            } catch (WrongInputPacketException e) {
                e.printStackTrace();
            }
        }


    }
}
