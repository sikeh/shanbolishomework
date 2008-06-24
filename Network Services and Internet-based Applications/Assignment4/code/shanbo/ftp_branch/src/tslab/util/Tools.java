package tslab.util;

import jpcap.NetworkInterface;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterfaceAddress;
import jpcap.JpcapSender;
import jpcap.packet.*;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.util.Arrays;


import com.sun.snoop.TCPHeader;
import com.sun.snoop.SnoopException;
import com.sun.snoop.IPv4Header;
import tslab.exception.ValidationFailedException;
import org.savarese.vserv.tcpip.ICMPEchoPacket;
import org.opennms.protocols.icmp.ICMPHeader;
import org.opennms.protocols.ip.OC16ChecksumProducer;


/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 11:07:52 PM
 */
public class Tools {

    /**
     * Validate IP Packet
     *
     * @param ipPacket the ip packet
     * @throws ValidationFailedException validate failed.
     */
    public static void validateIPPacket(IPPacket ipPacket) throws ValidationFailedException {
        if (ipPacket == null) {
            throw new ValidationFailedException("ipPacket is null");
        }
//        The packet length reported by the Link Layer must be large enough to hold the minimum length legal IP datagram (20 bytes).
        if (ipPacket.length < 20) {
            throw new ValidationFailedException("Invalid length");
        }
//        The IP version number must be 4. If the version number is not 4 then the packet may be another version of IP, such as IPng or ST-II.
        if (ipPacket.version != ((byte) 4)) {
            throw new ValidationFailedException("Invalid IP version");
        }

        byte[] header = ipPacket.header;
        IPv4Header sunHeader = null;
        try {
            sunHeader = IPv4Header.decodeIPv4Header(header, 14);
        } catch (SnoopException e) {
            throw new ValidationFailedException(e.getMessage());
        }
        byte[] ipHeader = Arrays.copyOfRange(header, 14, header.length);
        org.savarese.vserv.tcpip.IPPacket p = new org.savarese.vserv.tcpip.IPPacket(ipHeader.length);
        try {
            p.setData(ipHeader);
        } catch (Exception e) {
            throw new ValidationFailedException("Invalid Packet");
        }

        int calculatedCheckSum = 0;
        try {
            calculatedCheckSum = p.computeIPChecksum();
        } catch (Exception e) {
            throw new ValidationFailedException("Invalid Packet");
        }

        if (calculatedCheckSum != sunHeader.getHeaderChecksum()) {
            throw new ValidationFailedException("Invalid Checksum");
        }
    }

    /**
     * validate TCP packet
     *
     * @param tcpPacket
     * @throws ValidationFailedException
     */
    public static void validateTCPPacket(TCPPacket tcpPacket) throws ValidationFailedException {
        if (tcpPacket == null) {
            throw new ValidationFailedException("Packet is null");
        }

        IPPacket ipPacket = tcpPacket;

        byte[] ipHeader = ipPacket.header;

        IPv4Header sunIpHeader = null;
        try {
            sunIpHeader = IPv4Header.decodeIPv4Header(ipHeader, 14);
        } catch (SnoopException e) {
            throw new ValidationFailedException(e.getMessage());
        }

        int sunIpLength = sunIpHeader.getIPHeaderLength();

        TCPHeader sunTcpHeader = null;
        try {
            sunTcpHeader = TCPHeader.decodeTCPHeader(ipHeader, 14 + sunIpLength);
        } catch (SnoopException e) {
            throw new ValidationFailedException(e.getMessage());
        }
        int originalTcpCheckSum = sunTcpHeader.getChecksum();
        org.savarese.vserv.tcpip.TCPPacket p = new org.savarese.vserv.tcpip.TCPPacket(1124);
        byte[] savareseIpHeader = Arrays.copyOfRange(ipHeader, 14, ipHeader.length);
        int calculatedChecksum = -1;
        try {
            p.setData(savareseIpHeader);
            calculatedChecksum = p.computeTCPChecksum();
        } catch (Exception e) {
            throw new ValidationFailedException("Invalid Packet");
        }

        if (originalTcpCheckSum != calculatedChecksum) {
            throw new ValidationFailedException("Invalid TCP checksum");
        }
    }

