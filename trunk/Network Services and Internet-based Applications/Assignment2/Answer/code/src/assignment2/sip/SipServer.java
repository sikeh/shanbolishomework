package assignment2.sip;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.List;

/**
 * User: Shanbo Li
 * Date: Apr 13, 2008
 * Time: 2:59:40 PM
 *
 * @author Shanbo Li
 */
public class SipServer {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        try {
            int port = 5060;

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
                dsocket.receive(packet);

                // Convert the contents to a string, and display them
                String msg = new String(buffer, 0, packet.getLength());
                sb.append(msg);
                break;
//                System.out.print(msg);

//                if (msg.contains("a=direction")) break;
                // Reset the length of the packet before reusing it.
//                packet.setLength(buffer.length);
            }
            String s = sb.toString();
            String[] lines = s.split("\r\n");
            System.out.println(sb.toString());
        } catch (Exception e) {
            System.err.println(e);
        }

    }
}
