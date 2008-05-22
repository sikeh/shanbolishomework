package tslab.factory;

import jpcap.packet.*;
import tslab.exception.WrongInputPacketException;
import tslab.util.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 9:30:36 PM
 */
public class TCPFactory extends PacketFactory {


    private static final int INITIAL_TCP_PORT = 11240;
    private static int bouncerTCPPortCounter = 11240;
    private static final int INITIAL_FTP_PORT = 8598;
    private static int bouncerFTPCounter = 8598;
    List<TCPMapping1> sessions1 = new ArrayList<TCPMapping1>();
    List<TCPMapping3> sessions3 = new ArrayList<TCPMapping3>();
    List<FTPMapping> ftptcpMappings = new ArrayList<FTPMapping>();
    private final static TCPFactory instance = new TCPFactory();
    private String ipInString;
    private String portCommand;
    private FTPMapping ftpMapping;
    private TCPMapping1 newMapping1;
    private TCPMapping3 newMapping3;
    private FTPMapping client;

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

        if (tcpIn.dst_port >= INITIAL_TCP_PORT && tcpIn.dst_port <= bouncerTCPPortCounter) {
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
        FTPMapping tempFTPMapping = new FTPMapping(tcpIn.dst_port);
        //Use for redirect server port
        int serverPortInPractice = (serverPort < 0) ? tcpIn.dst_port : serverPort;
        int bouncerToServerPort;
        long newSeq = tcpIn.sequence;
        TCPMapping1 mapping = new TCPMapping1(tcpIn.src_ip, tcpIn.src_port, tcpIn.dst_port);
        if (!sessions1.contains(mapping)) {
            bouncerToServerPort = bouncerTCPPortCounter++;
        } else {
            bouncerToServerPort = sessions1.get(sessions1.indexOf(mapping)).getBouncerPortToServer();
            TCPMapping1 mapInDatabase = sessions1.get(sessions1.indexOf(mapping));
            if (mapInDatabase.isNeedAdjust()) {
                //TODO    +/-
                newSeq = newSeq + mapInDatabase.getInterval();
            }
        }
        TCPPacket tcpOut = new TCPPacket(bouncerToServerPort, serverPortInPractice, newSeq, tcpIn.ack_num, tcpIn.urg,
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

        if (ftptcpMappings.contains(tempFTPMapping)) {
            client = ftptcpMappings.get(ftptcpMappings.indexOf(tempFTPMapping));
            tcpOut.dst_port = client.getClientPort();
            ((EthernetPacket) tcpOut.datalink).dst_mac = client.getClientMac();
            tcpOut.dst_ip = client.getClientIP();
            newMapping1 = new TCPMapping1(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port);
            newMapping3 = new TCPMapping3(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port);
            if (!sessions1.contains(newMapping1)) {
                sessions1.add(newMapping1);
                sessions3.add(newMapping3);
            }
            return tcpOut;
        }

        newMapping1 = new TCPMapping1(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port);
        newMapping3 = new TCPMapping3(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port);

        if (data != null && data.length >= 4) {
            if (data[0] == 0x50 && data[1] == 0x4f && data[2] == 0x52 && data[3] == 0x54) {
                String[] oriPortCommand = new String(data).split(",");
                int oriPort = 0;
                try {
                    int high = Integer.valueOf(oriPortCommand[oriPortCommand.length - 2]);
                    int low = Integer.valueOf(oriPortCommand[oriPortCommand.length - 1].replaceAll("\r\n", ""));
                    oriPort = high * 256 + low;
                } catch (NumberFormatException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                ipInString = tcpIn.dst_ip.toString().split("/")[1];
                portCommand = new String("PORT " + ipInString.replaceAll("\\.", ",") + "," + (bouncerFTPCounter / 256) + "," + (bouncerFTPCounter % 256) + "\r\n");
                System.out.println("Port Command = " + portCommand);
                tcpOut.data = portCommand.getBytes();
                long wrongAck = tcpOut.data.length + tcpOut.sequence;
                int interval = tcpOut.data.length - data.length;
                ftpMapping = new FTPMapping(oriPort, tcpIn.src_ip, ethIn.src_mac, bouncerFTPCounter++);
                if (!ftptcpMappings.contains(ftpMapping)) {
                    ftptcpMappings.add(ftpMapping);
                    TCPMapping1 record1 = sessions1.get(sessions1.indexOf(newMapping1));
                    TCPMapping3 record3 = sessions3.get(sessions3.indexOf(newMapping3));
                    record1.setNeedAdjust(true);
                    record1.setInterval(interval);
                    record3.setNeedAdjust(true);
                    record3.setInterval(interval);
                }
            }
        }

        if (!sessions1.contains(newMapping1)) {
            sessions1.add(newMapping1);
            sessions3.add(newMapping3);
        }

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

        long newAck = tcpIn.ack_num;
        if (record.isNeedAdjust()) {
            newAck = newAck - record.getInterval();
        }

        TCPPacket tcpOut = new TCPPacket(record.getBouncerPortToClient(), record.getClientPort(), tcpIn.sequence, newAck, tcpIn.urg,
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
        return tcpOut;
    }


}
