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
import tslab.exception.ValidationFailedException;
import tslab.util.Tools;

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
    private ICMPFactory icmpFactory;
    private TCPFactory tcpFactory;
    private FTPDataPacketFactory ftpFactory;

    public PacketHandler(JpcapCaptor captor, InetAddress serverAddress, byte[] serverMac, int serverPort) {
        this.captor = captor;
        icmpFactory = ICMPFactory.getInstance();
        // the initial value of serverPort is -1
        // which means it has not be set
        // the factory will check it upon
        icmpFactory.initial(serverAddress, serverMac);
        tcpFactory = TCPFactory.getInstance();
        tcpFactory.initial(serverAddress, serverPort, serverMac);
        ftpFactory = FTPDataPacketFactory.getInstance();
        ftpFactory.initial(serverAddress, serverPort, serverMac);


    }

    public void receive() {
        captor.loopPacket(-1, new MyPacketHandler(captor, icmpFactory, tcpFactory, ftpFactory));
    }
}

class MyPacketHandler implements PacketReceiver {
    private JpcapCaptor captor;
    private ICMPFactory icmpFactory;
    private TCPFactory tcpFactory;
    private FTPDataPacketFactory ftpFactory;

    MyPacketHandler(JpcapCaptor captor, ICMPFactory icmpFactory, TCPFactory tcpFactory, FTPDataPacketFactory ftpFactory) {
        this.captor = captor;
        this.icmpFactory = icmpFactory;
        this.tcpFactory = tcpFactory;
        this.ftpFactory = ftpFactory;
    }

    public void receivePacket(Packet packet) {
        PacketFactory factory = null;
        String packetType = null;

        //validate ip packet
        if (packet instanceof IPPacket) {
            try {
                Tools.validateIPPacket((IPPacket) packet);
            } catch (ValidationFailedException e) {
                //TODO change to print message and return
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else{
            return;
        }


        if (packet instanceof ICMPPacket) {
            //validate IP packet

            packetType = "icmp -> ";
            factory = icmpFactory;
        } else if (packet instanceof TCPPacket) {
            //validate TCP packet
            try {
                Tools.validateTCPPacket((TCPPacket) packet);
            } catch (ValidationFailedException e) {
                //TODO change to print message and return                
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            packetType = "tcp -> ";
            if (isFtpData(packet)) {
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
                System.out.println(e.getMessage());
            }
        }


    }

    private boolean isFtpData(Packet packet) {
        return FTPDataPacketFactory.isFtpDataPacket((TCPPacket) packet);
    }


}
