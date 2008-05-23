package tslab;

import jpcap.NetworkInterface;
import jpcap.JpcapSender;
import jpcap.JpcapCaptor;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.EthernetPacket;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li and Sike Huang
 * Date: May 17, 2008
 * Time: 10:46:32 PM
 */
public class SendICMP {
    public static void main(String[] args) throws java.io.IOException {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        if (args.length < 1) {
            System.out.println("Usage: java SentICMP <device index (e.g., 0, 1..)>");
            for (int i = 0; i < devices.length; i++)
                System.out.println(i + ":" + devices[i].name + "(" + devices[i].description + ")");
            System.exit(0);
        }
        int index = Integer.parseInt(args[0]);
        JpcapSender sender = JpcapSender.openDevice(devices[index]);

        ICMPPacket p = new ICMPPacket();
        p.type = ICMPPacket.ICMP_TSTAMP;
        p.seq = 1000;
        p.id = 999;
        p.orig_timestamp = 123;
        p.trans_timestamp = 456;
        p.recv_timestamp = 789;
        p.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_ICMP,
                InetAddress.getByName("10.8.0.53"), InetAddress.getByName("10.8.0.50"));
        p.data = "data".getBytes();

        EthernetPacket ether = new EthernetPacket();
        ether.frametype = EthernetPacket.ETHERTYPE_IP;
        //set source and destination MAC addresses
        ether.src_mac = new byte[]{(byte) 0x82, (byte) 0x95, (byte) 0xEC, (byte) 0xAA, (byte) 0xFE, (byte) 0x0D};
        ether.dst_mac = new byte[]{(byte) 0x00, (byte) 0xff, (byte) 0x24, (byte) 0x23, (byte) 0x47, (byte) 0x19};
        p.datalink = ether;

        for(int i=0;i<3;i++)
        sender.sendPacket(p);
        sender.close();
    }


}
