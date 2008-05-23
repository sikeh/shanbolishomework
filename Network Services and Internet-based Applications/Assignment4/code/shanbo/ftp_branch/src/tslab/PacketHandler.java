package tslab;

import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.*;

import java.net.InetAddress;

import tslab.factory.ICMPFactory;
import tslab.factory.PacketFactory;
import tslab.factory.TCPFactory;
import tslab.factory.FTPDataPacketFactory;
import tslab.exception.WrongInputPacketException;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * Date: May 17, 2008
 * Time: 4:08:34 PM
 *
 * @author Sike Huang and Shanbo Li
 */
public class PacketHandler {
    private JpcapCaptor captor;
    private InetAddress bouncerAddress;
    private byte[] bouncerMac;
    private InetAddress serverAddress;
    private byte[] serverMac;
    private ICMPFactory icmpFactory;
    private TCPFactory tcpFactory;
    private FTPDataPacketFactory ftpFactory;

    public PacketHandler(JpcapCaptor captor, InetAddress bouncerAddress, byte[] bouncerMac, InetAddress serverAddress, byte[] serverMac) {
        this.captor = captor;
        this.bouncerAddress = bouncerAddress;
        this.bouncerMac = bouncerMac;
        this.serverAddress = serverAddress;
        this.serverMac = serverMac;
        icmpFactory = ICMPFactory.getInstance();
        icmpFactory.initial(serverAddress, serverMac);
        tcpFactory = TCPFactory.getInstance();
        tcpFactory.initial(serverAddress, serverMac);
        ftpFactory = FTPDataPacketFactory.getInstance();
        ftpFactory.initial(serverAddress, serverMac);


    }

    public void receive() {
        captor.loopPacket(-1, new MyPacketHandler(captor, bouncerAddress, bouncerMac, serverAddress, serverMac, icmpFactory, tcpFactory, ftpFactory));
    }
}

class MyPacketHandler implements PacketReceiver {
    private JpcapCaptor captor;
    private InetAddress bouncerAddress;
    private byte[] bouncerMac;
    private InetAddress serverAddress;
    private byte[] serverMac;
    private ICMPFactory icmpFactory;
    private TCPFactory tcpFactory;
    private FTPDataPacketFactory ftpFactory;
    private int tcpDstPort;

    MyPacketHandler(JpcapCaptor captor, InetAddress bouncerAddress, byte[] bouncerMac, InetAddress serverAddress, byte[] serverMac, ICMPFactory icmpFactory, TCPFactory tcpFactory, FTPDataPacketFactory ftpFactory) {
        this.captor = captor;
        this.bouncerAddress = bouncerAddress;
        this.bouncerMac = bouncerMac;
        this.serverAddress = serverAddress;
        this.serverMac = serverMac;
        this.icmpFactory = icmpFactory;
        this.tcpFactory = tcpFactory;
        this.ftpFactory = ftpFactory;
    }

    public void receivePacket(Packet packet) {
//        System.out.println(packet);
        PacketFactory factory = null;
        String packetType = null;
        if (packet instanceof ICMPPacket) {
            packetType = "icmp -> ";
            factory = icmpFactory;
        } else if (packet instanceof TCPPacket) {
            packetType = "tcp -> ";
            tcpDstPort = ((TCPPacket) packet).dst_port;
            if ((tcpDstPort >= TCPFactory.INITIAL_TCP_PORT && tcpDstPort <= TCPFactory.tcpDataPortCounter) || tcpDstPort == 20) {
                factory = ftpFactory;
            } else {
                factory = tcpFactory;
            }
        } else {
            return;
        }

        if (packet instanceof IPPacket) {
            try {
                System.out.println(packetType + packet);
                IPPacket ipPacket = factory.createPacket((IPPacket) packet);
                System.out.println(packetType + ipPacket);
                captor.getJpcapSenderInstance().sendPacket(ipPacket);
            } catch (WrongInputPacketException e) {
                e.printStackTrace();
            }
        }


    }
}
