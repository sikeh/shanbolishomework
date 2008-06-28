package tslab.factory;

import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;
import jpcap.packet.EthernetPacket;
import tslab.exception.WrongInputPacketException;
import static tslab.factory.TCPFactory.*;
import tslab.util.FTPDataMapping1;
import tslab.util.FTPDataMapping3;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li and Sike Huang
 * Date: May 17, 2008
 * Time: 9:30:59 PM
 *
 * @author Shanbo Li and Sike Huang
 */
public class FTPDataPacketFactory extends PacketFactory {

    private static FTPDataPacketFactory instance = new FTPDataPacketFactory();


    private FTPDataPacketFactory() {
    }

    /**
     * Get a instance of TCPFactory, pass serverAddress and serverMac to it
     */
    public static FTPDataPacketFactory getInstance() {
        return instance;
    }

    public static boolean isFtpDataPacket(TCPPacket packet) {
        FTPDataMapping1 mapping1 = new FTPDataMapping1(packet.dst_port);
        FTPDataMapping3 mapping3 = new FTPDataMapping3(packet.src_ip, packet.src_port);
        return ((ftpDataSessions1.contains(mapping1) || ftpDataSessions3.contains(mapping3)));
    }


    public IPPacket createPacket(IPPacket ipPacket) throws WrongInputPacketException {
        TCPPacket tcpIn = null;
        if (ipPacket instanceof TCPPacket) {
            tcpIn = (TCPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet.\nPlease check if the incoming packet is the correct type.");
        }
        int tcpDstPort = tcpIn.dst_port;
        if ((tcpDstPort >= INITIAL_BOUNCER_TO_SERVER_FTP_DATA_PORT && tcpDstPort <= tcpDataPortCounter)) {
            return toClient(ipPacket);
        } else {
            return toServer(ipPacket);
        }
    }

    /**
     * FTP Server to client
     *
     * @param ipPacket
     * @return
     * @throws WrongInputPacketException
     */
    public IPPacket toClient(IPPacket ipPacket) throws WrongInputPacketException {
        TCPPacket tcpIn;
        if (ipPacket instanceof TCPPacket) {
            tcpIn = (TCPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a TCP packet.\nPlease check if the incoming packet is the correct type.");
        }

        FTPDataMapping1 mapping1 = new FTPDataMapping1(tcpIn.dst_port);

        if (!ftpDataSessions1.contains(mapping1)) {
            throw new WrongInputPacketException("I got a ftp data packate, but it is not in the database!");
        }
        FTPDataMapping1 record = ftpDataSessions1.get(ftpDataSessions1.indexOf(mapping1));

        TCPPacket tcpOut = new TCPPacket(20, record.getClientPort(), tcpIn.sequence, tcpIn.ack_num, tcpIn.urg,
                tcpIn.ack, tcpIn.psh, tcpIn.rst, tcpIn.syn, tcpIn.fin, tcpIn.rsv1, tcpIn.rsv2, tcpIn.window, tcpIn.urgent_pointer);

//        TCPPacket tcpOut = new TCPPacket(tcpIn.src_port, record.getClientPort(), tcpIn.sequence, tcpIn.ack_num, tcpIn.urg,
//                      tcpIn.ack, tcpIn.psh, tcpIn.rst, tcpIn.syn, tcpIn.fin, tcpIn.rsv1, tcpIn.rsv2, tcpIn.window, tcpIn.urgent_pointer);

        //produce packet to server
        EthernetPacket ethIn = (EthernetPacket) tcpIn.datalink;
        tcpOut.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_TCP, tcpIn.dst_ip, record.getClientAddress());
        EthernetPacket ethOut = new EthernetPacket();
        ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
        ethOut.src_mac = ethIn.dst_mac;
        ethOut.dst_mac = record.getClientMac();
        tcpOut.datalink = ethOut;
        tcpOut.data = tcpIn.data;
        return tcpOut;
    }

    public IPPacket toServer(IPPacket ipPacket) throws WrongInputPacketException {
        TCPPacket tcpIn;
        if (ipPacket instanceof TCPPacket) {
            tcpIn = (TCPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a TCP packet.\nPlease check if the incoming packet is the correct type.");
        }

        FTPDataMapping3 mapping3 = new FTPDataMapping3(tcpIn.src_ip, tcpIn.src_port);

        if (!ftpDataSessions3.contains(mapping3)) {
            throw new WrongInputPacketException("I got a ftp data packate, but it is not in the database!");
        }
        FTPDataMapping3 record = ftpDataSessions3.get(ftpDataSessions3.indexOf(mapping3));

        TCPPacket tcpOut = new TCPPacket(record.getBouncerPortToFTPServer(), tcpIn.dst_port, tcpIn.sequence, tcpIn.ack_num, tcpIn.urg,
                tcpIn.ack, tcpIn.psh, tcpIn.rst, tcpIn.syn, tcpIn.fin, tcpIn.rsv1, tcpIn.rsv2, tcpIn.window, tcpIn.urgent_pointer);

        //produce packet to server
        EthernetPacket ethIn = (EthernetPacket) tcpIn.datalink;
        tcpOut.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_TCP, tcpIn.dst_ip, this.serverAddress);
        EthernetPacket ethOut = new EthernetPacket();
        ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
        ethOut.src_mac = ethIn.dst_mac;
        ethOut.dst_mac = this.serverMac;
        tcpOut.datalink = ethOut;
        tcpOut.data = tcpIn.data;
        return tcpOut;
    }
}