    /**
     * validate ICMP packet
     *
     * @param icmpPacket
     * @throws ValidationFailedException
     */
    public static void validateICMPPacket(ICMPPacket icmpPacket) throws ValidationFailedException {
        if (icmpPacket == null) {
            throw new ValidationFailedException("Packet is null");
        }

        byte[] ipHeader = icmpPacket.header;

        IPv4Header sunIpHeader = null;
        try {
            sunIpHeader = IPv4Header.decodeIPv4Header(ipHeader, 14);
        } catch (SnoopException e) {
            throw new ValidationFailedException(e.getMessage());
        }

        org.savarese.vserv.tcpip.ICMPEchoPacket p = new ICMPEchoPacket(24);
        byte[] savareseIpHeader = Arrays.copyOfRange(ipHeader, 14, ipHeader.length);
        int calculatedChecksum = -1;
        int originalTcpCheckSum = -2;

        byte[] icmpHeader = Arrays.copyOfRange(ipHeader, ipHeader.length - 8, ipHeader.length);


        ICMPHeader icmpHeader2 = new ICMPHeader(icmpHeader, 0);
        short beforeCalculate = icmpHeader2.getChecksum();
        short oppositeChecksum = (short) ~beforeCalculate;
        icmpHeader2.computeChecksum();
        icmpHeader2.getChecksum();

        byte[] data = icmpPacket.data;

        byte[] all = new byte[8 + data.length];
        try {
            System.arraycopy(icmpHeader, 0, all, 0, 8);
            System.arraycopy(data, 0, all, 8, data.length);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        try {
            p.setData(savareseIpHeader);
            originalTcpCheckSum = p.getICMPChecksum();
            int calculatedChecksum1 = computeChecksum(0, 2, 8, 0, false, all);
//            int calculatedChecksum2 = computeChecksum(0, 3, 8, 0, false, icmpHeader);
            int calculatedChecksum3 = computeChecksum(0, 4, 8, 0, false, all);
            CRC16 crc16 = new CRC16();
            crc16.update(icmpHeader, 0, icmpHeader.length);
            String calculatedChecksum4 = Long.toHexString(crc16.getValue());
            int calculatedChecksum5 = CheckCRC16.crc16(icmpHeader);
            int calculatedChecksum6 = icmpHeader2.getChecksum();
            int calculatedChecksum7 = crc16_v2(icmpHeader, data);
            int calculatedChecksum8 = crc16_v3(icmpHeader, data);
            int calculatedChecksum9 = crc16_v4(icmpHeader, data);
            int calculatedChecksum10 = crc16_v5(all);

            System.out.println("");
        } catch (Exception e) {
            throw new ValidationFailedException(e);
        }


        if (originalTcpCheckSum != calculatedChecksum) {
            //   throw new ValidationFailedException("Invalid ICMP checksum");
        }


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

    private static int computeChecksum(int startOffset,
                                       int checksumOffset,
                                       int length,
                                       int virtualHeaderTotal,
                                       boolean update, byte[] _data_) {
        int total = 0;
        int i = startOffset;
        int imax = checksumOffset;

        while (i < imax)
            total += (((_data_[i++] & 0xff) << 8) | (_data_[i++] & 0xff));

        // Skip existing checksum.
        i = checksumOffset + 2;

        imax = length - (length % 2);

        while (i < imax)
            total += (((_data_[i++] & 0xff) << 8) | (_data_[i++] & 0xff));

        if (i < length)
            total += ((_data_[i] & 0xff) << 8);

        total += virtualHeaderTotal;

        // Fold to 16 bits
        while ((total & 0xffff0000) != 0)
            total = (total & 0xffff) + (total >>> 16);

        total = (~total & 0xffff);

        if (update) {
            _data_[checksumOffset] = (byte) (total >> 8);
            _data_[checksumOffset + 1] = (byte) (total & 0xff);
        }

        return total;
    }

    private static int crc16_v3(byte[] h, byte[] datas) {
        int sum = 0;
        int i = 0;
        while (i < 8) {
            sum += (((h[i++] & 0xff) << 8) | (h[i++] & 0xff));
        }
        i = 0;
        while (i < datas.length) {
            sum += (((datas[i++] & 0xff) << 8) | (datas[i++] & 0xff));
        }
        return sum;
    }

    private static int crc16_v4(byte[] h, byte[] datas) {
        int sum = 0;
        int i = 0;
        while (i < 8) {
            sum += (((h[i++]) * 256) + (h[i++]));
        }
        i = 0;
        while (i < datas.length) {
            sum += (((datas[i++]) * 256) + (datas[i++]));
        }
        return sum;
    }

    private static int crc16_v5(byte[] datas) {
        int sum = 0;
        int i = 0;
        while (i < datas.length) {
            sum += datas[i] * 256;
            i++;
            sum += datas[i];
            i++;
        }
        return sum;
    }


    private static short crc16_v2(byte[] h, byte[] datas) {
        OC16ChecksumProducer summer = new OC16ChecksumProducer();

        summer.add(h[0], h[1]);
        summer.add(h[4], h[5]);
        summer.add(h[6], h[7]);

        for (byte data : datas) {
            summer.add(data);
        }
        return summer.getChecksum();
    }

}
