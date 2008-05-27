package tslab.factory;

import jpcap.packet.*;
import tslab.exception.WrongInputPacketException;
import tslab.util.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li and Sike Huang
 * Date: May 17, 2008
 * Time: 9:30:36 PM
 *
 * @author Shanbo Li and Sike Huang
 */
public class TCPFactory extends PacketFactory {

    public static List<FTPDataMapping1> ftpDataSessions1 = new ArrayList<FTPDataMapping1>();
    public static List<FTPDataMapping3> ftpDataSessions3 = new ArrayList<FTPDataMapping3>();

    //TODO change the fields name there
    private static final int INITIAL_BOUNCER_TO_SERVER_TCP_PORT = 11240;
    private static int bouncerToServerPortCounter = 11240;
    public static final int INITIAL_BOUNCER_TO_SERVER_FTP_DATA_PORT = 8598;
    public static int tcpDataPortCounter = 8598;
    List<TCPMapping1> sessions1 = new ArrayList<TCPMapping1>();
    List<TCPMapping3> sessions3 = new ArrayList<TCPMapping3>();
    List<FTPCommandMapping> ftpPortSessionCommand = new ArrayList<FTPCommandMapping>();
    private final static TCPFactory instance = new TCPFactory();
    private String ipInString;
    private String portCommand;
    private FTPCommandMapping ftpCommandMapping;
    private TCPMapping1 newMapping1;
    private TCPMapping3 newMapping3;

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

        if (tcpIn.dst_port >= INITIAL_BOUNCER_TO_SERVER_TCP_PORT && tcpIn.dst_port <= bouncerToServerPortCounter) {
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
            throw new WrongInputPacketException("Not a TCP packet.\nPlease check if the incoming packet is the correct type.");
        }

        int bouncerToServerPort;
        long newSeq = tcpIn.sequence;

        int serverPortInPractice = (serverPort < 0) ? tcpIn.dst_port : serverPort;

        TCPMapping1 mapping = new TCPMapping1(tcpIn.src_ip, tcpIn.src_port, serverPortInPractice);
        if (!sessions1.contains(mapping)) {
            bouncerToServerPort = bouncerToServerPortCounter++;
        } else {
            bouncerToServerPort = sessions1.get(sessions1.indexOf(mapping)).getBouncerPortToServer();
            TCPMapping1 mapInDatabase = sessions1.get(sessions1.indexOf(mapping));
            if (mapInDatabase.isNeedAdjust()) {
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

        newMapping1 = new TCPMapping1(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port);
        newMapping3 = new TCPMapping3(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port);

        if (data != null && data.length >= 4) {
            if (data[0] == 0x50 && data[1] == 0x4f && data[2] == 0x52 && data[3] == 0x54) {
                long correctAck = data.length + tcpOut.sequence;
                int oriPort = 0;
                try {
                    String[] oriPortCommand = new String(data).split(",");
                    int high = Integer.valueOf(oriPortCommand[oriPortCommand.length - 2]);
                    int low = Integer.valueOf(oriPortCommand[oriPortCommand.length - 1].replaceAll("\r\n", ""));
                    oriPort = high * 256 + low;
                } catch (NumberFormatException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                ipInString = tcpIn.dst_ip.toString().split("/")[1];
                tcpDataPortCounter++;
                portCommand = new String("PORT " + ipInString.replaceAll("\\.", ",") + "," + (tcpDataPortCounter / 256) + "," + (tcpDataPortCounter % 256) + "\r\n");
                System.out.println("");
                System.out.println(portCommand);
                System.out.println("");
                tcpOut.data = portCommand.getBytes();
                long wrongAck = tcpOut.data.length + tcpOut.sequence;
                int interval = tcpOut.data.length - data.length;
                ftpCommandMapping = new FTPCommandMapping(wrongAck, correctAck);
                ftpPortSessionCommand.add(ftpCommandMapping);
                newMapping1 = new TCPMapping1(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port);
                newMapping3 = new TCPMapping3(tcpIn.src_ip, ethIn.src_mac, tcpIn.src_port, tcpIn.dst_port, tcpOut.src_port, tcpOut.dst_ip, tcpOut.dst_port);
                TCPMapping1 record1 = sessions1.get(sessions1.indexOf(newMapping1));
                TCPMapping3 record3 = sessions3.get(sessions3.indexOf(newMapping3));
                ftpDataSessions1.add(new FTPDataMapping1(tcpIn.src_ip, ethIn.src_mac, oriPort, tcpDataPortCounter));
                ftpDataSessions3.add(new FTPDataMapping3(tcpIn.src_ip, ethIn.src_mac, oriPort, tcpDataPortCounter));
                record1.setNeedAdjust(true);
                record1.setInterval(interval + record1.getInterval());
                record3.setNeedAdjust(true);
                record3.setInterval(interval + record3.getInterval());
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
            throw new WrongInputPacketException("Not a TCP packet.\nPlease check if the incoming packet is the correct type.");
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
