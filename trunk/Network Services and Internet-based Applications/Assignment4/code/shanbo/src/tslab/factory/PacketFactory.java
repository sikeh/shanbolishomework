package tslab.factory;

import jpcap.packet.Packet;
import jpcap.packet.IPPacket;
import tslab.exception.WrongInputPacketException;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 11:24:14 PM
 */
interface PacketFactory {
    public IPPacket toClient(IPPacket ipPacket) throws WrongInputPacketException;
    public IPPacket toServer(IPPacket ipPacket) throws WrongInputPacketException;

}
