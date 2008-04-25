package assignment2.sip;


import assignment2.audio.RtpTransmit;

import javax.media.MediaLocator;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


/**
 * User: Shanbo Li
 * Date: Apr 13, 2008
 * Time: 2:59:40 PM
 *
 * @author Shanbo Li
 */
public class SipServer {
    public static final Logger logger = Logger.getLogger(SipServer.class.getName());

    public static void init(String sipUser, String sipInterface, int sipPort) {
        String incomingSIP;
        SipParser sipParser;
        MessageType incomingMessageType;
        Map<String, ConnectHandler> handlers = new HashMap<String, ConnectHandler>();
        try {
            int port = sipPort;

            DatagramSocket dsocket = new DatagramSocket(sipPort, InetAddress.getByName(sipInterface));
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
                sipParser = new SipParser(incomingSIP);
                String callId = sipParser.getCallId();
                String to = sipParser.getTo();
                if (incomingMessageType.equals(MessageType.INVITE)) {
                    ConnectHandler handler = new ConnectHandler(incomingSIP);
                    if (!to.equals(sipUser)) {
                        handler.sendNotFound();
                    } else if (!handlers.containsKey(callId)) {
                        handler.answer();
                        handlers.put(callId, handler);
                    }
                } else if (incomingMessageType.equals(MessageType.BYE)) {
                    if (handlers.containsKey(callId)) {
                        handlers.get(callId).endCall();
                    }
                }
            }


        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }

    }
}
