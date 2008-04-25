package assignment2.sip;


import assignment2.audio.AVTransmit2;

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

        try {
            int port = 5088;

            // Create a socket to listen on the port.
            DatagramSocket dsocket = new DatagramSocket(port);

            // Create a buffer to read datagrams into. If a
            // packet is larger than this buffer, the
            // excess will simply be discarded!
//            byte[] buffer = new byte[2048];
            byte[] buffer = new byte[9999];

            // Create a packet to receive data into the buffer
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Now loop forever, waiting to receive packets and printing them.
            while (true) {
                // Wait to receive a datagram
                logger.info("Listening UDP at " + port + " ...");
                dsocket.receive(packet);

                // Convert the contents to a string, and display them
                logger.info("Got SIP");
                incomingSIP = new String(buffer, 0, packet.getLength());

                break;
            }
            logger.info("Incoming SIP is \n" + incomingSIP);



            String[] lines = incomingSIP.split("\r\n");
            SipFactory sf =  new SipFactory(lines,1124);
            ParseContract parseContract = new ParseContract(incomingSIP);
            String remoteSIPIP = parseContract.getRemoteSIPIp();
            int remoteSIPPort = parseContract.getRemoteSIPPort();
            String remoteRTPPort = Communication.getRemoteRTPPort(incomingSIP);
            logger.info("Generating response SIP ...");
            responseOkForInvite = sf.getOkForInvite();

            // SIP/2.0 200 OK
            logger.info("Sending response SIP to " + remoteSIPIP + ":" + remoteSIPPort);
            Communication.sendSIP(responseOkForInvite, remoteSIPIP, remoteSIPPort);

            // TODO sikeh: wait for ACK from client, then establish RTP
//            TimeUnit.MILLISECONDS.sleep(50);

            AVTransmit2 rt = new AVTransmit2(new MediaLocator("file:./works.wav"), remoteSIPIP, String.valueOf(remoteRTPPort), null);
            rt.start();
            logger.info("RTP to " + remoteSIPIP + ":" + remoteRTPPort);


            try {
                Thread.currentThread().sleep(6000);
            } catch (InterruptedException ie) {
            }

            // Stop the transmission
//            rt.stop();


        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }

    }
}
