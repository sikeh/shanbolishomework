package tslab;

import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;
import jpcap.packet.IPPacket;

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
        captor.loopPacket(-1, new MyPacketRecevier());
    }
}

class MyPacketRecevier implements PacketReceiver {

    public MyPacketRecevier() {
        //
    }

    public void receivePacket(Packet packet) {
        IPPacket ipPacket = (IPPacket) packet;
        System.out.println(ipPacket);
    }
}
