package tslab.factory;

import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import tslab.exception.WrongInputPacketException;
import tslab.util.ICMPSessionMapping;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 9:30:16 PM
 */
public class ICMPFactory implements PacketFactory {

    List <ICMPSessionMapping> sessions = new ArrayList<ICMPSessionMapping>();

    private final static ICMPFactory instance = new ICMPFactory();

    private ICMPFactory (){

    }

    public static ICMPFactory getInstance(){
        return instance;
    }

    /**
     * Produce a packet <b>to server</b> according the incoming packet from client.
     * @param ipPacket the packet which comes from client
     * @return a packet which will be send <b>to server</b> according the incoming packet from client.
     * @throws WrongInputPacketException incoming packet is not a ICMP packet.
     */
    public IPPacket toServer(IPPacket ipPacket) throws WrongInputPacketException {
        ICMPPacket inPacket;
        ICMPPacket outPacket = null;
        if (ipPacket instanceof ICMPPacket){
            inPacket = (ICMPPacket)ipPacket;
        }   else {
            throw new WrongInputPacketException("Not a ICMP packet");
        }

        


        
        return outPacket;
    }

    /**
     * Produce a packet <b>to client</b> according the incoming packet from server.
     * @param ipPacket the packet which comes from server
     * @return a packet wich will be send <b>to client</b> according the incoming packet from server
     * @throws WrongInputPacketException incoming packet is not a ICMP packet.
     */
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
}
