package tslab.factory;

import jpcap.packet.*;
import tslab.exception.WrongInputPacketException;
import tslab.util.TCPMapping;
import tslab.util.TCPMapping1;
import tslab.util.TCPMapping3;
import tslab.util.FTPMapping;

import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 9:30:36 PM
 */
public class TCPFactory extends PacketFactory {


    private static final int INITIAL_SOURCE_PORT = 11240;
    private static int bouncerToServerPortCounter = 11240;
    List<TCPMapping1> sessions1 = new ArrayList<TCPMapping1>();
    List<TCPMapping3> sessions3 = new ArrayList<TCPMapping3>();
    List<FTPMapping> ftpPortSession = new ArrayList<FTPMapping>();
    private final static TCPFactory instance = new TCPFactory();
    private String ipInString;
    private String portCommand;
    private FTPMapping ftpMapping;

    private TCPFactory() {
    }

    /**
     * Get a instance of TCPFactory, pass serverAddress and serverMac to it
     */
    public static TCPFactory getInstance() {
        return instance;
    }

    /**
     * Create a packet according incomming packet
     *
     * @param ipPacket
     * @return new packet for sending
     * @throws WrongInputPacketException
     */
    public IPPacket createPacket(IPPacket ipPacket) throws WrongInputPacketException {
        TCPPacket tcpIn;
        if (ipPacket instanceof TCPPacket) {
            tcpIn = (TCPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet.\nPlease check if the incoming packet is the correct type.");
        }

        if (tcpIn.dst_port >= INITIAL_SOURCE_PORT && tcpIn.dst_port <= bouncerToServerPortCounter) {
            return toClient(ipPacket);
        } else {
            return toServer(ipPacket);
        }
    }

    //TODO ADJUST THE ACK EVERY TIME

    public IPPacket toServer(IPPacket ipPacket) throws WrongInputPacketException {
        //bouncer's source port for server

        TCPPacket tcpIn;

        if (ipPacket instanceof TCPPacket) {
            tcpIn = (TCPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet.\nPlease check if the incoming packet is the correct type.");
        }

        int bouncerToServerPort;
        TCPMapping1 mapping = new TCPMapping1(tcpIn.src_ip, tcpIn.src_port, tcpIn.dst_port);
        if (!sessions1.contains(mapping)) {
            bouncerToServerPort = bouncerToServerPortCounter++;
        } else {
            bouncerToServerPort = sessions1.get(sessions1.indexOf(mapping)).getBouncerPortToServer();
        }


        int serverPortInPractice = (serverPort < 0) ? tcpIn.dst_port : serverPort;

        TCPPacket tcpOut = new TCPPacket(bouncerToServerPort, serverPortInPractice, tcpIn.sequence, tcpIn.ack_num, tcpIn.urg,
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

        byte[] data = tcpOut.data;


        if (data != null && data.length >= 4) {
            if (data[0] == 0x50 && data[1] == 0x4f && data[2] == 0x52 && data[3] == 0x54) {
                long correctAck = data.length + tcpOut.sequence;
                ipInString = tcpIn.dst_ip.toString().split("/")[1];
                portCommand = new String("PORT " + ipInString.replaceAll("\\.", ",") + "," + (tcpOut.src_port / 256) + "," + (tcpOut.src_port % 256) + "\r\n");
                System.out.println("Port Command = " + portCommand);
                tcpOut.data = portCommand.getBytes();
                long wrongAck = tcpOut.data.length + tcpOut.sequence;
                ftpMapping = new FTPMapping(wrongAck, correctAck);
                if (!ftpPortSession.contains(ftpMapping)) {
                    ftpPortSession.add(ftpMapping);
                }
            }
        }
        sessions1.add(new TCPMapping1(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port));
        sessions3.add(new TCPMapping3(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port));

        return tcpOut;
    }


    public IPPacket toClient(IPPacket ipPacket) throws WrongInputPacketException {
        TCPPacket tcpIn;
        if (ipPacket instanceof TCPPacket) {
            tcpIn = (TCPPacket) ipPacket;
        } else {
            throw new WrongInputPacketException("Not a ICMP packet.\nPlease check if the incoming packet is the correct type.");
        }


        TCPMapping3 mapping3 = new TCPMapping3(tcpIn.dst_port);
        if (!sessions3.contains(mapping3)) {
            throw new WrongInputPacketException("Can not produce a packet according incoming packet.\nNo record found in session.\n");
        }
        TCPMapping record = sessions3.get(sessions3.indexOf(mapping3));

        TCPPacket tcpOut = new TCPPacket(record.getBouncerPortToClient(), record.getClientPort(), tcpIn.sequence, tcpIn.ack_num, tcpIn.urg,
                tcpIn.ack, tcpIn.psh, tcpIn.rst, tcpIn.syn, tcpIn.fin, tcpIn.rsv1, tcpIn.rsv2, tcpIn.window, tcpIn.urgent_pointer);

        //produce packet to server
        EthernetPacket ethIn = (EthernetPacket) tcpIn.datalink;
        tcpOut.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_TCP, tcpIn.dst_ip, record.getClientAddress());
        EthernetPacket ethOut = new EthernetPacket();
        ethOut.frametype = EthernetPacket.ETHERTYPE_IP;
        ethOut.src_mac = ethIn.dst_mac;
        ethOut.dst_mac = record.getClientMac();
        tcpOut.datalink = ethOut;
        tcpOut.data = tcpIn.data;
        byte[] data = tcpOut.data;
        if (data != null && data.length >= 3) {
            if (data[0] == 0x32 && data[1] == 0x30 && data[2] == 0x30) {
                FTPMapping correctMapping = ftpPortSession.get(ftpPortSession.indexOf(new FTPMapping(tcpOut.ack_num)));
                tcpOut.ack_num = correctMapping.getCorrectAck();
                ftpPortSession.remove(correctMapping);
            }
        }
        return tcpOut;
    }


}
