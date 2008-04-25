package assignment2.sip;


import assignment2.audio.RtpTransmit;

import javax.media.MediaLocator;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * User: Shanbo Li
 * Date: Apr 13, 2008
 * Time: 2:59:40 PM
 *
 * @author Shanbo Li
 */
public class SipServer {
    public static final Logger logger = Logger.getLogger(SipServer.class.getName());

    public static void main(String[] args) {
        String incomingSIP;
        String responseOkForInvite;
        MessageType incomingMessageType;


        try {
            int port = 5088;

            DatagramSocket dsocket = new DatagramSocket(port);
//            byte[] buffer = new byte[2048];
            byte[] buffer = new byte[9999];

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                // Wait to receive a datagram
                logger.info("Listening UDP at " + port + " ...");
                dsocket.receive(packet);

                // Convert the contents to a string, and display them
                logger.info("Get a SIP message.");
                incomingSIP = new String(buffer, 0, packet.getLength());
                incomingMessageType = SipParser.getMessageType(incomingSIP);
                if (incomingMessageType.equals(MessageType.INVITE)) {
                    break;
                } else {
                    logger.info("Get a message, Type is " + incomingMessageType);
                }
            }
            logger.info("Incoming SIP is \n" + incomingSIP);

            SipFactory sipFactory = new SipFactory(incomingSIP, 1124);
            SipParser sipParser = new SipParser(incomingSIP);
            String remoteSIPIP = sipParser.getRemoteSIPIp();
            int remoteSIPPort = sipParser.getRemoteSIPPort();
            String remoteRTPPort = Communication.getRemoteRTPPort(incomingSIP);
            logger.info("Generating response SIP ...");
            responseOkForInvite = sipFactory.getOkForInvite();
            String bye = sipFactory.getBye();
            String notFound = sipFactory.getNotFound();



            // SIP/2.0 200 OK
            logger.info("Sending response SIP to " + remoteSIPIP + ":" + remoteSIPPort);
            Communication.sendSIP(notFound, remoteSIPIP, remoteSIPPort);
            Communication.sendSIP(responseOkForInvite, remoteSIPIP, remoteSIPPort);

            // TODO sikeh: wait for ACK from client, then establish RTP
//            TimeUnit.MILLISECONDS.sleep(50);


            logger.info("RTP to " + remoteSIPIP + ":" + remoteRTPPort);


            try {
                Thread.currentThread().sleep(6000);
            } catch (InterruptedException ie) {
            }
            Communication.sendSIP(bye, remoteSIPIP, remoteSIPPort);


            // Stop the transmission
//            rt.stop();


        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }

    }
}
