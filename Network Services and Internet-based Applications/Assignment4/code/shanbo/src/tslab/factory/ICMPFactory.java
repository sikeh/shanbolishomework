package tslab.factory;

import jpcap.packet.Packet;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import tslab.exception.WrongInputPacketException;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 9:30:16 PM
 */
public class ICMPFactory implements PacketFactory {

    public IPPacket toClient(IPPacket ipPacket) throws WrongInputPacketException {
        ICMPPacket inPacket;
        ICMPPacket outPacket = null;
        if (ipPacket instanceof ICMPPacket){
            inPacket = (ICMPPacket)ipPacket;
        }   else {
            throw new WrongInputPacketException("Not a ICMP packet");
        }







        return outPacket;
    }

    public IPPacket toServer(IPPacket ipPacket) throws WrongInputPacketException {
        //TODO: complete this method.
        throw new UnsupportedOperationException("Not a ICMP packet");
    }
}
