package tslab.util;

import jpcap.NetworkInterface;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterfaceAddress;
import jpcap.JpcapSender;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.util.Arrays;


import com.sun.snoop.TCPHeader;
import com.sun.snoop.SnoopException;


/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 11:07:52 PM
 */
public class Tools {
    private static byte[] header;


    public static boolean validateIPPacket(IPPacket ipPacket) {
//        The packet length reported by the Link Layer must be large enough to hold the minimum length legal IP datagram (20 bytes).
        try {
            if (ipPacket.length < 20) return false;
//        The IP version number must be 4. If the version number is not 4 then the packet may be another version of IP, such as IPng or ST-II.
            if (ipPacket.version != ((byte) 4)) return false;

            header = ipPacket.header;
//        IPv4Header ipv4Header = IPv4Header.decodeIPv4Header(header, 0);

            int originalChecksum1 = header[10];
            int originalChecksum2 = header[11];
            header[10] = 0x0;
            header[11] = 0x0;
            CheckCRC16 crc = new CheckCRC16();
            crc.crc16(header);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean validateTCPPacket(TCPPacket tcpPacket) {
        try {
            TCPHeader tcpHeader = null;
            try {
                tcpHeader = TCPHeader.decodeTCPHeader(tcpPacket.header, 43);
            } catch (SnoopException e) {
                return false;
            }

            CheckCRC16 crc = new CheckCRC16();
            crc.crc16(tcpHeader.getHeader());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * ARP , use this find server mac
     *
     * @param ip
     * @return MAC
     * @throws java.io.IOException
     */
    public static byte[] arp(String ip) throws java.io.IOException {
        return arp(InetAddress.getByName(ip));
    }

    /**
     * ARP, use this find server mac
     *
     * @param ip
     * @return MAC
     * @throws java.io.IOException
     */
    public static byte[] arp(InetAddress ip) throws java.io.IOException {
        //find network interface
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        NetworkInterface device = null;

        loop:
        for (NetworkInterface d : devices) {
            for (NetworkInterfaceAddress addr : d.addresses) {
                if (!(addr.address instanceof Inet4Address)) continue;
                byte[] bip = ip.getAddress();
                byte[] subnet = addr.subnet.getAddress();
                byte[] bif = addr.address.getAddress();
                for (int i = 0; i < 4; i++) {
                    bip[i] = (byte) (bip[i] & subnet[i]);
                    bif[i] = (byte) (bif[i] & subnet[i]);
                }
                if (Arrays.equals(bip, bif)) {
                    device = d;
                    break loop;
                }
            }
        }

        if (device == null)
            throw new IllegalArgumentException(ip + " is not a local address");

        //open Jpcap
        JpcapCaptor captor = JpcapCaptor.openDevice(device, 2000, false, 3000);
        captor.setFilter("arp", true);
        JpcapSender sender = captor.getJpcapSenderInstance();

        InetAddress srcip = null;
        for (NetworkInterfaceAddress addr : device.addresses)
            if (addr.address instanceof Inet4Address) {
                srcip = addr.address;
                break;
            }

        byte[] broadcast = new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255};
        ARPPacket arp = new ARPPacket();
        arp.hardtype = ARPPacket.HARDTYPE_ETHER;
        arp.prototype = ARPPacket.PROTOTYPE_IP;
        arp.operation = ARPPacket.ARP_REQUEST;
        arp.hlen = 6;
        arp.plen = 4;
        arp.sender_hardaddr = device.mac_address;
        arp.sender_protoaddr = srcip.getAddress();
        arp.target_hardaddr = broadcast;
        arp.target_protoaddr = ip.getAddress();

        EthernetPacket ether = new EthernetPacket();
        ether.frametype = EthernetPacket.ETHERTYPE_ARP;
        ether.src_mac = device.mac_address;
        ether.dst_mac = broadcast;
        arp.datalink = ether;

        sender.sendPacket(arp);

        while (true) {
            ARPPacket p = (ARPPacket) captor.getPacket();
            if (p == null) {
                throw new IllegalArgumentException(ip + " is not a local address");
            }
            if (Arrays.equals(p.target_protoaddr, srcip.getAddress())) {
                return p.sender_hardaddr;
            }
        }
    }

    class CRC16 {

    }
}
