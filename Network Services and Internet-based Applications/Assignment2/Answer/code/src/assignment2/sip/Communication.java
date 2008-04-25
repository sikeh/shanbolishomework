package assignment2.sip;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * Date: Apr 19, 2008
 * Time: 11:01:13 PM
 *
 * @author Shanbo Li
 */
public class Communication {
    public static final Logger logger = Logger.getLogger(Communication.class.getName());

    public static void sendSIP(String sip, String ip, int port) {
        try {

            byte[] message = sip.getBytes();

            InetAddress address = InetAddress.getByName(ip);

            DatagramPacket packet = new DatagramPacket(message, message.length,
                    address, port);

            DatagramSocket dsocket = new DatagramSocket();
            dsocket.send(packet);
            dsocket.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }

    }



    /**
     * @param sip
     * @return Remote RTP port or null
     */
    public static String getRemoteRTPPort(String sip) {
        String[] lines = sip.split("\r\n");
        String[] mField = null;

        for (String line : lines) {
            if (line.startsWith("m=")) {
                mField = line.split(" ");
                return mField[1];

            }
        }
        return null;
    }
}
